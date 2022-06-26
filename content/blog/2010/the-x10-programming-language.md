---
date: 2010-08-05
title: "The X10 Programming Language"
draft: true
---

[X10](http://x10-lang.org/) is an interesting and relatively new language from IBM being developed as part of [DARPA](http://wikipedia.org/wiki/DARPA)'s [High Productivity Computing Systems](http://www.highproductivity.org/) program.  X10 is designed for high-performance parallel programming using a [partitioned global address space](http://wikipedia.org/wiki/partitioned_global_address_space) model.  To my mind, I see X10 as being a modern [Fortran](http://wikipedia.org/wiki/Fortran) ... but perhaps that's a bit disingenuous.  Anyway, what got me looking at this was the following paper:

*Constrained Types for Object-Oriented Languages*, N. Nystrom, V. Saraswat, J. Parlsberg and C. Grothoff, OOPSLA, 2008. [[DOI](http://doi.acm.org/10.1145/1449764.1449800)] [[PDF](http://ranger.uta.edu/~nystrom/papers/oopsla08.pdf)]

Constrained types (which are a form of [dependent type](http://en.wikipedia.org/wiki/Dependent_type)) are quite interesting to me, since Whiley supports something similar.  A simple example from the paper is this:

```whiley
 class List(length:int}{length >= 0} { ... }
```

This is a *constrained* list type whose constraint states that the length cannot be negative.  I find the notation here is a bit curious.  X10 divides fields up into two kinds: *properties* and *normal fields*.  The distinction is that properties are immutable values, whilst fields make up the mutable state of an object.  Thus, constraints can only be imposed over the properties of a class.  This implies our constrained list cannot have anything added to it, or removed from it. But,  I suppose we can still change the contents of a given cell.

Constraints can also be given for methods, like so:
```whiley
def search(value: T, lo: int, hi: int)
 {0 <= lo, lo <= hi, hi < length}: ...
```
The first question that springs to mind here is: *what can we do inside a constraint?* Obviously, we've already seen properties, parameters and ints being used ... but what else?  In particular, can we call impure methods from constraints?  Unfortunately, I don't have definite answer here.  As far as I can tell, X10 has no strong notion of a [pure function](http://wikipedia.org/wiki/pure_function).  The [spec](http://dist.codehaus.org/x10/documentation/languagespec/x10-latest.pdf) specifically states that X10 functions are "not mathematical functions".  On the other hand,  I haven't seen a single constraint which involves a method invocation, so perhaps *you simply can't call methods/functions from constraints*.  Sadly, the [spec](http://dist.codehaus.org/x10/documentation/languagespec/x10-latest.pdf) is rather brief on this point.

An interesting design choice they've made with X10 is to rely on "pluggable constraint systems", which presumably stems from work on "pluggable type systems" (see e.g. [this](http://lambda-the-ultimate.org/node/1311)):
> The X10 compiler allows programs to extend the semantics of the language with compiler plugins.  Plugins may be used to support different constraint systems.

Now, let's be clear: *i'm not a fan of this*.  The problem is really that the meaning of programs is no longer clearly defined, and relies on third-party plugins which may be poorly maintained, or subsequently become unavailable, etc.  I think the problem is compounded by the following:
> If constraints cannot be solved, an error is reported

To me, this all translates into the following scenario: ***"I download and compile an X10 program, but it fails telling me I need such and such plugin; but, it turns out, such and such author is not maintaining it any more and I can't find it anywhere*."

I'm assuming here *that it will be obvious which plugins you need to compile a given program*.  If not, then you're faced with a real challenge deciding which plugin(s) you need.

Anyway, that's my 2c on X10 ... let's see how it pans out!!