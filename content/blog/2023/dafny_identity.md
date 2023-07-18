---
date: 2023-07-17
title: "Proving a Beautiful Identity in Dafny"
draft: true
#metaimg: "images/2023/DafnyAbove_Preview.png"
#metatxt: "Dafny is a programming language which often genuinely amazes me."
#twitter: "https://twitter.com/whileydave/status/1673926723568832513"
#reddit: ""
---

Recently, I came across [a tweet](https://twitter.com/Mathinity_/status/1679451151517859841?s=20)
about the following identity:

{{<img class="text-center" src="/images/2023/DafnyIdentity_eg1.png" width="454px" alt="Illustrating a mathematical identity which relates the square of the sum from 1 to n with the sum from 1 to n of cubes.">}}

This seemed quite surprising to me, though the tweet's thread included
a number of proofs.  We can check the first few values easily for
ourself:

{{<img class="text-center" src="/images/2023/DafnyIdentity_eg2.png"
width="454px" alt="Illustrating the first three evaluations of the mathematical identity at x=1, x=2 and x=3.">}}

So, the thought occurred: _can we prove this identity with Dafny?_

## Setup

Before we can prove the identity we need to first translate it into
Dafny.  This is actually pretty straightforward, and we can translate
the left-hand side of our identity as follows:

```Whiley
function Sum3(n:nat) : nat 
requires n > 0 {
  if n == 1 then 1 
  else 
     Sum3(n-1) + (n*n*n)
}
```

This describes the sum of cubes using a recursive function, which
works out well.  For the right-hand side, we'll use the following:

```Whiley
function Sum(n:nat) : nat 
requires n > 0 {
  if n == 1 then 1
  else 
    Sum(n-1) + n
}
```

This describes the sum from `1` to `n`, which can then be squared.  We
can now express the identity as follows:

```Whiley
lemma Identity(n:nat) 
requires n > 0 
ensures Sum(n) * Sum(n) == Sum3(n) 
{
   ...
}
```

Here, the precondition `n > 0` is required for both `Sum(n)` and
`Sum3(n)` to make sense.  Furthermore, the `...` is where our proof
goes (more on this below).

## Proof

At this point, I'll jump right in and show Dafny the proof I came up
with:

```Whiley
lemma Identity(n:nat) 
requires n > 0 
ensures Sum(n) * Sum(n) == Sum3(n) 
{
  if n != 1 {
    var m := n-1;
    assert Sum3(n) == Sum(m)*Sum(m) + n*n*n;
    assert Sum(n)*Sum(n) == Sum(m)*Sum(m) + n*n*n;
} }
```

This proof probably seems quite strange (certainly, it does to me) so
let us unpack it now.  It is worth noting that I did not just write
this out first time and get it.  In fact, I spent several hours
playing around with various intermediate forms before finally arriving
at what we have above.  Specifically, it took me a while to realise
`Sum(n)*Sum(n) == Sum(m)*Sum(m) + n*n*n`, and this was key (I actually
didn't look at the existing proofs beforehand, so I was starting out
from scratch).

First, its quite easy to see that 
