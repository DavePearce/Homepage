---
date: 2010-09-22
title: "On Flow-Sensitive Types in Whiley"
draft: true
---

In the ensuing months since the previous release of Whiley, I have been working away on a major rewrite of the compiler.  This is now almost ready, at last!  One of the "executive decisions" I made recently, was to move away from a declared variable model to a completely [flow-sensitive](http://wikipedia.org/wiki/Data-flow_analysis) typing model.  To understand this, consider the following program written in the old version of Whiley:

```whiley
[int] f(int y):
   int x = y
   x = [1,2,3,x]
   return x
```

Here, variable `x` is declared to have type `int`, making the subsequent assignment of `[1,2,3,x]` a type error.  In the new version of Whiley, we don't declare variables at all, so this just becomes:

```whiley
[int] f(int y):
   x = y
   x = [1,2,3,x]
   return x
```

Here, the type of `x` after the first assignment is `int`, whilst after the second assignment it's `[int]`.  There is no type error like before and, in fact, the type of a variable is now free to change at will.  Observe that the parameters and return value are still typed, since these constitute part of the function's specification.

This approaching to typing, which I call *flow-sensitive typing*, is rather nice and makes Whiley feel more like a dynamically typed language.  The reason for the switch  arose from me writing some non-trivial benchmarks in Whiley, and finding that type declarations really get in the way.

Some neat things happen when you start defining types in different places.  For example:

```whiley
real f(int y):
   if y > 1:
      x = y
   else:
      x = 2.0
   return x
```

Here, the type of `x` after the assignment from `y` is `int`; similarly, after the other assignment it's `real`.  Then, after the if-statement it's also `real` as this is the [least upper bound](http://wikipedia.org/wiki/supermum) of `int` and `real`.  A more interesting case is this:

```whiley
define intList as int | [int]

intList f(int y):
   if y > 1:
      x = y
   else:
      x = [1,2,3]
   return x
```

Here, the type of `x` at the return statement is `int|[int]`, which represents the fact that `x` can hold either an `int` or a `[int]` value at that point.

Anyway, flow-sensitive types add real flexibility whilst also reducing the amount of code --- seems like a win-win to me!  Of course, some will say that it does make reasoning about functions more complex in Whiley.  Whilst I agree this is true to some extent, I don't see it as a major issue.  In particular, Whiley has no notion of global variables and, hence, there is never any confusion regarding [variable scope](http://wikipedia.org/wiki/Scope_(programming)) (unlike e.g. [JavaScript](http://wikipedia.org/wiki/JavaScript) or [Python](http://wikipedia.org/wiki/Python_(programming_language))).