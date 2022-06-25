---
date: 2011-06-20
title: "On the Duality of Types: the Ideals versus the Reals"
draft: false
---

I've been working hard over the last few weeks on the next release of Whiley, which should be out soon.  However, I've got myself into a little bit of a pickle over the type system (again).  I want the type system to be simple and easy to use, but at the same time efficient (as I want Whiley to compile for e.g. embedded systems).  As usual, there some trade-offs here!

## Problem Statement
Consider this very simple program:

```whiley
void f({int} xs, {real} ys):
   zs = xs + ys // set union
   // what type does zs have now?
```

The question is: *what type does zs have?* There are at least two options: `{real}` or `{int|real}`.  This issue is really about whether we have a strong distinction between an `int` and a `real`.  In most languages, this is indeed the case.  For example, in Java, an `int` is distinct from a `float`.  That is, we can have a variable of `float` type which has the same value as one of `int` type.  The use of implicit coercions, however, hides this distinction from us.  In Java, this mostly doesn't cause a problem (except for the fact that [implicit coercions in Java can lose precision](http://stackoverflow.com/questions/1293819/why-does-java-implicitly-without-cast-convert-a-long-to-a-float) --- but that's another story).

As another example, consider this short function:

```whiley
int f(real x):
    if x ~= int:
        return x
    else:
        ...
```

The question here is: *should this function be considered syntactically correct or not? *If the type `int` is distinct from `real`, then we should get an "incompatible operands" error (or similar).

So, what's going on here?  Well, this is really about the *ideals* versus the *reals*:
   *  The **ideals** are the idealised (i.e. mathematical) notions of values in the language. In such a world, a value of type `int` also has type `real`.  Conversely, every value of type `real` which is, in fact, an integer must also have type `int` (e.g. 10.2 / 5.1 : `int`). Likewise, a value of type `[T]` also has type `{int->T}` and, hence, the value `{0->"x",1->"y"}` has type `[string]`.  This is very flexible, but makes it difficult to know exactly what type you have --- making it hard for the compiler to optimise.

   * The **reals** are a more pragmatic approach where types more closely reflect their machine implementation; in such case, type `int` is quite separate from type `real` (although implicit coercions can make it seem otherwise).  In such case, `2.0` has type `real`, whilst `2` has type `int`.  Likewise, (e.g. 10.2 / 5.1 : `real`).  This case is perhaps the more traditional approach, and it certainly makes the compiler's job easier.


In Java, this issue is really not an issue.  However, in Whiley, it appears to be more complicated and worth at least considering.
## Option 1 --- Go with the Ideals
This case probably makes the most sense, but it also impacts upon performance (I believe).  Here are a few observations:
   * `{int} + {real}` => `{real}` whilst `{int} & {real}` => `{int}`.

   * If `xs` has type `[real]`, then this holds after `xs[0] = 1`.

   * The comparison `xs == ys` makes sense if e.g. `xs` has type `{real}`, but `ys` has type `{int}`.


All of this sounds quite nice and rosey, and makes heaps of sense.  However, the machine representation of values is more complicated.  For example, suppose we want to represent an `int` with `BigInteger` and `real` with `BigRational`.  Then, consider these examples:

```whiley
any f(int x, int y):
    if x > y:
        y = x
    else:
        y = x / y
    return y

void g(any x):
    if x ~= int:
        out.println("GOT INT")
    else if x ~= real:
        out.println("GOT REAL")
```

**Note:** in the above, `g(1.0)` prints `"GOT INT"`, whilst `g(1.2)` prints `"GOT REAL"`.

Now, we can choose whether or not to enforce the following invariant (**note:** there are other invariants we could choose, this just an example):
> if a variable has static type `int` then it must be a `BigInteger`, otherwise it has type `BigRational`.

The reason for this invariant comes from the type `any`.  That is, if a variable has type `any`, can it be a `BigInteger` or a `BigRational` or both?  The invariant says it can only be a `BigRational`.

Now, suppose we do enforce the invariant.  Then, in the first example above, we must coerce `y` from a `BigInteger` into a `BigRational`; however, the second example is easier since we know `x` cannot be a `BigInteger`.  

Suppose we we don't enforce that invariant, then no coercion is needed in `f()`, but in `g()` variable `x` could be either a `BigInteger` or a `BigRational` and still pass the test `x ~= int` --- meaning we must test for both.

Another interesting case is this:

```whiley
bool f([int] xs, [real] ys):
    return xs == ys
```

Again, if we enforce our invariant then we'll need to convert all elements of `xs` to `BigRational` before making the comparison.  If we don't enforce it, then we may have spurious instances of `BigInteger` in `ys`.

Probably the easiest way to resolve all of these issues regarding representation is to implement `int` and `real` values using the same underlying datatype.  This avoids the need to coerce at all, but may add some overhead.  Alternatively, we can construct our own number hierarchy (i.e. `MyBigInt` and `MyBigReal`) where one can compare across instances of the different classes.

All of these issues are duplicated across the other equivalences in the type hierarchy e.g. `{int->T}` and `[T]`.  However, it's very unlikely we could employ an efficient uniform representation that worked across these different types (JavaScript does something like this, where a list is really just a map).
## Option 2 --- Go with the Reals
The second option is actually the easiest to implement from the compiler perspective, and also the easiest to make efficient and compact.  However, it also ends up with quite weird semantics IMHO.  Considering the same examples from the ideals, this would give:
   * `{int} + {real}` => `{int|real}` whilst `{int} & {real}` => `{int}`.

   * If `xs` has type `[real]`, then after `xs[0] = 1` we have `xs` with type `[int|real]`.

   * The comparison `xs == ys` does **not** makes sense if `xs` has type `{real}`, but `ys` has type `{int}`.


In this case, we can easily implement any value of type int as a `BigInteger`, and any value of type real as a `BigRational`.  For example:

```whiley
any f(int x, int y):
    if x > y:
        y = x
    else:
        y = x / y
    return y

void g(any x):
    if x ~= int:
        out.println("GOT INT")
    else if x ~= real:
        out.println("GOT REAL")
```

In both of these examples, `any` signals that we can have a value of either type `int` or type `real` (or some other type).  The key difference from before is that there's no ambiguity since an `int` is distinct from a `real`.  For example, `g(1.0)` will now print `"GOT REAL"`, whilst `g(1)` will print `"GOT INT"`.

We can try to use implicit coercions to make the distinction between int and real more seamless.  For example:

```whiley
int f(int x, real y):
    if x == y:
        return x
    else:
        return -1
```

In this case, a coercion would be applied from `int` to `real` for variable `x` in the comparison.  However, the proper choice of coercions is not entirely clear to me.  For example:

```whiley
void f({int} xs, {real} ys):
    zs = xs + ys
```

Should we apply a coercion at this point for `xs`?  Or, should we say the type of `zs` is `{int|real}`?   Likewise, what about this case:

```whiley
void f([int] xs):
    xs[0] = 1.2
    ...
```

Do we coerce `xs` into type `[real]` after the assignment, or leave it with type `[int|real]` ?
## Summary
Probably, these issues seem very insignificant.  However, it's important for me to decide what the semantics are in order that I don't have to back track!

In the end, I think option 1 makes the most sense.  I believe I can see a range of fairly efficient implementations, and it has the most natural semantics IMHO.