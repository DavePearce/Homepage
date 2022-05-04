---
date: 2022-04-27
title: "Puzzling Strong Updates in Rust"
draft: false
#twitter: ""
reddit: "https://www.reddit.com/r/rust/comments/uigljf/puzzling_strong_updates_in_rust/"
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

Alright, that completes the intro on strong updates.  _But, what has
this got to do with Rust?_ Well, the borrow checker performs strong
updates in some situations.  _But, it doesn't always apply them when
it could_.  Whilst considering edge cases like this is a somewhat
academic exercise, I find it useful for understanding the borrow
checker.  And, perhaps, it could even suggest ways to improve the
borrow checker.

Here is a simple example accepted by `rustc`:

```Rust
fn main() {
 let mut r = 1;
 let mut s = 2;
 let mut p = &mut r;
 p = &mut s;
 println!("r={}, *p={}",r,*p);
} 
```

As expected, this compiles and prints `r=1, *p=2`.  They key is that,
since Rust knows `p` is overwritten in the assignment, it can
relinquish the borrow `&mut r`.  However, if we change this a bit, it
breaks:

```Rust
fn main() {
 let mut r = 1;
 let mut s = 2;
 let mut p = Box::new(&mut r);
 *p = &mut s;
 println!("r={},**p={}",r,**p);
}
```

This is pretty much the same example as before.  The contents of `*p`
must be overwritten in the assignment, and so the borrow `&mut r`
should be relinquished as before.  Unfortunately, `rustc` rejects this
program with the following error:

```
let mut p = Box::new(&mut r);
 mutable borrow here ------
*p = &mut s;
println!("r={},**p={}",r,**p);
  mutable borrow  here --- ^
                           |
       immutable borrow here
```

One could argue this is expected since `Box<T>` has no special status
in Rust (i.e. it is just a regular user-defined type).  Hence, the
borrow checker cannot be expected to know about its invariants.  Ok,
so let's just change up the example like this:

```Rust
fn main() {
 let mut r = 1;
 let mut s = 2;
 let mut q = &mut r;
 let p = &mut q;
 *p = &mut s;
 println!("r={}, **p={}",r,**p);
}
```

Now, instead of using `Box<T>`, we're using a mutable borrow.  Its
roughly the same thing but, in this case, mutable borrows do have
special status.  So, we could reasonably expect the borrow checker to
know about the invariants they maintain (i.e. and allow a strong
update here).  _And yet this example is still rejected._

## Is that it?

Well, not quite.  The above suggests the borrow checker _doesn't_
perform strong updates in some situations when it could.  Based on
that, I was thinking the following would fail:

```Rust
fn f<'a>(p: &mut &'a mut i32, 
         q: &'a mut i32) {
  let r = &mut **p;
  *p = q;
  println!("r={}, **p={}",r,**p);
}
```

This is somehow similar to before, in that it requires a strong update
on `*p` to know that `p` and `r` do not interfere on the last
statement.  _But, in fact, the above program is accepted!_ So, the
borrow checker does perform strong updates sometimes...

## Conclusion

Well, I'm not sure what the conclusion is here.  Its not clear that
these kinds of situations are likely to arise in practice.  Perhaps
with some further refinement we could figure out a realistic example
which could (in principle) be accepted, but currently is not.
Eitherway, exploring the limits of the borrow checker is fun!
