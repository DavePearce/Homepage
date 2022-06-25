---
date: 2011-08-29
title: "A Subtyping Gotcha"
draft: false
---

An interesting issue with the following piece of code has recently come to my attention:

```whiley
define Queue as process { [int] items }

int Queue::get():
    item = this.items[0]
    this.items = this.items[1..]
    return item

void Queue::put(int item):
    this.items = this.items + [item]

Queue ::Queue():
    // following line should be invalid
    return spawn { items: [] }
```

The problem is that this code *should not compile*.  More specifically, the constructor `::Queue` should generate a syntax error.

The reason for this is that *we cannot safely subtype processes*.  To see why, consider the following (artificial) example:

```whiley
define MyProc as process { [int] field }
define BrokenProc as process { [int]|int field }

BrokenProc ident(MyProc mp):
    return mp // should not be allowed

void ::breakIt(MyProc mp):
    bp = ident(mp)
    bp.field = 1 // uh oh

[int] ::broken(MyProc mp):
    breakIt(mp)
    return mp.field // should be ok, but isn't...
```

The problem here arises from *aliasing*.  That is, since both `mp` and `bp` refer to the same process and, hence, assigning to `bp.field` corrupts the type of `mp.field`.  To resolve the above problem we require that `MyProc` is not a subtype of `BrokenProc`.  In other words, *no subtyping between process types should allowed*. 

This issue may seem fairly innocuous since we easily can "fix" it by preventing subtyping between processes.  Unfortunately, does this means `spawn {field: []}` is not a subtype of `MyProc` either, since it has type `process {[void] field}`.  In other words, we cannot initialise a process field with an empty list!  There are some well-known ways in which we might get around this.  In particular, if we have a *unique reference* to a process, then subtyping is safe.  Looking at the `::Queue` constructor again, we actually do have a unique reference since we just spawned it!