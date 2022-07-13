---
date: 2013-04-21
title: "Iso-Recursive versus Equi-Recursive Types"
draft: false
---

An important component of the Whiley language is the use of {{<wikip page="Recursive_data_type">}}recursive data types{{</wikip>}}.  Whilst these are similar to the {{<wikip page="Algebraic_data_type">}}algebraic data types{{</wikip>}} found in languages like Haskell, they are also more powerful since Whiley employs a {{<wikip page="Structural_type_system">}}structural type system{{</wikip>}}. *So, what does all this mean?* Well, let's look at an example:

```whiley
define IntList as null | {int data, IntList next}
define AnyList as null | {any data, AnyList next}

AnyList f(IntList ls):
    return ls
```

Here, we've got two recursive data types which describe something akin to a linked list. For example, an `IntList` describes a recursive structure made up of zero or more nodes. Each node contains an `int data` field, and a `next` field to access either the next node, or `null` if the end is reached. An `AnyList` is very similar, except that its payload consists of arbitrary data, rather than just integer data (as for `IntList`).

In the above example, we see that the parameter `ls` is returned without an explicit cast. In other words, we know that `IntList` is a subtype of `AnyList`. This is a key difference from standard algebraic data types, where `IntList` and `AnyList` would always be considered distinct (i.e. unrelated) types.

## Subtyping Recursive Types

The ability to have implicit subtyping relationships between recursive data types is a key strength compared with algebraic data types.  At the same time, it also presents a complex algorithmic challenge and numerous approaches have been proposed in the literature.  Previously, I have written extensively on this subject (see e.g. [here](/2011/02/16/minimising-recursive-data-types/), [here](/2011/03/07/implementing-structural-types/) and [here](/2011/08/30/simplification-vs-minimisation-of-types-in-whiley/)).  In fact, there are two broad approaches taken to subtyping recursive data types: *iso-recursive* and *equi-recursive*.  In Whiley, and my previous writings on this topic, I have strictly followed the equi-recursive approach and I would strongly recommend this to anyone developing a recursive type system.

A good account of the iso- versus equi-recursive approaches can be found in [Pierce's excellent book](http://www.cis.upenn.edu/~bcpierce/tapl/) [1].  The key difference between the two approaches is whether the recursion is "implicit" (equi-recursive) or "explicit" (iso-recursive).  In the equi-recursive approach, types are implemented under-the-hood as directed graphs where recursion corresponds to a cycle in the graph (see [here](/2011/02/16/minimising-recursive-data-types/) for an example).  In the iso-recursive approach, special types of the form `μX.T` are used (the so-called "mu" types). In such a type, `X` is a recursive variable used within the body `T`. For example, a mu type corresponding to our `IntList` example is: `μX.(null | {int data, X next})`.

Mu types can be "folded" and "unfolded".  To unfold a type `μX.T` we generate the type `T[X/μX.T]` (that's `T` with `X` replaced by `μX.T`).  For example, unfolding `μX.(null | {int data, X next})` gives `null | {int data, μX.(null | {int data, X next}) next}`. Explicit operators for unfolding and folding are provided for manipulating types where `fold(unfold(T)) = T` holds for any type `T`. In the iso-recursive scheme (and unlike the equi-recursive scheme), a type and its unfolding are distinct and unrelated. To show that one mu type subtypes another, we must first fold/unfold them to have the same recursive structure, after which we can establish the subtyping relation via the so-called "Amber Rule" (see e.g. [1,2] for more on this).  

## Subtyping Iso-Recursive Types

Having considered how subtyping is performed for iso-recursive types, the question is: *for two types which should be related, can we always fold/unfold them to reach a matching recursive structure?* Well, I believe the answer is no.  Here's my informal proof:

```whiley
define LTree as null | { int data, LTree left, LTree right}
define RTree as null | { int data, RTree_b left, RTree right}
define RTree_b as null | { int data, RTree left, RTree right}
```

Now, we have to ask ourselves the question: is there a sequence of fold/unfold operations that will transform `LTree` into `RTree` (i.e. to show that they are equivalent)?  To see why this is impossible, we consider the notion of "balance" (as in *balanced tree*). After any number of fold / unfold operations the `LTree` type will remain balanced; but, for the `RTree` type, it will never be balanced. 

For a more detailed investigation into the expressiveness of iso-recursive types, I'd suggest looking at this [recent paper](http://www.cse.usf.edu/~ligatti/papers/subIsoTR.pdf) [2].

# References

   1. [Types and Programming Languages](http://www.cis.upenn.edu/%7Ebcpierce"), Benjamin C. Pierce. The MIT Press, ISBN 0-262-16209-1.
   
   2. [Completely Subtyping Iso-recursive Types](http://www.cse.usf.edu/~ligatti/papers/subIsoTR.pdf), Technical Report CSE-071012, Jeremy Blackburn Ivory Hernandez Jay Ligatti Michael Nachtigal, University of South Florida.
