---
date: 2021-09-01
title: "Fooling the Borrow Checker"
draft: false
metaimg: "images/2021/Fooling_Borrow_Checker_Preview.png"
metatxt: "Pondering how the Rust borrow checker decides when a borrow could still be live"
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

Answering the question comes down to the return type of `f()`.  For
example, if `f()` returns `i32` then it certainly cannot return the
borrow.  On the otherhand, if `f()` has the following type then it can
return the borrow:

```Rust
fn f<'a>(p : &'a i32) -> &'a i32 { ... }
```

In this case, it _must_ return the borrow as there is nothing else it
could return to satisfy the return type (well, assuming its not doing something `unsafe`).

## Warm Up

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

## Stretching Out

In the above examples, its fairly obvious from the method signature
that it could return the borrow.  We can obfuscate this a little by
trying to hide it in something else.  For example:

```Rust
struct Wrap<'a> { field: &'a i32 }

fn f<'a>(p : &'a i32) -> Wrap<'a> { ... }
```

This is still not enough to fool the borrow checker though.  The
presence of lifetime `a` in the return type is a giveaway that our
borrow could be hiding in there.  The same applies for arrays, such
as:


```Rust
fn f<'a>(p : &'a i32) -> Box<[Wrap<'a>]> { ... }
```

(**NOTE:** to make this compile add `#[derive(Copy,Clone)]` before
`struct Wrap`)

Again, the presence of `a` is enough to trigger the borrow checker
that our borrow might be returned.  Still, in this case, we don't
actually _have_ to return the borrow --- we could just return an empty
array.

## The Puzzle

An interesting (though largely pointless) question arising from all
this, is the following puzzle:

> _Can a method signature fool the borrow checker into thinking a
> borrow can be returned when, in fact, it cannot?_

That is, where the return type is something we know could never hold
the borrow, but where Rust must still assume it could.  In fact, it's
not so hard.  But, my first attempt failed:

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
call, _even though this is no valid implementation of `f()` where this
is true_ (again, assuming it doesn't do something `unsafe`).

## Another Puzzle

Now, there are other ways to fool the borrow checker, but these
somehow don't seem as interesting to me.  For example, we can exploit
knowledge of control-flow like so:

```Rust
fn f(n:i32) {
    let mut x = 123;
    let mut y = 234;
    let mut z = 456;
    let mut p = &mut x;
    //
    if n >= 0 { p = &mut y; } 
    if n <= 0 { p = &mut z; } 
    //
    println!("x={},p={}",x,p);
}
```

Here, we know the borrow `&mut x` has expired by the time we reach
`println!()`, but the borrow checker is not this smart (it is just a
fancy [data-flow
analysis](https://en.wikipedia.org/wiki/Data-flow_analysis) after
all).  We can also fix this program by just using an `else` block:

```Rust
fn f(n:i32) {
    let mut x = 123;
    let mut y = 234;
    let mut z = 456;
    let mut p = &mut x;
    //
    if n >= 0 { p = &mut y; } 
    else { p = &mut z; } 
    //
    println!("x={},p={}",x,p);
}
```

This now compiles because the borrow checker can easily determine that
the borrow has expired on all paths through the function.

## Conclusion

Well, hopefully that was an interesting take on a few subtle points of
Rust!  There is no real conclusion, but if you like studying the
borrow checker you might find my [recent paper on the
subject](https://whileydave.com/publications/pea21_toplas/) provides
interesting reading.
