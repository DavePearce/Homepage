---
date: 2022-09-15
title: "Formal Verification of a Token Contract"
draft: true
#metaimg: "images/2022/AuctionContract_Preview.png"
metatxt: "Verifying a token contract in Whiley helps to find problems."
#twitter: ""
# reddit: "https://www.reddit.com/r/rust/comments/uigljf/puzzling_strong_updates_in_rust/"
---

Following on from my previous post on [verifying an auction
contract](/2022/05/17/verifying-an-auction-contract-in-whiley/) in
Whiley, I thought it might be useful to look at a more challenging
example.  A _token contract_ is a very common form of smart contract
which allows someone to create and manage their own currency.  On
Ethereum, token contracts have been standardised in the under
[ERC20](https://docs.openzeppelin.com/contracts/3.x/erc20).

## Overview

A very simple token contract maintains, for each account, a balance of
tokens owned by that account.  Account holders can _transfer_ tokens
to others, but only the contract owner can _mint_ new tokens.  The
following defines the various components maintained by the contract:

```Whiley
// Account balances
map<uint160,uint256> tokens
// Total tokens in circulation
uint256 total
// Contract owner
uint160 owner
```

Explicitly maintaining the total number of tokens in circulation may
seem unnecessary (i.e. because it can be computed from the map).
However, in the context of a smart contract it is useful to avoid
such computation (as this can become prohibitively expensive).

Consider the following implementation of `transfer()` which includes
an incomplete specification of what is _required_ for it to execute
(otherwise it _reverts_) along with the properties that it _ensures_.

```Whiley
// Transfer some amount of tokens from one account to another.
method transfer(uint160 to, uint256 value)
// (1) Ensure sufficient funds in source account
requires tokens[msg::sender] >= value
// (2) Ensure sender balance decreased
ensures tokens[msg::sender] == old(tokens[msg::sender]) - value
// (3) Ensure target balance increased
ensures tokens[to] == old(tokens[to]) + value:
   tokens[msg::sender] = tokens[msg::sender] - val
   tokens[to] = tokens[to] + val
```

As expected, the transfer cannot complete unless the account holder
has sufficient funds (item `(1)` above).  Likewise, upon completion,
the account sender's balance must have _decreased_ (item `(2)`) and,
accordingly, the destination account balance must have _increased_
(item `(3)`).  The power of a formal verification tool like Whiley is
that we can statically check whether or not our implementation meets
this specification.  In fact, attempting to verify the above in Whiley
will immediately raise some errors:

   1. **(Integer Overflow)**.  Our implementation above suffers from
      an _integer overflow_ whereby the value sent overflows target's
      balance.  Such flaws are certainly exploitable (and have been in
      the past).
   2. **(Logic Error)**.  Our specification also suffers from a more
      subtle form of _logic error_.  The problem arises when
      `msg::sender == to` as, in such case, items `(2)` and `(3)`
      cannot simultaneously hold for the same account!
   
To resolve these issues and allow the method to pass verification, we
must extend the specification with two more requirements:

```Whiley
// (4) Prevent overflow in target account
requires tokens[to] + value <= MAX_UINT256
// (5) Cannot transfer to myself!
requires msg::sender != to
```

The first of these requirements simply prevents an overflow from
occuring, whilst the latter represents one way of fixing the
specification (though is not the only way).

```Whiley
// Mint new coins into a given target uint160
method mint(uint160 to, uint256 value)
// Only the owner can mint new coins
requires msg::sender == owner
// Prevent overflow in target account
requires tokens[to] + value <= MAX_UINT256
// Prevent overflow of total
requires total + value <= MAX_UINT256
// Ensure target balance increased
ensures tokens[to] == old(tokens[to]) + value:
   tokens[to] = tokens[to] + value
   total = total + value
```

## Contract Invariant

```Whiley
public property sum(map<uint256> tokens, uint i) -> (int r)
requires i >= 0 && i <= |tokens|:
    if i == |tokens|:
        return 0
    else:
        return tokens[i] + sum(tokens,i+1)
```

```Whiley
function lemma_1(map<uint256> xs, map<uint256> ys, uint i)
// Arrays must have same size
requires |xs| == |ys|
// Index must be within bounds
requires i >= 0 && i <= |xs|
// Everything beyond this is the same
requires all { k in i..|xs| | xs[k] == ys[k] }
// Conclusion
ensures sum(xs,i) == sum(ys,i):
   //
   if i == |xs|:
      // base case
   else:
      lemma_1(xs,ys,i+1)

// Index i identifies position within the two arrays which differ.
// Index j is current index through arrays (starting from zero).
function lemma_2(map<uint256> xs, map<uint256> ys, uint160 i, uint j, int d)
// Arrays must have same size
requires |xs| == |ys|
// Indices must be within bounds
requires j >= 0 && j <= i && i < |xs|
// Everything else must be same
requires all { k in 0..|xs| | k == i || xs[k] == ys[k] }
// Ith element must have increased
requires xs[i] == ys[i] + d
// Conclusion
ensures sum(xs,j) == sum(ys,j) + d:
    //
    if j < i:
        lemma_2(xs,ys,i,j+1,d)
    else:
        lemma_1(xs,ys,i+1)      
```
