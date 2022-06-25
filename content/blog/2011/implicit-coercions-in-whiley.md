---
date: 2011-07-21
title: "Implicit Coercions in Whiley"
draft: false
---

The issue of [implicit coercions](http://wikipedia.org/wiki/Type_conversion) in Whiley is proving to be a particularly thorny issue.  The following motivates why (I think) coercions make sense:

```whiley
real f(int x, real y):
    return x + y

real g(int x, int y):
    return f(x,y)
```

I believe the above should compile without error.  However, this requires an implicit coercion from `int` to `real` in several places.  Some statically typed programming languages (notably [ML](http://wikipedia.org/wiki/ML_(programming_language))) simply don't perform *any* implicit coercions.  Instead, they require explicit coercions in the form of type casts.  Under this model, the above code would be:

```whiley
real f(int x, real y):
    return real(x) + y

real g(int x, int y):
    return f(x,real(y))
```

To me, this seems rather cumbersom and, *when it's clear from the context*, I want the compiler to do this for me.

So, what coercions could we support in Whiley?  Here's a taster:
   * `int` --> `real`

   * `{int x, int y}` --> `{int y}`

   * `[int]` --> `{int}`

   * `{int->int}` --> `{int,int}`

   * `[string]` --> `{int->string}`


Unfortunately, whilst this looks all good on the surface, there are some tricky cases.  Here's one example:

```whiley
define Rec1 as { int x, real y }
define Rec2 as { real x, int y }
define Rec12 as Rec1 | Rec2

int f(Rec12 r):
   if r is Rec1:
       return r.x
   else:
       return r.y

int test():
   z = {x: 1, y: 1} // z has type {int x, int y}
   return f(z)
```

The problem here is that we cannot determine whether to coerce `z` to `Rec1` or `Rec2`.  I guess we should report an ambiguous coercion error.  Which immediately raises the question of how, in the general case, I detect this.