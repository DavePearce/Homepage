---
date: 2021-20-01
title: "Some Borrow Checker Puzzles"
draft: true
#twitter: ""
#reddit: ""
---

An interesting question is how the Rust borrow checker decides when a
borrow could still be live.  This illustrates a simple example:

```Rust
let mut x = 1234;
let z = f(&x);
...
```

The question here is whether or not the borrow `&x` is still live
after the method call.  This matters as it impacts how `x` can be used
in following statements.  In fact, we don't have enough information
above to answer this definitively.  There are two cases:

   * **(Can Return)**.  If `f()` could potentially return the borrow,
       then Rust must assume it is live afterwards.
   
   * **(Cannot Return)**.  If `f()` cannot return the borrow, the Rust
       can assume the borrow is dead and allow `x` to be mutated
       again.

Answering the question then comes down to the return type `f()`.  For
example, if `f()` returns `i32` then it certainly cannot return the
borrow.  On the otherhand, if `f()` has this type then it can (in fact
must) return the borrow:

```Rust
fn f<'a>(p : &'a i32) -> &'a i32 { ... }
```

In this case, it must return the borrow as there is nothing else it
could return.

## Puzzle #1

The above is pretty straightforward, but we can make it a bit more
interesting as follows:

```Rust
fn f<'a>(p : &'a i32, q : &'a i32) -> &'a i32 { ... }

...
let mut x = 1234;
let mut y = 678;
let z = f(&x,&y);
...
```

Now, there are two borrows going in and only one coming out.  To be
safe, Rust must assume that either borrow could be returned.  Hence,
neither `x` nor `y` can be mutated after the call (at least while `z`
is still live).

The above is interesting because Rust makes assumptions about what our
code is doing, and those assumptions might not hold true.  For
example, maybe we always return `p` above but only ever read `q` (for
whatever reason).  If we know this, its annoying that Rust doesn't.
In fact, we can resolve this using a simple pattern by rewriting `f`
as follows:

```Rust
fn f<'a,'b>(p : &'a i32, q : &'b i32) -> &'a i32 { p }
```

This might seem cumbersome, but it does the job as Rust now knows `q`
could never be returned (i.e. since there is no relationship between
the lifetimes `a` and `b`).

## Puzzle #2

Talk about other compounds like arrays or structs.

## Puzzle #3

An interesting (largely pointless) question arising from all this, was
whether we could completely "fool" the borrow checker.  That is,
return something we know could never hold the borrow, but where Rust
assumes it could.  In fact, it's not so hard.  But, my first attempt
failed:

```Rust
struct Empty<'a> { }

fn f<'a>(v : &'a i32) -> Empty<'a> {
    Empty{}
}
```

The intuition here is that the mere presence of lifetime `a` in the
return type triggers the borrow checker to think `&x` might be live
afterwards.  However, this doesn't compile as Rust is not happy with
lifetimes that aren't used.  So, we just need to use it without using
it:

```Rust
struct Empty<'a> { phantom: PhantomData<&'a i32> }

fn f<'a>(v : &'a i32) -> Empty<'a> {
    Empty{phantom:PhantomData}
}
```

And now Rust will always think the borrow could be live after the
call.


# Stuff

   * Taking two parameters (even if always the same)
   * Lifetime bounds
   * Mention paper
