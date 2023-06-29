---
date: 2023-06-27
title: "Programming Languages Going Above and Beyond"
draft: false
metaimg: "images/2023/DafnyAbove_Preview.png"
metatxt: "Dafny is a programming language which often genuinely amazes me."
#twitter: ""
#reddit: ""
---

Having been a programmer for a long time now, I have experienced my
fair share of programming languages.  What strikes me the most is that
programming languages have not improved much over the years.  Java,
for example, has certainly improved from when I started using it in
the mid-nineties --- but only in pretty minor ways.  We still get
buffer overflows and integer overflows.  The compiler still cannot
tell when our loops will terminate (yes, this is possible).  Aliasing
is still a complete unbridled mess.  Even Rust, my favourite language
du jour, only offers a minor improvement on the status quo.  These are
not stop-the-world everything has changed kinds of improvements.

Still, big improvements are possible.  I now use, on a daily basis, a
language ([Dafny](https://github.com/dafny-lang/dafny/)) which often
genuinely amazes me.  I'll admit, its not super easy to use and
perhaps not ready yet for mainstream --- but, it gives a glimpse of
what is possible.  Dafny's ability to statically check critical
properties of your program goes well beyond what mainstream languages
can do (that includes you, Rust).  Here's a simple example:

```dafny
function Gcd(a:nat, b:nat) : (g:nat,x:int,y:int)
// Bezout's identity
ensures (a*x)+(b*y) == g
{
    if a == 0 then (b,0,1)
    else
        var (g,x,y) := Gcd(b%a,a);
        (g,y-((b/a)*x),x)
}
```

This is a recursive implementation of [Euclid's extended GCD
algorithm](https://en.wikipedia.org/wiki/Extended_Euclidean_algorithm)
(i.e. an integral part of modern cryptography, including
[RSA](https://en.wikipedia.org/wiki/RSA_(cryptosystem)) and
[ECC](https://en.wikipedia.org/wiki/Elliptic-curve_cryptography)).
Using the `ensures` clause I've specified a
[postcondition](https://en.wikipedia.org/wiki/Postcondition) --- that
is, a property that should always hold of the return value.  This
particular postcondition corresponds to [Bézout's
identity](https://en.wikipedia.org/wiki/B%C3%A9zout%27s_identity).
That is, _a theorem about numbers proved by Étienne Bézout in the 18th
century_.  The amazing thing is that, in order to check my program is
correct, Dafny has reproved this theorem for me.  

## Bits and Bytes

The above example was taken from our [DafnyEVM
codebase](https://github.com/Consensys/evm-dafny/blob/63a9da2335634572bfb1dcf616c7eda081bf7d8f/src/dafny/util/int.dfy#L185).
Here's [another
example](https://github.com/Consensys/evm-dafny/blob/63a9da2335634572bfb1dcf616c7eda081bf7d8f/src/dafny/util/int.dfy#L254)
which is, perhaps, more concrete:

```dafny
function ToBytes(v:nat) : (r:seq<u8>)
ensures |r| > 0 {
  // Extract the byte
  var byte : u8 := (v % 256) as u8;
  // Determine what's left
  var w : nat := v/256;
  if w == 0 then [byte]
  else
    ToBytes(w) + [byte]
}
```

The above is a straightforward (recursive) function for converting an
arbitrary-sized unsigned integer (`nat`) into a sequence of one or
more bytes (following a big endian representation).  Here, `ToBytes()`
has an `ensures` clause clarifying it always returns a non-empty
sequence.  _Dafny checks at compile time that this is always true_.

Now, here is a function taking us in the opposite direction:

```dafny
function FromBytes(bytes:seq<u8>) : (r:nat) {
  if |bytes| == 0 then 0
  else
    var last := |bytes| - 1;
    var byte := bytes[last] as nat;
    var msw := FromBytes(bytes[..last]);
    (msw * 256) + byte
}
```

There isn't anything too special going on here (yet).  But, now the
magic begins!  What we want to know is that these two functions are
"opposites" of each other.  We can do this like so:

```
lemma LemmaFromToBytes(v: nat)
ensures FromBytes(ToBytes(v)) == v {
 // Dafny does all the work!
}
```

This might not seem like much, but it represents something you cannot
easily do in other languages.  This `lemma` asks Dafny to check that,
for any possible `nat` value `v`, it always holds that
`FromBytes(ToBytes(v)) == v`.  _Again, Dafny checks this at compile
time --- no testing required!_

Finally, an interesting puzzle remains.  For an arbitrary sequence
`bs` of bytes, does `ToBytes(FromBytes(bs)) == bs` always hold?
Again, Dafny can answer this for us almost immediately.  If
you're interested to know the answer, take a look at the [lemma we
came up
with](https://github.com/Consensys/evm-dafny/blob/63a9da2335634572bfb1dcf616c7eda081bf7d8f/src/dafny/util/int.dfy#L310).
