---
date: 2013-04-09
title: "Compile-Time Verification and I/O"
draft: false
---

A surprisingly common question people ask me when I talk about compile-time checking of [pre-/post-conditions](http://en.wikipedia.org/wiki/Precondition) and [invariants](http://en.wikipedia.org/wiki/Invariant_%28computer_science%29) is: *how do you deal with I/O?*

To understand what the difficulty is, let's consider a simple example in Whiley:

```whiley
define nat as int where $ >= 0
define pos as int where $ > 0

define Rectangle as { nat x, nat y, pos width, pos height }
```

Here, we've defined a simple `Rectangle` data type which (intuitively) lives in a 2D space.  For whatever reason, the invariants on this data type restrict it's `x` and `y` position to being non-negative, whilst the `width` and `height` must be at least `1` (i.e. zero-sized rectangles are not allowed).

Now the question: *how do I read in a Rectangle from an I/O device (e.g. from the network)?*  This is a fair question since we must assume that I/O devices are "untrusted".  That is, we can read something which looks like a `Rectangle` from an I/O device, *but we have no guarantee that the required Rectangle invariants are upheld*.  This seems like a problem.

Let's consider first a broken approach.  This makes use of a method `::readInt()` for reading an integer from an `InputStream`:

```whiley
Rectangle readRectangle(InputStream in):
   x = readInt(in)
   y = readInt(in)
   w = readInt(in)
   h = readInt(in)
   return {
       x: x,
       y: y,
       width: w,
       height: h
   }
```

This program would generate a compile-time error in Whiley, because the verifier cannot prove the required `Rectangle` invariants are met by the value returned.  For example, it knows only that variable `w` is an `int` of some sort and, hence, *that it could hold any valid integer value*.  Therefore, it cannot prove that `w > 0` holds which is required by the `Rectangle` invariant.

At this point, the answer is fairly straightforward: *we need to check the data we've read matches the required invariants and, if not, report an error*:

```whiley
Rectangle readRectangle(InputStream in) throws Error:
   x = readInt(in)
   y = readInt(in)
   w = readInt(in)
   h = readInt(in)
   if x < 0 || y < 0 || w <= 0 || h <= 0:
       throw Error("invalid rectangle read from stream")
   return {
       x: x,
       y: y,
       width: w,
       height: h
   }
```

Now the verifier can now be certain that the required `Rectangle` invariants are met by the value returned.  Whilst this idea is not exactly rocket science, it surprises me how often people miss it.

To me, the above illustrates an oft-overlooked benefit of using compile-time verification: *the compiler forces us to validate all data read from I/O*.  Some will argue this introduces unnecessary overhead, since there are situations where you trust an I/O stream and, hence, can avoid data validation.  Personally, I believe this is unusual and that, in general, you want to validate all data coming in from I/O.  Furthermore, you get some potential performance benefits from using compile-time verification here as, once you've validated the input on entry, the compiler will statically guarantee the invariants are upheld throughout your program.  
