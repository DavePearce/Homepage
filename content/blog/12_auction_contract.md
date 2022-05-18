---
date: 2022-05-17
title: "Verifying an Auction Contract in Whiley"
draft: false
metaimg: "images/2022/AuctionContract_Preview.png"
metatxt: "Verifying a smart contract in Whiley finds lots of problems."
#twitter: ""
# reddit: "https://www.reddit.com/r/rust/comments/uigljf/puzzling_strong_updates_in_rust/"
---

Since Whiley is a general purpose verification system, I thought it
might be interesting to try and verify a [smart
contract](https://en.wikipedia.org/wiki/Smart_contract).  Smart
contracts are well suited to formal verification tools (like Whiley),
as they are small and typically self-contained.

In this example, we're going to work through a simple _auction
contract_.  The idea is that a _seller_ is auctioning off some item in
the real world, and trying to raise the highest price for it.  My
contract is intentionally simplified so its easier to work through.
For example, the auction ends whenever the seller says it does rather
than at some predefined point in time.

### Version 1

Right, so let's begin.  Here's our first version (which has lots of
problems):

```Solidity
type Auction is {
   address seller,
   address bidder,
   uint256 bid
}

public Auction self = {seller: 0, bidder: 0, bid: 0}

public export method bid()
// New bid must be higher!
requires msg::value > self.bid:
   self.bidder = msg::sender
   self.bid = msg::value

public export method end()
// Only seller can end the auction!
requires msg::sender == self.seller:
   evm::util::transfer(self.seller,self.bid)
```

Here, the seller is identifier by `self.seller` and the current
highest bidder by `self.bidder`, along with their current highest bid.
I've provided a default initialiser for `self` to make Whiley happy
(in practice it would be initialised using a _contract constructor_).
The `requires` clause in `bid()` ensures that any new bid is higher
than the last.  If this precondition is not met when `bid()` is
called, the transaction will
[revert](https://consensys.github.io/smart-contract-best-practices/development-recommendations/solidity-specific/assert-require-revert/#use-assert-require-revert-properly).
Likewise, a `requires` clause is employed to ensure only the seller
can end their auction.

Unfortunately, there are a lot of problems with this auction contract!
Here are some of the things we would want to fix:

   * **(Unreclaimable Bids)**.  When the previous highest bidder is
       bested by another bid, they should be able to reclaim their
       funds.  At the momement, there is no way for them to do this!

   * **(Multiple Endings)**.  The seller can actually end the contract
       more than once.  This presents an attack vector since they may
       be able to claim more funds than are owed to them by calling
       `end()` multiple times.

So, we want to fix our contract whilst, at the same time, _exploiting
Whiley as much as possible to catch problems_.

### Version 2

Right, let's tackle the first problem above.  To do this, we are going
record how much can be reclaimed by a previous bidder.  This is done
asynchronously to prevent previous bidders from causing chaos (such as
trying to block new bids by forcing out-of-gas conditions):

```Solidity
type Auction is {
   address seller,
   address bidder,
   uint256 bid,
   map<uint256> returns
}

public Auction self = { ..., returns: [0; MAX_UINT160+1]}

public export method bid()
requires msg::value > self.bid:
   // Calculate new return
   uint256 nret = self.returns[self.bidder] + self.bid
   // Update amount returnable to previous bidder
   self.returns[self.bidder] = nret
   self.bidder = msg::sender
   self.bid = msg::value

public export method withdraw():
   uint256 amount = self.returns[msg::sender]
   self.returns[msg::sender] = 0
   evm::util::transfer(msg::sender,amount)

public export method end()
requires msg::sender == self.seller:
   evm::util::transfer(self.seller,self.bid)
```

Here, I've used a `map<uint256>` to map addresses to balances.  To
make Whiley happy, I've initialised it with an empty array covering
the entire address space (which effectively models how the EVM
initialises storage).

We can see that when a new bid comes in now, the previous highest
bidder has their funds recorded in `returns` so they can reclaim it
later using `withdraw()`.  _But, there are problems hidden here_.  If
try to verify this contract, Whiley immediately tells us:

```
type invariant may not be satisfied
   uint256 nret = self.returns[self.bidder] + self.bid
                  ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
```

This tells us that an *integer overflow* can occur here which would
lead to an unexpected outcome.  Granted, for the overflow to trigger
it would (in this case) require an impossibly large amount of ether.
But, that's not the point.  Overflows have caused serious problems in
deployed contracts, and we want to avoid them.  Common advice is to
use [safe math
libraries](https://ethereumdev.io/using-safe-math-library-to-prevent-from-overflows/)
for this which automatically revert on overflow.  This is nice, but it
does cost some gas to do the check!  Instead, we can use Whiley to
check the contract for us.

The question is: _how can we prevent this overflow?_ Well, we just
have to ensure that any new bid can be placed into `returns`.  We can
do this using a contract invariant as follows:


```Solidity
type Auction is {
   ...
}
where (returns[bidder] + bid) <= MAX_UINT256
```

Here, `MAX_UINT256` is the maximum value representable by a `uint256`
(i.e. `2^256 - 1`).  This says that, for every highest bidder, there
must be space in `returns` for their bid in the event they get ousted.
Verifying our contract now gives a new error:

```Solidity
type invariant may not be satisfied
   self.returns[self.bidder] = nret
                               ^^^^
```

This makes sense because our invariant won't hold after this
assignment!  That is there wouldn't be space now to store `bid` again
into `returns`.  There are different ways to solve this, but a simple
option is to reset the bid:

```Solidity
   uint256 nret = self.returns[self.bidder] + self.bid
   self.bid = 0
   self.returns[self.bidder] = nret
```

By reseting `self.bid`, the invariant on `returns` holds as we update
it.  Having done this, there is still another error:

```Solidity
type invariant may not be satisfied
   self.bid = msg::value
              ^^^^^^^^^^
```

The issue is that we haven't restricted the new bidder based on their
bidding history.  We can do this with another precondition:

```Solidity
public export method bid()
requires msg::value > self.bid
// Restrict bidder based on their history
requires self.returns[msg::sender] + msg::value <= MAX_UINT256
```

We are close now, but amazingly we still have a problem!  Here is the
error being reported:

```Solidity
type invariant may not be satisfied
   self.bid = msg::value
              ^^^^^^^^^^
```

At this point, I had to scratch my head for a moment _as I thought it
should work_.  But, no --- there is still an overflow bug!  The
problem occurs when the previous highest bidder and the new bidder
_are the same person_.  Again, there are different ways to solve this
but the simplest is to prevent a bidder from besting themselves:

```Solidity
// Bidder cannot best themself
requires msg::sender != self.bidder
// Restrict bidder based on their history
requires self.returns[msg::sender] + msg::value <= MAX_UINT256:
   ...
```

And now it verifies!  At this point, there is more we could do to
tidy things up.  For example, reseting the bid with `self.bid = 0` was
quite ugly and, with care, we can get rid of that.  Likewise, we might
want to double-check that `self.bid` actually _increases_ after
`bid()` is called, etc.

### Version 3

The final issue is preventing multiple auction endings.  For this, we
can use a flag to signal when it has ended:

```Solidity
type Auction is {
   ...
   bool ended
} where (returns[bidder] + bid) <= MAX_UINT256

public export method bid()
// Cannot bid if auction over
requires !self.ended
  ...

public export method end()
// Cannot end auction more than once!
requires !self.ended
// Only seller can end the auction!
requires msg::sender == self.seller:
   // End the auction
   self.ended = true
   // Transfer the winnings
   evm::util::transfer(self.seller,self.bid)
```

What we can see here is that, once `ended` has been set it cannot be
unset.  Thus, no bids can be accepted after this point and, likewise,
`end()` cannot be called again.  Note, `withdraw()` remains untouched
and bidders can continue reclaiming funds well after the auction has
ended.

### Conclusion

If you made it this far then it should be pretty clear that managing
even a simple issue like integer overflow is a tricky business.  Using
tools like Whiley can certainly help!  At the same time, there are
some obvious limitations as well:

   * **Authentication**.  We have required `msg::sender ==
       self.seller` on `end()` to ensure only the seller can end the
       auction.  However, the tool does not tell us to do this and is
       quite happy if we leave it out.  It would be nice if we could
       somehow declare that the `ended` field was _owned_ by `seller`
       (or something similar), such that the tool forced us to
       authenticate.

   * **Endings**.  We have set `ended = true` within `end()` and
       determined that, once set, it cannot be unset.  This conclusion
       was based on a _manual analysis_ of the contract and, again,
       the tool does not prevent bugs from being introduced.  For
       example, we might set `ended = false` by accident elsewhere in
       the contract.  It would be nice if we could specify that
       `ended` cannot be changed once it is `true`.

Finally, [code for the auction
contract](https://github.com/DavePearce/AuctionContract.wy) is
available on GitHub.  Furthermore, I should emphasise that this is
something of a thought experiment on how Whiley could be used.  At
this time, Whiley does not compile to EVM bytecode and has no specific
support for writing smart contracts.  Still, [others have been using
it](https://arxiv.org/abs/2106.14457) for verifying smart contracts so
perhaps I should do something here!