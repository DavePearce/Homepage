---
date: 2022-09-15
title: "Formal Verification of a Token Contract"
draft: false
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

## Method Specifications

Consider the following implementation of `transfer()` which includes
an incomplete specification of what is _required_ for it to execute
(otherwise it _reverts_) along with the properties that it _ensures_.

```Whiley
// Transfer some amount of tokens 
// from one account to another.
method transfer(uint160 to, uint256 val)
// (1) Ensure sufficient funds
requires tokens[msg::sender] >= val
// (2) Ensure sender balance decreased
ensures tokens[msg::sender] == old(tokens[msg::sender]) - val
// (3) Ensure target balance increased
ensures tokens[to] == old(tokens[to]) + val:
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
requires tokens[to] + val <= MAX_UINT256
// (5) Cannot transfer to myself!
requires msg::sender != to
```

The first of these requirements simply prevents an overflow from
occuring, whilst the latter represents one way of fixing the
specification (though not the only way).

The following illustrates our implementation of `mint()` including its
specification:

```Whiley
// Mint new coins into target account
method mint(uint160 to, uint256 val)
// Only the owner can mint new coins
requires msg::sender == owner
// Prevent overflow in target account
requires tokens[to] + val <= MAX_UINT256
// Prevent overflow of total
requires (total + val) <= MAX_UINT256
// Ensure target balance increased
ensures tokens[to] == old(tokens[to]) + val:
   tokens[to] = tokens[to] + val
   total = total + val
```

As before checks are required to protect against overflow, and also to
ensure that only `owner` can mint new tokens.

## Contract Invariant

An interesting observation is that the contract maintains an {{<wikip
page="Invariant">}}invariant{{</wikip>}} over its storage state.
Specifically, that `total` matches the number of tokens distributed to
all account holders.  Using Whiley, we can _verify_ this invariant
actually holds.  

Roughly speaking, to make this work we define `sum(map<uin160,uint256>
tokens)` as a property which sums the tokens from all accounts in the
map.  Using our property, we can then extend the specification for
`transfer()` as follows (and similarly for `mint()`):

```Whiley
// Transfer some amount of tokens
// from one account to another.
method transfer(uint160 to, uint256 val)
...
// (2) Invariant holds on entry
requires sum(tokens) == total
...
// (5) Invariant holds on exit
ensures sum(tokens) == total:
   ...
```

What remains is to _verify_ this property holds.  In fact, this
presents some challenges for the verifier used in Whiley, and requires
some additional hints (in the form of lemmas).

## Conclusion

We have demonstrated how a verification tool like Whiley can be used
to verify key properties of our contracts hold.  Since Whiley does not
yet compile to Ethereum bytecode, this remains a proof-of-concept.
Still, the value from doing this should hopefully be apparent!
