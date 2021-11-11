---
date: 2021-11-11
title: "A Simple Model of Rust Lifetimes"
draft: true
#metaimg: "images/2021/Verifying_Whiley_Std.png"
#metatxt: "Using Boogie / Z3 it is possible verify a large number of Whiley programs."
#twitter: ""
#reddit: ""
---

For quite a while now, I've been working on a [formalisation of
lifetimes in Rust](http://localhost:1313/publications/pea21_toplas/).
The idea is to help people think clearly about what lifetimes are and
how they work (in someways perhaps similar to the [Stacked
Borrows](https://plv.mpi-sws.org/rustbelt/stacked-borrows/) work but
with a different perspective).  So, I thought it might be interesting
to explore how this looks here.

## Overview

Most of the programs we're going to look at are very simple and, for
example, don't even consider functions.  Ignoring as much stuff as
possible helps us focus on lifetimes.  Here's an example to illustrate:

```Rust
{
  let mut x = 0;
  let mut p = &x;
  let mut q = Box::new(1);
  ...
}
```

Right after the three statements, we can view the borrow checker has
having the following environment:


```
{ x: i32, p: &{x}, q: ☐i32 }
```

This says that `x` has type `i32`, that `p` is an immutable borrow of
`x` and that `q` is a boxed `i32`.  Whilst this is a simplified view,
it still gives insight into how borrow checking works.  For example,
suppose the next statement is `x = 0;`.  Then, the borrow checker can
reject this because it sees `x` is already borrowed (ignoring
[non-lexical
lifetimes](https://stackoverflow.com/questions/50251487/what-are-non-lexical-lifetimes)
for now).

The idea is that the model is easy to understand.  Let's look at a
slightly more interesting example:


```Rust
{
  let mut p = Box::new(0);
  let q = &*p;
  //
  p = Box::new(1); // reject
  ...
}
```

Rust [rejects this program](https://play.rust-lang.org/?version=stable&mode=debug&edition=2021&gist=cce4261e5615b380d1a3aa25d11d13fb) with the following error:

```Rust
error[E0506]: cannot assign to 'p' because it is borrowed
  --> src/main.rs:8:5
   |
3  |     let q = &*p;
   |             --- borrow of 'p' occurs here
4  |     //
5  |     p = Box::new(1);
   |     ^ assignment to borrowed 'p' occurs here
```

This might seem a bit strange, but it makes sense if we consider how
the environment at the point of the assignment:

```
{ p: ☐i32, q: &{*p} }
```

We see that `q` is borrowing through `p` and, hence, the borrow
checker is unhappy.

## Conditionals

In our environments borrows are represented as e.g. `&{x}`, and this
might seem strange --- _why not just `&x`_?  There is a reason for
this which we can see in the following:

```Rust
{
  let mut x = 0;
  let mut y = 1;
  let mut p = &x;
  //
  if ... { p = &y; }
  //
  x = 1;
  ...
}
```

Here, `if ...` just represents some sensible condition.  Again, Rust
[rejects this
program](https://play.rust-lang.org/?version=stable&mode=debug&edition=2021&gist=f15d989a58a533b073b49d6bc25a9ad2)
because `x` may be borrowed at the point of the assignment.  Looking
at the environment right before the environment shows us why:

```
{ x:i32, y:i32, p:&{x,y} }
```

Here, `&{x,y}` should be taken to represent a borrow of _either_ `x`
or `y` (but we don't know which one).  In such cases, the borrow
checker must be conservative and assume either borrow could hold and
therefore reject the assignment.

In some cases, we might actually know that a particular borrow cannot
still be in play:

```Rust
fn f(a: i32) {
  let mut x = 0;
  let mut y = 1;
  let mut z = 2;
  let mut p = &x;
  //
  if a > 0 { p = &y; }
  if a <= 0 { p = &z; }
  //
  x = 1;
  ...
}
```

This program is actually safe since we know for certain that `&x` has
expired at the point of the assignment.  However, Rust still [rejects
this
program](https://play.rust-lang.org/?version=stable&mode=debug&edition=2021&gist=e3d6fe0f77e95d40899e67b040044866)
because all the borrow checker before the assignment is this:

```
{ x:i32, y:i32, y:i32, p:&{x,y,z} }
```

That is, the borrower checker cannot reason about the conditionals
themselves and must conservatively assume `x` is still borrowed.

### Improving the Model

  * Reborrowing has been improved for known locations (did this change
    between versions?)
  * Compatibility
  * Moved types
  * Non-Lexical Lifetimes
