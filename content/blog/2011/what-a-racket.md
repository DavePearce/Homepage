---
date: 2011-06-03
title: "What a Racket!"
draft: false
---

The other day I was listening to [this podcast](http://twit.tv/floss167) over at [FLOSS weekly](http://twit.tv/FLOSS).  It was an interview was with Matthew Flatt about the [Racket language](http://racket-Lang.org) (formerly [PLT Scheme](http://wikipedia.org/wiki/PLT_Scheme)).  The language is a Lisp dialect which was primarily designed for teaching, and subsequently used as a research platform.

Anyway, the thing that is most interesting to me is not Racket *per se*, but rather *Typed Racket* ([see here](http://docs.racket-lang.org/ts-guide/index.html)). This is a dialect of Racket which adds static typing (i.e. Racket is untyped by default).

There's a [really good paper](http://www.ccs.neu.edu/racket/pubs/icfp10-thf.pdf) discussing the issues faced in Typed Racket.  The key thing is that they want to provide a type system which is as flexible as possible.  This is to allow typing the miriad of structures which arise in dynamically typed languages.  Here's a choice quote:
> A type system for an untyped language must accommodate the existing programming idioms in order to keep the cost of type enrichment low.

Here, *type enrichment* refers to the process of taking an untyped program and typing it:
> Put positively, the ideal typed sister language requires nothing but the addition of type specifications to function headers, structure definitions, etc.

As we all know, in an untyped language, one can do a whole bunch of things not normally allowed in a statically typed language.  For example, assigning different types to a variable at different points in a method; or, assuming a variable has a specific type only if some other variable is e.g. greater than zero.  In your typical statically typed language, a variable must have a single static type for its duration.  Even in languages with [type inference](http://wikipedia.org/wiki/type_inference), such as [C#](http://wikipedia.org/wiki/C#), a single type is inferred based on the first assignment.

Typed Racket adopts an approach the authors call *occurrence typing,* which allows type tests to refined the types of variables:
> when the expression `(and (number? x) (> x 100))` is `true`, the type system should know that `x` is a number, but `x` might or might not be a number when the expression is `false`, since it might be `97` or `"Hello"`

This is all remarkably similar to the *flow-sensitive* typing used in Whiley.  However, Racket is a [functional language](http://wikipedia.org/wiki/Functional_programming) with [single-assignment semantics](http://en.wikipedia.org/wiki/Assignment_%28computer_science%29#Single_assignment), whilst Whiley is imperative.  So, in Racket,variables are never given *completely different* types, since they are never assigned new values.  In contrast, Whiley allows one to assign a variable arbitrary values at any point, and a new type is automatically determined.

Anyway, I really enjoyed the interview over at FLOSS weekly ... well worth a listen!