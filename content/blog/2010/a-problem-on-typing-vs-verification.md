---
date: 2010-11-14
title: "A Problem on Typing vs Verification"
draft: false
---

Whiley uses a [flow-sensitive type system](/2010/11/09/more-on-flow-sensitive-typing/), and a verification engine to enable [Extended static checking](http://wikipedia.org/wiki/Extended_static_checking).  My desire in designing these two subsystems is to keep them completely isolated.  In particular, such that we can always compile and run a Whiley program with verification turned off.  This requirement stems from the fact that verification of Whiley code is [undecidable](http://wikipedia.org/wiki/Undecidable_problem) and, hence, the theorem prover may fail to give a result at any point.

Thus far, this separation between typing and verification has been implemented successfully.  However, whilst writing a non-trivial program in Whiley recently, I quickly hit upon an issue.  Consider this code:

```whiley
define Var as string
define Num as int
define Expr as Num|Var

Num evaluate(Expr e):
    if e ~= Var:
        return 0 // dummy value
    else:
        return e // int value
```

This is extracted from a simple calculator program.  Anyway, on the surface it looks fine --- the flow-sensitive type system should see that on the `else` branch, variable `e` has type `int`.  Unfortunately, it doesn't.  To understand why, we need to consider the intermediate language (wyil) used in the Whiley Compiler.  This translates the above into (roughly speaking) the following:

```whiley
int evaluate(int|[int] e)
requires e ~= int || all {v in e | v >= 0 || < 1114111}:
    if e ~= [int] && all {v in e | v >= 0 || < 1114111}:
        return 0
    else:
        return e
```

(**note:** 1114111 is the maximum allowable unicode character)

The type system does not know that the `all` component of the `if` condition will always hold if `e~=[int]` holds.  Therefore, it assumes there is a path to the `return e` where `e ~= [int]` holds and, hence, reports a type error.

Only by using the verifier can the compiler figure out that `e~=[int]` can never hold at the final `return` statement.  Thus, we hit on the condrum --- *typing the above requires the verifier*.

One interesting observation, is that we can rewrite the above to avoid the problem altogether:

```whiley
Num evaluate(Expr e):
    if e ~= [int]:
        return 0 // dummy value
    else:
        return e // int value
```

This compiles and type checks without any problem, because there are no constraints involved in the type test.

Anyway, for now, I haven't figured out the best solution to this.  Perhaps, I should just abandon the idea of keeping typing completely separate from verification ... hmmmm
