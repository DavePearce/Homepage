---
date: 2016-05-28
title: "Reference Lifetimes in Whiley"
draft: false
---

The concept of [lifetimes](https://doc.rust-lang.org/book/lifetimes.html) was pioneered in the Rust programming language, and builds on earlier notions of [regions](https://en.wikipedia.org/wiki/Region-based_memory_management) and ownership types. Lifetimes are considered one of Rust's "most unique and compelling features".

Recently, the concept of reference lifetimes has been added to Whiley by Sebastian Schweizer ([@SebastianS90](https://github.com/SebastianS90)). In this post, I'm going to try and summarise the basic idea and how it looks in Whiley. To start with, let's consider the following (broken) program written in C:

```c
int *getPointer() {
  int local = 0;
  return &local
}
```

This method returns the address of a local variable thereby creating a *[dangling pointer](https://en.wikipedia.org/wiki/Dangling_pointer)*. We say that the returned pointer *outlives* the lifetime of the data it refers to.  The purpose of reference lifetimes is to ensure *dangling pointers cannot be created*. Or, put it another way, to ensure memory deallocation *can be handled safely without a garbage collector*.
## Lifetime Syntax
In Whiley, we can't take the address of a local variable so we cannot exactly recreate the above example. A similar example using the new lifetime syntax would be:

```whiley
method getReference() -> &int:
   &this:int local = this:new 0
   return local
```

This defines a simple method which allocates a new integer on the heap and returns a reference to it. As expected, this example now produces a compile-time error. This is because the reference `local` is declared to have lifetime `this`. This means the data to which it refers has the same lifetime as the enclosing method and, hence, we cannot return a reference to it.

Previously Whiley supported references without lifetimes. For example, we would have declared `local` above to be `&int` rather than `&this:int`. Since there is no deallocation primitive in Whiley, this meant that all heap allocated data had to be garbage collected. With lifetimes we can now avoid garbage collection when we want to (e.g. on an embedded system). However, Whiley still supports references of the form `&int`. These are now syntactic sugar for `&*:int`, where `*` is the "global" lifetime.
## Lifetime Parameters
Like Rust, Whiley allows methods to declare *lifetime parameters*. The following illustrates:

```whiley
method <l1,l2> swap(&l1:int r1, &l2:int r2):
    int tmp = *r1
    *r1 = *r2
    *r2 = tmp
```

The above method accepts two references of lifetimes `l1` and `l2`. These lifetimes must be provided as arguments when calling the method, so the body can just assume they exist. For example, we could call the method as follows:

```whiley
method caller():
    &this:int i1 = this:new 1
    &this:int i2 = this:new 2
    //
    swap<this,this>(i1,i2)
```

Here, we've created two references with lifetime `this` and passed them into `swap()`, providing `this` as the lifetime argument.

Whiley also now supports the notion of *named blocks*, which can be used for identifying a subscope within a method.  For example:

```whiley
method caller():
    &this:int i1 = this:new 1
    inner:
       &inner:int i2 = inner:new 2
       //
       swap<this,inner>(i1,i2)
```

Here, the `inner` scope identifies a smaller lifetime than that of the enclosing method. We say that `inner` is *outlived* by the lifetime of method body (`this`).
## Lifetime Inference
The Whiley compiler will try to infer lifetime arguments where possible. For example, the above method `caller()` can be written as:

```whiley
method caller():
    &this:int i1 = this:new 1
    inner:
       &inner:int i2 = inner:new 2
       //
       swap(i1,i2)
```

Here, the lifetimes necessary for `swap()` have been omitted and, instead, are inferred by the compiler from the arguments `i1` and `i2`. In general, lifetime inference works pretty well, although there are cases where ambiguity arises and the compiler cannot infer correct lifetimes. In such cases, it reports an error indicating the ambiguity.
## Ownership (or not)
In Rust, the concept of lifetimes, [*ownership*](https://doc.rust-lang.org/book/ownership.html) and [*borrowing*](https://doc.rust-lang.org/book/references-and-borrowing.html) are closely tied together and, in fact, can be hard to distinguish. Roughly speaking, ownership ensures there is at most one mutable reference to any data allocated on the heap, whilst borrowing is the mechanism by which temporary mutable and non-mutable references are obtained.

*The lifetime extension to Whiley does not include the concept of ownership*. This is because lifetimes are being used primarily to ensure safe memory deallocation. In the future, we may still introduce ownership into Whiley as, for example, it is important for preventing data races. However, it is likely that ownership in Whiley will be quite different from ownership in Rust. In particular, rather than using a simplistic flow analysis (i.e. as in Rust's borrow checker), *we can use Whiley's more sophisticated verifier to help enforce ownership*.
## Conclusion
The introduction of lifetimes in Whiley is a big step in the evolution of the language which, eventually, will pave the way for running Whiley on platforms which don't support garbage collection (i.e. as native code). And, over the next few months, I'll be talking more about how this is going to work.
