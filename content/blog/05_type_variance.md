---
date: 2021-02-01
title: "Understanding Generic Type Variance"
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
_subtype_ of another related type `C<S>`.  Perhaps surprisingly,
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

## Kinds of Variance

   * **(Covariance)** If `C<T>` is a subtype of `C<S>` whenever `T` is
a subtype of `S`, we say that `T` is _covariant_ in `C<T>`.

   * **(Contravariance)** In
contrast, if `T` is _contravariant_ in `C<T>`, then `C<T>` is a
subtype of `C<S>` whenever `T` is a _supertype_ of `S`.

   * **(Invariance)** Finally, we can say that `T` is _invariant_ in
`C<T>` meaning that (like Java without wildcards) `C<T>` is _never_ a
subtype of `C<S>` (unless `T=S`).

## Definition-Site Variance


### Notes

- Whiley [follows
Rust](https://rustc-dev-guide.rust-lang.org/variance.html) in adopting
_definition-site variance_ for generic types which (for example)
differs from the approach taken in Java.

- Java supports use-site variance through wildcards.