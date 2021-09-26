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
`cargo-fuzz` or `proptest`) which can offer stronger guarantees.  To
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

This is one of my goto functions for testing verification systems,
since it has some nice post-conditions.  To get started we need to add
some empty methods as follows:

```Rust
fn __nondet<T>() -> T { unimplemented!() }
fn __VERIFIER_assume(cond: bool) { unimplemented!() }
```

These functions are known to RMC and have special significance.  Now,
let's write our first simple test:

```Rust
#[cfg(rmc)]
#[no_mangle]
pub fn test_01() {
    let xs : [u32; 2] = __nondet();
    assert!(indexof(&xs,0) == usize::MAX);
}
```

Running this code through RMC reports that the `assert` can fail.
What's happening here is that we are testing `indexof()` for all
possible arrays of size `2`.  Hence, it should fail since some arrays
may contain `0` and `indexof()` will not return `usize::MAX`.  This is
the key difference between using RMC and an automated testing tool:
upto certain bounds, RMC will check all possible values.


This makes since we cannot be sure what `indexof()` will return for an
_arbitrary_ array of length `2` (i.e. since such an array may or may
not hold `0`).

## First GO

Do something simple.

## Getting more sophisticated

Generalising

## Good Stuff

Final solution.