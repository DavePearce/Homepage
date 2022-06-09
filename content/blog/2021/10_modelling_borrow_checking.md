---
date: 2021-12-06
title: "Modelling Borrow Checking in Rust"
draft: false
metaimg: "images/2021/ModellingBorrowChecking_Preview.png"
metatxt: "We can use a simple model to think about borrow checking in Rust which is surprisingly effective"
#twitter: ""
#reddit: ""
---

Recently, I've been working on a [formalisation of borrow checking in
Rust](https://whileydave.com/publications/pea21_toplas/).  The idea is
to help people think clearly about how borrow checking works (in
someways perhaps similar to the [Stacked
Borrows](https://plv.mpi-sws.org/rustbelt/stacked-borrows/) work but
with a different perspective).  So, I thought it might be interesting
to explore how this looks here.

## Overview

To get started we'll look at some simple programs which, for example,
don't even consider functions.  Ignoring as much stuff as possible
helps us stay focused.  Here's an example to illustrate:

```Rust
{
  let mut x = 0;
  let mut p = &x;
  let mut q = Box::new(1);
  ...
}
```

Right after the three statements, we can view the borrow checker as
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
for now).  _A simple rule is that we cannot assign or modify a
variable which is borrowed in the environment_.

The idea is that the model is as easy as possible to understand.
Let's look at a slightly more interesting example:


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
checker is unhappy as we are attempting to assign something which is
borrowed in the environment.

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

In some cases, we might actually know better.  For example:

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

This program is safe as we know for certain that `&x` has expired at
the point of the assignment.  However, Rust still [rejects this
program](https://play.rust-lang.org/?version=stable&mode=debug&edition=2021&gist=e3d6fe0f77e95d40899e67b040044866)
because all the borrow checker sees before the assignment is this:

```
{ x:i32, y:i32, y:i32, p:&{x,y,z} }
```

That is, the borrower checker cannot reason about the conditionals
themselves and must conservatively assume `x` is still borrowed.

### Exploring the Model

At this point its important to highlight that this is just a model and
is not a perfect representation of borrowing checking in Rust.
Rather, its purpose is to give us some intuition into how borrow
checking works.  We can use it as a starting point for understanding
the borrow checker.

For example, let's consider this program which is a variation on one
from above:

```Rust
{
  let x = 0;
  let y = 1;
  let mut p = &x;
  let q = &*p;
  //
  p = &y;
  ...
}
```

Unlike our example above, this is not rejected by the borrow checker.
The environment right before the final assignment might be:

```
{ x: i32, y: i32, p: &x, q: &{*p} }
```

This suggests the program should be rejected, since we're attempting
to modify something which is borrowed (i.e. `p`).  However, the borrow
checker is quite sophisticated and (presumably) realises that `&*p` is
equivalent to `&x`.  

Now things start to get interesting!  Consider the following small
change:

```Rust
{
  let mut x = 0;
  let mut y = 1;
  let mut p = &mut x;
  let q = &mut *p;
  //
  p = &mut y;
  ...
}
```

This program is accepted by Rust today but was rejected by earlier
versions of Rust (e.g. `v1.35.0`, edition `2015`).  _Thus, we see how
Rust's own internal model changes over time_ (presumably, in this
case, its related to the [progressive roll-out of
NLL](https://blog.rust-lang.org/2019/07/04/Rust-1.36.0.html)).  The
point is, the model can provide a concrete datapoint to reason about
what Rust is doing.

### Improving the Model

There are still many aspects of Rust which are not captured in the
model as described this far.  For example, this [program is rejected by
Rust](https://whileydave.com/blob/understanding-partial-moves-in-rust/):

```Rust
{ 
  let mut x = 123;
  let mut y = 456;
  let mut p = (&mut x,&mut y);
  let mut q = p.1;
  //
  let z = p;
  ...
}
```

_But, what does the environment look like before the assignment?_
Well, a simple approach is to use `_` for something undefined.  Then,
the environment right before the final assignment is:

```
{ x: i32, y: i32, p: (&mut x, _), q: &{mut y} }
```

And now we can explain why the program is rejected: _Rust doesn't
allow us to move a type containing `_`_.  

A follow up question is: _does Rust allow us to _copy_ a typing
containing `_`_?  But, I'll leave that as an exercise for the reader.

### Conclusion

There are plenty of other things we haven't touched on here.  For
example, the issue of _type compatibility_ and, of course,
_non-lexical lifetimes_.  But, also, more complex issues around
_function invocations_, _lifetime parameters_, _generic types_, etc.
Whilst it is possible to extend the model to handle these, things do
get complicated quite quickly!  If you're interested in seeing more
about how to do that, check out my [recent
paper](https://whileydave.com/publications/pea21_toplas/) discussing
it.
