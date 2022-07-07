---
date: 2011-07-29
title: "A Semantic Interpretation of Types in Whiley"
draft: false
---

An interesting and intuitive way of thinking about a type system is using a _semantic interpretation_.  Typically, a set-theoretic model is used where a type `T` is a subtype of `S` iff every element in the set described by `T` is in the set described by `S`.

## The Semantic Model

The starting point is to define our notion of types `T` and values `V`:

   * `T` ::= `null` | `int` | `any` | `[T]` | `{T1 f1, ... Tn fn}` | `T1 ∨ T2`

   * `V` ::= `null` | `i` | `[V1,...,Vn]` | `{f1: V1, ... f2: V2}`

This is a simplified notion of the types and values found in Whiley.  For example, I've left out sets and function references and ignored recursive types altogether.

We can define our semantic model as follows using an acceptance relation `T` |= `V`, which holds if value `V` is in the set described by type `T`.

   1. `null` |= `null`
   
   1. `any` |= `V`
   
   1. `int` |= `i`, **if** i ∈ _I_ (the set of all integers)
   
   1. `[T]` |= `[V1,...Vn]`, **if** ∀1≤i≤n.[`T` |= `Vi`]
   
   1. `{T1 f1, ..., Tn fn}` |= `{f1: V1, ... fn: Vn}`, **if** `T1` |= `V1`, ... `Tn` |= `Vn`
   
   1. `T1 ∨ T2 ` |= `V`, **if** `T1` |= `V` **or** `T2` |= `V`

**Note:** this model could be made more advanced by supporting {{<wikip page="Subtype_polymorphism#Record_types">}}width subtyping{{</wikip>}} --- but its enough for now.

Finally, we can give a semantic notion of subtyping where `T1` |= `T2` holds if ∀`V`.[`T1` |= `V` **implies** `T2` |= `V`].  In otherwords, `T1` |= `T2` if `T1` is a subtype of `T2`.

## The Subtyping Algorithm
Now that we have an "intuitive" model of what types should mean, we want to compare that against an actual algorithm for subtype testing.  The following pseudo-code outlines the basic algorithm used in Whiley:

```whiley
// Check whether t1 is a subtype of t2
bool isSubtype(Type t1, Type t2):
   // rule 1
   if t2 is any:
       return true
   // rule 2
   else if t1 == t2:
       return true
   // rule 3
   else if t1 is [t3] && t2 is [t4]:
       return isSubtype(t3,t4)
   // rule 4
   else if t1 is {t3 f3, ..., Tn fn} &&
            t2 is {s3 f3, ..., Sn fn}:
       for i in 3..n:
           if !isSubtype(ti,si):
               return false
       return true
   // rule 5
   else if t1 is (t3 ∨ t4):
       return isSubtype(t3,t2) && isSubtype(t4,t2)
   // rule 6
   else if t2 is (t3 ∨ t4):
       return isSubtype(t1,t3) || isSubtype(t1,t4)
   // rule 7
   else:
       return false
```

Thus, for example, `isSubtype(int,any)` holds under rule 1, whilst `isSubtype(int,int ∨ null)` holds by rules 6+2.

## The Question

> _Is the subtyping algorithm sound and complete with respect to our semantic model?_

In some sense, the whole point of the semantic model is to let us ask this question.  We can break this down into two separate questions of _soundness_ and _completeness_:

> **Soundness.** If `isSubtype(T1,T2)` then `T1` |= `T2`.

> **Completeness.** If `T1` |= `T2` then `isSubtype(T1,T2)`.

Considering these questions separately simplifies the problem.  I'm not going to provide any proofs, but it's relatively easy to see that `isSubtype()` is sound.  The more interesting question is whether or not it is complete.

In fact, it turns out that the `isSubtype()` algorithm as given is _not complete_.  A simple counter-example is sufficient to show this.  Let `T1` = `{int ∨ null x}` and `T2` = `{int x} ∨ {null x}` .  Then, `T1` |= `T2`, but `isSubtype(T1,T2)` does not hold.  This is because rule 6 requires `isSubtype(T1,{int x})` and `isSubtype(T1,{null x})` (neither of which hold).

The problem is that `isSubtype()` is not _distributive_ over records.  An interesting question is how we can fix it, but that's a story for another day!

If you're interested in learning more about this, I've worked through the full system in [this paper](/publications/ECSTR10-23.pdf).  Also, the following reference provides a good introduction to semantic subtyping:

   * "**A Gentle Introduction to Semantic Subtyping"**, Giuseppe Castagna and Alain Frisch.  In _Proceedings of the ACM Conference on Principles and practice of declarative programming (PPDP)_, 2005. [[ACM Link](http://portal.acm.org/citation.cfm?id=1069793)][[PDF](http://citeseerx.ist.psu.edu/viewdoc/download?doi=10.1.1.65.8026&rep=rep1&type=pdf)]

