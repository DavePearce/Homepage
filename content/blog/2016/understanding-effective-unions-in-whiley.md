---
date: 2016-12-09
title: "Understanding Effective Unions in Whiley"
draft: false
---

The concept of *effective union types* in Whiley exposes some interesting features worth considering.  In particular, they result in a separation between the *readable* and *writeable* view of a type.  But, we're getting ahead of ourselves!  Let's start with what exactly effective unions are...
### Effective Unions
An effective union is a union type which has some commonality which can be exploited.  Here's a simple example to illustrate:

```whiley
type Rectangle is { int x, int y, int width, int height }
type Circle is { int x, int y, int radius }

// A shape is either a rectangle or a circle
type Shape is Rectangle | Circle

// Move the center point by a given translation
function translate(Shape s, int dx, int dy) -&gt; (Shape r):
    s.x = s.x + dx
    s.y = s.y + dy
    return s
```

This little function is responsible for moving the position of a given `Shape`. Since both `Rectangle` and `Circle` have the fields `x` and `y`, we can access them when given just a `Shape`. We say that the effective union type of `Shape` is `{ int x, int y, ... }`, where `...` represents zero or more unknown fields.

Effective union types have some analogies in other programming languages. For example, in the C programming language, they are similar to the notion of a Common Initial Sequence (see e.g. [here](http://publications.gbdirect.co.uk/c_book/chapter6/unions.html) and [here](http://stackoverflow.com/questions/34616086/union-punning-structs-w-common-initial-sequence-why-does-c-99-but-not)). In Java, effective union types perhaps share some connection with type bounds as, for example, the type `T` where `T extends Shape` can be regarded as containing everything common to a `Shape`.
### Readability vs Writeability
An interesting question is what we can do with an effective union.  In particular, what types we can read from it and write to it.  Let's consider another, slightly more interesting example:
```whiley
type Message is Request | Response
type Request is { int meth, URL data }
type Response is { int code, byte[] data }
```
Type `Message` is an effective union which \"looks\" like `{ URL|(byte[]) data, ... }`. Given a variable `m` of type `Message`, reading the field `m.data` returns a value of type `URL|(byte[])`. That makes sense. So, *can we write to the field m.data?* In this case, no we can't.

Reading from the field of an effective union returns the union of types for that field. But, for writing, we can only write values common to all instances of that field in the union. To motivate this, let's refine our example a little:
```whiley
type Message is Request | Response
type Request is { int meth, null|URL data }
type Response is { int code, null|(byte[]) data }
```
Again, suppose we have a variable `m` of type `Message`. Since `null` is supported by both instances of the data field, we can safely write `m.data = null`. But, we cannot assign a URL to `m.data` as we don't know whether `m` represents a `Request` or a `Response` (and if we allowed the assignment regardless, we might end up with a mangled value).
### Conclusion

Effective union types are an interesting feature of Whiley which leads to a distinction between the "readable" view of a type, and the "writeable" view. At this point in time, it's fair to say that it is unclear how useful this will turn out to be. But, it's definitely something I'm going to continue exploring ...
