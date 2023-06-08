---
date: 2023-06-08
title: "Programming Languages going Above and Beyond"
draft: true
#metaimg: ""
#metatxt: ""
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
not stop-the-world everything has changed kind of improvements.

Still, big improvements are possible.  I now use, on a daily basis, a
language ([Dafny](https://github.com/dafny-lang/dafny/)) which often
genuinely amazes me.  I'll admit, its not super easy to use and
perhaps not ready yet for mainstream --- but, it gives a glimpse of
what is possible.  Dafny's ability to statically check critical
properties of your program goes well beyond what mainstream languages
can do (that includes you, Rust).  Here's a simple example:

```dafny
function GcdExtended(a: nat, b: nat) : (g:nat,x:int,y:int))
// Bezout's identity
ensures (a*x)+(b*y) == g
{
    if a == 0 then (b,0,1)
    else
        var (g,x,y) := GcdExtended(b%a,a);
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
is, a property that should always hold for the return values.  This
particular postcondition corresponds to [Bézout's
identity](https://en.wikipedia.org/wiki/B%C3%A9zout%27s_identity).
That is, _a theorem about numbers proved by Étienne Bézout in the 18th
century_.  The amazing thing is that, in order to check my program is
correct, Dafny has reproved this theorem for me.  

You might think this is no big deal, that it doesn't really matter to
you, etc.  _Maybe you're right!_  But, if you care
about getting the bugs out of your code, then this matters to you.  
