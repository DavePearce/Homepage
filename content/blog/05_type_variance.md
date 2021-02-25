---
date: 2021-02-01
title: "Understanding Generic Type Variance (in Whiley)"
draft: true
#metaimg: "images/2021/DeadlockDetection_Preview.png"
#metatxt: "An algorithm of mine is being used in the Abseil C++ library for dynamic deadlock detection.  So, I thought I would give an overview of how it works."
#twitterimgalt: "Illusstrating a partial ordering of mutexes"
#twittersite: "@whileydave"
#twitter: "https://twitter.com/whileydave/status/1352366798448910336"
#reddit: "https://www.reddit.com/r/cpp/comments/l6hqfi/understanding_deadlock_detection_in_abseil/"
---

For languages which support [generic
types](https://en.wikipedia.org/wiki/Parametric_polymorphism), an
important question is deciding whether or not a type `C<T>` is a
_subtype_ of another related type `C<S>`.  Since Whiley was recently
extended to support generic types, this was a question I faced.  Let's
start by thinking about subtyping in Whiley:

```[whiley]
type nat is (int n) where n >= 0
```
This defines a type `nat` as a _subtype_ of `int`, meaning we can assign a `nat` to an `int` (but not the other way around).  This makes sense as every value of `nat` is an `int` but not every `int` is a `nat` (e.g. `-1`).

### Example 1
Let's add a simple generic type to illustrate:

```
type Box<T> is { T item }
```

_The question arising is whether or not `Box<nat>` is a subtype of `Box<int>`?_ Well, yes, this makes total sense!  For example, the following is allowed:

```
Box<nat> b1 = {item:0}
Box<int> b2 = b1
```

In contrast the following is (quite rightly) not allowed:

```
Box<int> b1 = {item:-1}
Box<nat> b2 = b1
```

The above is not allowed because `{item:-1}` is clearly not a valid instance of `Box<nat>`.

### Example 2

Now, let's add another simple generic type:

```
type Pred<T> is function(T)->(bool)
```

Here, `Pred<int>` represents a lambda which accepts an `int` and
returns an `int`.  _So, is `Pred<nat>` a subtype of `Pred<int>`?_  This is an interesting question.  Let's create a function to illustrate:

```
function f_n(nat x) -> (bool r):
    ...
```

I've left out the function body here, since it isn't important.  If `Pred<nat>` a subtype of `Pred<int>`, then the following should be allowed:

```
Pred<int> p = &f_n
bool y = p(-1)
```

(Here, `&f_n` returns a reference to function `f_n()` and has type `Pred<nat>`) 

_But, isn't there a problem here?_  Yes, there is!  If `Pred<nat>` is a subtype of `Pred<int>` then (as illustrated above) we can call `f_nat(-1)` --- _which does not make sense_.

### Recap

In summary, `Box<nat>` should be a subtype of `Box<int>`, but `Pred<nat>` should *not* be a subtype of `Pred<int>`.  _So, for a given type `C<T>`, how can we tell which way around it is?_  Good question! We need some theory here:

   * **(Covariance)** If `C<T>` is a subtype of `C<S>` whenever `T` is
a subtype of `S`, we say that `T` is _covariant_ in `C<T>`.

   * **(Contravariance)** In
contrast, if `T` is _contravariant_ in `C<T>`, then `C<T>` is a
subtype of `C<S>` whenever `T` is a _supertype_ of `S`.

   * **(Invariance)** Finally, we can say that `T` is _invariant_ in
`C<T>` meaning that (like Java without wildcards) `C<T>` is _never_ a
subtype of `C<S>` (unless `T=S`).

From this, it follows that `Box<T>` is _covariant_ in `T`, whilst `Pred<T>` is _contravariant_ in `T`.  _But how do we know this?_  Well, roughly speaking, it really stems from the fact that `function(T)->(S)` is _contravariant_ in `T` and _covariant_ in `S`.  Therefore, any generic type containing a `function` type is constrained accordingly.  For example:

```
type BoxFn<T> is {
  function test(T)->(bool),
  ...
}
```
The presence of field `test` imposes a constraint that `BoxFn<T>` cannot be _covariant_ in `T`.  However, it can still be contravariant --- but this depends on what other fields it contains.  For example, the following variation is contravariant in `T`:
```
type BoxFn<T> is {
  function test(T)->(bool),
  int value
}
```
In contrast, the following is _invariant_ in `T`:
```
type BoxFn<T> is {
  function test(T)->(bool),
  T value
}
```
The reason this is invariant is that the field `T value` imposes a constraints that `T` cannot be contravariant.  Combining the constraints from both fields leaves only one possibility!

### Conclusion

Whiley [follows
Rust](https://rustc-dev-guide.rust-lang.org/variance.html) in adopting
_definition-site variance_ for generic types which (for example)
differs from the approach taken in Java.  In contrast, Java supports
use-site variance through wildcards.

### What about other languages?

Perhaps surprisingly,
different languages take different approaches to this question, for
example:

  * **(Java)**.  Java is quite strict as it does not allow subtyping
like this (unless we use wildcards).  For example, `List<Integer>` is
not a subtype of `List<Number>`.  However, `List<Integer>` is a
subtype of `List<? extends Number>` (with some limitations imposed).

  * **(Rust)**.  Rust is (in some ways) more flexible than Java, as it
      allows variance for traits.  For example, `Vec<String>` can be a
      subtype of `Vec<T>` (e.g. if `T` is bounded by trait
      `ToString`).  **(THIS IS WRONG AS CAN SIMULATE THIS IN JAVA)**

## References

   1. _Taming the Wildcards: Combining Definition and Use-Site Variance_, J. Altidor, S. Huang and Y Smaragdakis.  In _PLDI'11_.  [PDF](https://yanniss.github.io/variance-pldi11.pdf)


