---
date: 2021-09-01
title: "Trying out the Rust Model Checker (RMC)"
draft: true
#twitter: ""
#reddit: ""
---

The [Rust Model Checker (RMC)](https://github.com/model-checking/rmc)
allows Rust programs to be model checked using the [C Bounded Model
Checker (CBMC)](https://www.cprover.org/cbmc/).  In essence, RMC is an
extension to the Rust compiler which converts Rust's
[MIR](https://rustc-dev-guide.rust-lang.org/mir/index.html) into the
input language of CBMC ([GOTO](http://www.cprover.org/goto-cc/)).

Using RMC provides a powerful alternative to testing (e.g. with
`cargo-fuzz` or `proptest`) which offers stronger guarantees.  To
understand how it works, I'm going to walk through the process of
checking the following simple function:

```Rust 
fn indexof(items: &[u32], item: u32) -> usize {
    for i in 0..items.len() {
        if items[i] == item {
            return i;
        }
    }
    //
    return usize::MAX;
}
```

This is a nice functions for testing verification systems, since it
has some nice post-conditions.  To get started we need to add some
helper methods, the first of which is the following:

```Rust
fn __nondet<T>() -> T { unimplemented!() }
```

This function is known to RMC and has special significance.  Here,
`__nondet<T>()` returns a _non-deterministic value_.  We can think of
this as arbitrary value matching the type in question, and this is
where the power comes from.  Instead of testing individual values of a
given type, we're testing _all possible values_ of that type!  The
second helper method we need is the following:

```Rust
fn __VERIFIER_assume(cond: bool) { unimplemented!() }
```

Again, this has specifical significance to RMC and, as we'll see, it
is used _constrain_ non-determinstic values.  For example, we might
something like this:

```Rust
let x : u32 = __nondet();
let y : u32 = __nondet();
__VERIFIER_assume(x < y);
```

Here, we first created two arbitrary values and then constrained `x`
so that its always below `y`.  Imagine we needed to do this to meet
some requirement of the API we're testing.  This means RMC will never
consider the values e.g. `x=10, y=10` or `x=0, y=255`.  But, it will
still consider _all_ values where `x < y`, such as `x=0,y=1`,
`x=255,y=256`, etc.

## Our First Proof

We're now going to write our first "test" with RMC.  Except that its
not a test in the conventional sense, since we're using arbitrary
values.  To try and make this distinction clear, RMC refers to them as
_proofs_.

So, let's write our first simple proof:

```Rust
#[cfg(rmc)]
#[no_mangle]
pub fn test_01() {
  let xs : [u32; 2] = __nondet();
  assert!(indexof(&xs,0) == usize::MAX);
}
```

This tells RMC to test `indexof()` for all possible arrays of size `2`
and, in doing so, RMC finds the `assert` can fail.  This makes sense
as some arrays may contain `0` --- meaning `indexof()` will not return
`usize::MAX`.  This is the key difference between using RMC and an
automated testing tool: _upto certain bounds, RMC checks all possible
values_.

Of course, something isn't quite right yet.  The `indexOf()` method is
correct, but our proof is failing.  We need to refine our proof so
that it reflects the true contract of `indexOf()`.  Specifically, when
given an array `xs` which doesn't contain `0`, we expect
`indexOf(xs,0) == usize::MAX`.  The question is how to set this
constraint up with RMC.  In fact, its pretty easy since we know the
size of `xs`:

```Rust
#[cfg(rmc)]
#[no_mangle]
pub fn test_01() {
  let xs : [u32; 2] = __nondet();
  __VERIFIER_assume(xs[0] != 0);
  __VERIFIER_assume(xs[1] != 0);
  assert!(indexof(&xs,0) == usize::MAX);
}
```

We've used `__VERIFIER_assume()` to tell RMC that it should assume the
elements in our array do not hold `0`.  And finally RMC reports, as
expected, that our proof suceeds!

## Going Forward

So, we've written our first proof.  _Now what?_ Well, since our proof
only applies for arrays of size `2`, it would be nice to *generalise*
it more.  Unfortunately, testing arrays of arbitrary size is beyong
the limit of RMC but, for example, we can prove for all arrays _upto_
size `3`.  Furthermore our proof only checks for `item=0` but we could
check arbitrary values.  So, let's put that altogether:

```Rust
#[cfg(rmc)]
#[no_mangle]
pub fn test_01() {
  // Create arbitrary element
  let x : u32 = __nondet();
  // Create arbitrary (valid) length
  let len : usize = __nondet();
  __VERIFIER_assume(len <= 3);
  // Create arbitrary array
  let xs : [u32; 3] = __nondet();  
  // Ensure element not in array
  __VERIFIER_assume(xs[0] != x);
  __VERIFIER_assume(xs[1] != x);
  __VERIFIER_assume(xs[2] != x);
  // Check
  assert!(indexof(&xs[..len],x) == usize::MAX);
}
```

So, this is getting more interesting!  Now, we have an arbitrary value
`x` instead of `0` as the value to look for.  Likewise, the final
statement takes a _slice_ of `xs` upto a given length `len`.  Since
`len` is constrained to be anything upto and including length `3`,
this means RMC now checks all arrays upto length `3`.

Our updated proof represents a significant improvement over the first.
But, there are still tweaks we can make.  For example, its good to
employ a constant `LIMIT` which determines the maximum length:

```Rust
  ...
  __VERIFIER_assume(len <= LIMIT);
  // Create arbitrary array
  let xs : [u32; LIMIT] = __nondet();  
  // Ensure element not in array below len
  for i in 0..len {
    __VERIFIER_assume(xs[i] != x);
  }  
  ...
```

Using `LIMIT` means we can easily try larger maximum lengths to see
how far RMC can go.  One issue, however, is that we have used `for`
loop which causes difficulties for the underyling CBMC tool.  To
address this, we must provide a command-line argument `--unwind X`
where `X` is some bound (e.g. `3`).  This tells CBMC to _unroll_ the
loop at most `X` times.  In this case the maximum length of the array
determines how much unrolling CBMC needs to be confident the proof
holds


## The Flip Side

Now we've generalised our proof, its looking pretty nice.  But, it
only checks the case when `item` is _not_ in `items` --- that's only
half the story!  So, we should add a second proof for the case where
`item` _is_ in `items`

## Conclusion

[Getting Started Guide](https://model-checking.github.io/rmc/)