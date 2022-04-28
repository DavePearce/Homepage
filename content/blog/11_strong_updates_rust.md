---
date: 2022-04-27
title: "Understanding Strong Updates in Rust"
draft: true
#twitter: ""
#reddit: ""
---

The idea of a _strong update_ comes from earlier work on static
analysis and, in particular, [pointer
analysis](https://en.wikipedia.org/wiki/Pointer_analysis).  To
understand this, let's imagine a hypothetical non-null analysis for C:

```c
int* r = (int*) malloc(sizeof(int));
int** p = &r;
```

At this point, our non-null analysis would conclude that `p` was
`nonnull` and that `r` was `nullable`.  That is, `r` could be `NULL` if the
allocation failed.  Let's continue the example:

```c
...
int* q = (int*) malloc(sizeof(int));
if(q == NULL) { return; }
// Strong Update
*p = q;
```

At this point, the analysis should conclude that `r` is `nonnull`.  In
the terminology of static analysis, this requires what we call a
[strong
update](https://stackoverflow.com/questions/13199335/can-someone-explain-what-are-strong-updates-and-give-an-example-which-illustrate).
To perform a strong update, the non-null analysis must know there is
only one possible target for `p`.  If we change the example as
follows, then we lose this property (where `flag` is unknown):

```c
int* r = (int*) malloc(sizeof(int));
int* s = (int*) malloc(sizeof(int));
int** p = flag ? &r : &s;
int* q = (int*) malloc(sizeof(int));
if(q == NULL) { return; }
// Weak Update
*p = q;
```

  Now, the analysis must report both `r` and `s` as `nullable`.  This
is because, although it knows one of them is `nonnull`, it doesn't
know which one.

## Strong Updates in Rust

Alright, so that completes the intro on strong updates.  _But, what
has this got to do with Rust?_ Well, the borrow checker performs
strong updates in some situations _but not others_.  In fact, it
doesn't always apply a strong update when it could.  Whilst you might
think it is a somewhat academic exercise to consider edge cases like
this, I find it helps me better understand the borrow checker.
Furthermore, it could suggest ways in which the borrow checker might
be improved.

As a start, here's a simple example which is accepted by `rustc`:

```Rust
fn main() {
 let mut r = 1;
 let mut s = 2;
 let mut p = &mut r;
 p = &mut s;
 println!("r={:?}, *p={:?}",r,*p);
} 
```

As expected, this compiles and prints `r=1, *p=2`.  They key is that,
since Rust knows `p` is overwritten in the assignment, it can
relinquish the borrow `&mut r`.  However, if we change this in a
seemingly simple way, it breaks:

```Rust
fn main() {
 let mut r = 1;
 let mut s = 2;
 let mut p = Box::new(&mut r);
 *p = &mut s;
 println!("r={:?},**p={:?}",r,**p);
}
```

This is pretty much the same example as before.  The contents of `*p`
must be overwritten in the assignment, and so the borrow should be
relinquished.  Unfortunately, `rustc` rejects this program with the
following error:

```
let mut p = Box::new(&mut r);
                     ------ mutable borrow occurs here
*p = &mut s;
println!("r={:?},**p={:?}",r,**p);
                           ^ --- mutable borrow later used here
                           |
                           immutable borrow occurs here
```

We can make a reasonable argument that this should be expected since
`Box<T>` has no special status (i.e. it is just a regular user-defined
type).  Hence, the borrow checker couldn't be expected to know about
the invariants it maintains.  Ok, so let's just change up the example
like this:

```Rust
fn main() {
 let mut r = 1;
 let mut s = 2;
 let mut q = &mut r;
 let p = &mut q;
 *p = &mut s;
 println!("r={:?}, **p={:?}",r,**p);
}
```

Now, instead of using `Box<T>`, we're using a mutable borrow.  Its
roughly the same thing but, in this case, mutable borrows do have
special status.  So, we could reasonably expect the borrow checker to
know about the invariants they maintain (i.e. and allow a strong
update here).
