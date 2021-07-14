---
date: 2021-07-13
title: "Sizing Up Types in Rust"
draft: true
#metaimg: "images/2021/Type_Variance_Preview.png"
#metatxt: "Understanding type variance is important in languages with generic types.  So, I thought I'd take a look at this."
#twitterimgalt: "Illustrating a partial definition of a generic hashmap."
twittersite: "@whileydave"
#twitter: "https://twitter.com/whileydave/status/1371551037400641536"
#reddit: "https://www.reddit.com/r/cpp/comments/l6hqfi/understanding_deadlock_detection_in_abseil/"
---

When learning Rust, understanding the difference between statically
and dynamically sized types seems critical to me.  There are some good
discussions out there already
(e.g. [here](https://github.com/pretzelhammer/rust-blog/blob/master/posts/sizedness-in-rust.md)
and here).  Whilst these explain the mechanics, they didn't tell me
_why_ its done like this in Rust.  The articles made sense, but I was
still confused!  Eventually I had my "eureka" moment, so I thought I
would share that for anyone else struggling.

### Getting Started

I'm a C/C++/Java programmer (amongst other things) and the basic idea
of a statically- versus dynamically-sized type seemed pretty
straightforward: A statically-sized type is one whose size is known at
compile time; and, a dynamically sized type is everything else.  Easy
as!  For example, an `int` is statically sized in Java (i.e. because
its a 32-bit two's complement integer):

```
int x = 10;
```

Now, on the other hand I figured an array `int[]` in Java is
dynamically sized (i.e. because we cannot determine how many elements
it contains at compile time):

```
int[] xs = new []{256,15};
```

At some level, this all makes sense ... but, unfortunately, **it is
completely wrong!** In fact, a Java array would be considered
**statically sized** in the terminology of Rust.  A diagram helps to
shed some light on this:

{{<img class="text-center" src="/images/2021/SizingUpTypes_Java.png" height="175px" alt="Illustrating owning reference being copied to another variable.">}}

Its pretty easy to see that the size of `x` is known at compile time,
but what about `xs`?  Well, yes, it is known at compile time --- its
the size of a pointer (which I've just assumed is 32bits above for
simplicity).  

### Confusion Dawns

Now, we fast forward to a moment early on in my journey towards
learning Rust.  I'm writing a program, and I want an array to hold
some data (like in Jave above).  After some Googling, I think no
problem ... _Rust has arrays!_ And, of we go down the rabbit hole ...

```rust

```

### Enlightenment



## Notes

   * Structs.  Can have dynamically sized at the end, unlike C.
   * Arrays vs Slices.  This is where I got stuck.