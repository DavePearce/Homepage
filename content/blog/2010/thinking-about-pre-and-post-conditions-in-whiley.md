---
date: 2010-07-23
title: "Thinking about Pre- and Post-Conditions in Whiley"
draft: false
---

The notion of [pre-](http://wikipedia.org/wiki/precondition) and [post-conditions](http://wikipedia.org/wiki/postcondition) is well understood in the context of software verification.  However, Whiley deviates from the norm by combining them into a single condition.  The following illustrates a simple Whiley function:

```whiley
int f(int x) where x > $ && $ > 0:
return x-1
```

Here, `$` represents the return value.  Whilst this deviation from normal pre- and post-conditions may seem a little unnecessary, there are good reasons for it.  In particular, I want to allow first-class functions (similar to [function pointers](http://wikipedia.org/wiki/function_pointer) in C).  We might define the type of a function as follows:

```whiley
define func as int(int x) where x > $ && $ > 0
```

At this point, we can write functions that accept `func` variables like so:

```whiley
int g(func f, int x):
 return f(x)
```

This is an important feature for enabling various kinds of [polymorphism](http://wikipedia.org/wiki/polymorphism_in_object-oriented_programming) within Whiley.  Using `where` for functions helps keep the `define` statement consistent across both normal data types and function types.

*So, why does this cause some interesting problems?* Well, the problem is that we need to extract the pre- and post-conditions from this, in order to perform the necessary compile-time or run-time checks.  Looking at the first example above, we can draw the following conclusions:

```whiley

// PRECONDITION: x > 1
// POSTCONDITION: x > $ && $ > 0
int f(int x) where x > $ && $ > 0:
 return x-1

```

Extracting the post-condition is, in fact, relatively easy --- we can just identify the (maximum) [connected component](http://wikipedia.org/wiki/connected_component_(graph_theory)) containing `$`.  However, extracting the pre-condition seems harder, since it requires non-trivial reasoning about the condition.  In this case, it's fairly straightforward to apply [Fourier-Motzkin Elimination](http://wikipedia.org/wiki/fourier-motzkin_elimination) on `$` to obtain the desired result.

Other examples seem more tricky, and I'm unsure about them:

```whiley
int f(int x) where g(x) == $:
 return g(x)
```

Here, the question is whether or not `g(x) == $` imposes any constraint upon `x` before the function call (i.e whether it is a pre-condition of some sort). Suppose we had this:

```whiley
int g(int x) where x > 0 && $ > x:
    return x+1
```

This would appear to impose some constraints upon `x`.

Anyway, that's about as far as I've got in my thinking around this problem.  I guess an obvious solution is simply to mandate that any part of the condition which involves `$` is strictly the post-condition, and cannot be considered part of the pre-condition.  Whilst this would resolve the issue, it has slightly non-intuitive semantics in some cases (such as the first example).  Certainly, one can always rewrite the conditions to make everything explicit as would be required, but that seems less than ideal.
