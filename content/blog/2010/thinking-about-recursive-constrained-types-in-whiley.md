---
date: 2010-07-25
title: "Thinking about Recursive Constrained Types in Whiley"
draft: true
---

Whiley supports so-called [Algebraic Data Types](http://wikipedia.org/wiki/algebraic_data_type) for constructing tree-like data structures.  For example, an expression tree might be defined like so:
```define ADD as 1
define SUB as 2
define MUL as 3
define DIV as 4
define binop as {ADD,SUB,MUL,DIV}
define expr as int | (binop op, expr lhs, expr rhs)```
Using this definition for `expr`, we can create a variety of trees representing compound arithmetic expressions.  There is nothing particularly surprising about this so far, as languages like [Haskell](http://wikipedia.org/wiki/haskell_(programming_language)) have similar constructs.

The key difference from other languages is that Whiley supports *constrained types*.  That is, types with specific constraints on them which restrict the values that variables of those types may take.  In the above example, `binop` is an example of a constrained type.  In this particular case, it is not so exciting because you can achieve the same thing with [enumerations](http://wikipedia.org/wiki/enumerated_type) in most languages.

We can refine our example to make it a little more interesting as follows:
```define bexpr as (binop op, expr lhs, expr rhs)
             where !(lhs ~= int && rhs ~= int)
define expr as int | string| bexpr```
This introduces a constraint that prevents both sides of a binary operation from being constants.  We could think of it as an optimisation as, in such a case, we can immediately evaluate the operation to produce a value.  I have introduced the `string` option to represents variables (otherwise, our expression trees would always be constants!).

At this point, things start to get tricky.  Not with the syntax of the language per se, but rather with the details of how we will check all this at [compile time](http://wikipedia.org/wiki/compile_time).  First, let's imagine a simple case:
```expr e = (op:ADD,lhs:1,rhs:(op:SUB,lhs:2,rhs:"x"))```
This builds the expression `1+(2-x)` and assigns it to variable `e`.  To check at compile time that the constraints imposed by `bexpr` are satisfied is relatively straightforward.  We simply "unroll" the recursive constrained type, which introduces the following checks:
```expr e = (op:ADD,lhs:1,rhs:(op:SUB,lhs:2,rhs:"x"))
check !(e.lhs ~= int && e.rhs ~= int)
check !(e.rhs.lhs ~= int && e.rhs.rhs ~= int)```
Both of these checks are fairly straightforward for the theorem prover [wyone](wyone) to discharge.  A harder problem lies with this example:
```define asexpr as bexpr where $.op in {ADD,SUB}
void f(asexpr e1):
    expr e2 = e1```
Obviously, this is a contrived example, but it does make the point.  That is, we need to check that the constraints placed on variable `e2` remain satisfied after the assignment.  Whilst this appears fairly obvious to a human, the machine must follow a process.  In this case, we cannot "unroll" the recursive constrained type as we did before, since `e1` is an arbitrary instance of `asexpr`.

The question is *what can we do?* And, that's about where my thinking is up to right now.  Essentially, it needs some kind of structural induction to show that `asexpr` implies `expr`.  Generally speaking, [SMT solvers](http://wikipedia.org/wiki/Satisfiability_Modulo_Theories) (and other kinds of automated theorem provers) avoid providing induction primitives as this is considered very complex, and more suited to a Human.
## Further Reading   * Constrained Types for Object-Oriented Languages, Nat Nystrom, et al. [[PDF](http://ranger.uta.edu/~nystrom/papers/oopsla08.pdf)]****

   * Constrained Types and their Expressiveness, Palsberg and Smith.  [[PDF](http://citeseerx.ist.psu.edu/viewdoc/download?doi=10.1.1.25.8386&rep=rep1&type=pdf)]****

   * Lambda Calculus with Constrained Types, Val Breazu-Tannen and Albert R. Meyer. [[DOI](http://dx.doi.org/10.1007/3-540-15648-8)]

