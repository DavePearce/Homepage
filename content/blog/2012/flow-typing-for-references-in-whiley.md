---
date: 2012-06-11
title: "Flow Typing for References in Whiley"
draft: false
---

The Whiley language splits into a fully functional "core" and an imperative "outer layer".  References and objects do not exist within the functional core.  However, they can exist within the imperative outer layer and are necessary for supporting state and other side-effecting computation.  Here's a simple example:

```whiley
define Buffer as ref { [int] items }

Buffer ::Buffer():
    return new {items: []}

int Buffer::read():
    item = this->items[0] // get head
    this->items = this->items[1..] // strip head
    return item

void Buffer::write(int item):
    this->items = this->items + [item]
```

What we see here is the definition of a simple `Buffer` with a default constructor `::Buffer() `and two methods `read()` and `write(int)`.  We can tell that `Buffer` is a reference type because of the `ref` keyword.  Instances of `Buffer` are objects in the true sense (i.e. as in Java, C++, C#, etc), and can be aliased from multiple locations in the heap.
## Flow Typing for General References
Since objects can be aliased, we must be treat them differently from normal data values in Whiley.  To understand why,  consider this example involving a record value:

```whiley
define IntPoint as {int x, int y}
define RealPoint as {real x, real y}

IntPoint floor(RealPoint p):
    p.x = Math.floor(p.x)
    p.y = Math.floor(p.y)
    return p // OK
```

The above example is type safe because variable `p` is retyped from `{real x, real y}` to `{int x, int y}` by the two field assignments.

Now, let's consider the same example but this time using *references to records*, rather than just records:

```whiley
define IntPoint as ref {int x, int y}
define RealPoint as ref {real x, real y}

IntPoint ::floor(RealPoint p):
    p->x = Math.floor(p->x)  // NOT OK
    p->y = Math.floor(p->y) // NOT OK
    return p
```

Notice that `floor(RealPoint)` has become `::floor(RealPoint)` --- signalling that it is no longer a *function* in the functional core, but a *method* in the imperative outer layer.  Also, notice that we must use the `p->x` notation to access field `x` of `p` which (like C/C++) is syntactic sugar for `(*p).x`.

The problem is, our example involving references to records will not compile because it is unsafe.  This stems from the difference between values (which cannot be aliased) and objects (which can) in Whiley.  To understand why, consider this:

```whiley
real ::f(RealPoint p):
   q = floor(p)
   return p->x
```

Suppose that `::floor(RealPoint)` was consider safe.  Then, the above code would produce a runtime type error because `p->x` would give an `int` value when the runtime was expecting a `real` value (since these are distinct values in Whiley).  The problem is that the method `::f(RealPoint)` expects `p` to have type `RealPoint` after `::floor(RealPoint)` is called but, in fact, it would have type `IntPoint` [if we allowed `::floor(RealPoint)` to compile].

In a nutshell, the conclusion here is that we cannot retype general objects in the same way that we can for the value types (such as records, lists, etc).
## Flow Typing for Unique References
In fact, there are situations when we can safely retype objects!  For example:

```whiley
IntPoint ::create():
   p = new {x:1.23, y:2.34}
   p->x = Math.floor(p->x)
   p->y = Math.floor(p->y)
   return p
```

This example is rather artificial, but it makes the point.  The above code always works correctly because we know that `p` is a *unique reference*.  By unique reference, I mean that the variable `p` holds the only reference to that object and no one else does (i.e. the object is not aliased).  In such case, we could safely retype `p` if the system contained such a notion of unique reference (which, at the moment, the Whiley compiler does not).  Whilst this idea is a little speculative at this stage, there has been quite a bit of work done on this in the academic literature.  Here a few examples:
   * **Alias Annotations for Program Understanding**, Jonathan Aldrich Valentin Kostadinov Craig Chambers.  In *Proceedings of the ACM Conference on Object-Oriented Programming Systems, Languages, and Applications (OOPSLA)*, 2002.  [ [ACM Link](http://dx.doi.org/10.1145/583854.582448) / [PDF](http://archjava.fluid.cs.cmu.edu/papers/oopsla02.pdf) ]

   * **External Uniqueness is Unique Enough**, Dave Clarke and Tobias Wrigstad.  In *Proceedings of the European Conference on Object-Oriented Programming (ECOOP)*, 2003. [ [SpringerLink](http://www.springerlink.com/content/qu8hp60a3qe9f4k0/) / [PDF](https://lirias.kuleuven.be/bitstream/123456789/203436/1/euiue.pdf) ]


Anyway, hopefully that is some food for thought ...
