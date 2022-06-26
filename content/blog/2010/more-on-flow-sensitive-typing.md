---
date: 2010-11-09
title: "More on Flow-Sensitive Typing"
draft: false
---

The idea behind [flow-sensitive typing in Whiley](/2010/09/22/on-flow-sensitive-types-in-whiley/) is to give a [statically typed language](http://en.wikipedia.org/wiki/Type_system#Static_typing) the look-and-feel of a [dynamically typed language](http://wikipedia.org/wiki/Dynamic_programming_language)  (as much as possible).  The following illustrates:

```whiley
 int average([int] items):
    v = 0
    for i in items:
        v = v + items[i]
    return v / |items|
```

Here, we see that there are only two type declarations: one for the parameter, and one for the return.  Otherwise, it looks much like a dynamically typed language. Variable `v` is *declared by assignment* to have type `int`.  In other words, assigning `0` declares it and gives it the type `int` at that point.  We could assign variable `v` other values later on, and it's type would be automatically updated to reflect this.  The following gives an artificial example to illustrate:

```whiley
 int f(int x):
    y = 1123
    // here, y has type int
    x = x + y
    y = "Hello"
    // now, y has type string
    return x + |v|
```

A number of interesting issues arise with Whiley's flow-sensitive typing system, and we'll look at them in more detail now.
## Conditionals
An on obvious question is: *what happens when a variable is declared on one side of a conditional?* Here's an example:

```whiley
int f(int y):
    if y > 0:
        x = 123
    // following line gives a syntax error
    return x + y
```

This code does not compile, since variable `x` may be undefined at the `return` statement.  It gets more interesting if we do this:

```whiley
real f(int y):
    if y > 0:
        x = y
    else:
        x = 1.23
    return x
```

Now, the question is: *what type does x have at the return statement?* The answer, as expected, is `real` (i.e. because every `int` is also a `real`).

But, wait! *What happens if the types assigned on either side are unreleated?* Well, in this case, Whiley uses a [union type](http://en.wikipedia.org/wiki/Type_system#Union_types) (and see my [earlier post](/2010/09/22/on-flow-sensitive-types-in-whiley/) for more on this).
## Lists
List assignment has some interesting and surprising properties in Whiley.  For example:

```whiley
real f([int] ls):
    if |ls| > 0:
        ls[0] = 1.23
    // type of ls is now [real]
    ...
```

Here, we have a potential assignment from a `real` value to an element of `ls`.  Whiley does not complain that this is an error, despite the type of ls (on entry) being `[int]`.  Instead, Whiley updates the type of `ls` to reflect this.  Thus, since we now have at least one element of type `real`, the whole list is now considered to have type `[real]`.  
## Records
Things really start to get interesting when we consider records.  For example:

```whiley
define Point as {int x, int y}

real f(Point p):
    p.x = 1.234
    ... // type of p now {real x, int y}
```

What we see here is that the type of variable `p` is updated by the assignment to field `x`.  The reason for allowing this is to reflect dynamic languages as much as possible.  In a dynamic language, such an assignment would be always be permitted and, hence, so it is in Whiley.

Most dynamic languages also have *field-creation-by-assignment* semantics as well.  However, in Whiley, I don't permit this and, hence, the following is a compile-time error:

```whiley
int f(Point p):
    p.z = 1
    ...
```

The reason I don't allow this, is that I believe it will cause too many subtle errors.  That is, if the programmer meant to assign to variable `x`, but made a typo and assigned variable `z`, then *the system would not complain*.  For me, this would be going too far.
## Record Subtyping
In a language like Java, you create data-types using classes.  Each class has a specific name, and classes with different names are distinct.  Inheritance and interfaces can be used to allow some degree of interchangeability, but this remains very static and rigid in nature.  Whiley adopts a much more flexible approach, sometimes known as *structural subtyping*.  First and foremost, records in Whiley are not associated with names --- they are really just macros which expand.  So, for example, this piece of code
```whiley
define Point as {int x, int y}

int getX(Point p):
    return p.x
```
is essentially identical to this:
```whiley
int getX({int x, int y} p):
    return p.x
```
In other words, the name `Point` has no semantic meaning in the language.  The beauty of this is that we can easily interchange Points with types defined elsewhere, potentially by others.  For example, suppose in some other file we have this definition:
```whiley
define Point2D as {int x, int y}
```
Well, any method that accepts a `Point2D` will also accept an `Point` instance (and vice versa) --- *despite the fact that these types may come from files which were developed in isolation*.  There's no need to write code which converts between `Point`s and `Point2D`s, etc.  Similarly, subtyping of records works as expected:

```whiley
define RealPoint2D as {real x, real y}
```

Here, we can pass `Point` instances into functions which accept `RealPoint2D`s  (since every `Point` is a `RealPoint2D`).  However, we cannot pass `RealPoint2D` instancess into functions expecting Points as, for example, `{x:2.2,y:1.0}` is not an instance of `Point`.

Finally, Whiley also also supports subtyping between records with different numbers of fields:
```whiley
define Point3D as {int x, int y, int z}
```
A `Point3D` instance can be passed into a function expecting a `Point` or a `Point2D`, etc.  Again, no awkward conversions between different types are necessary, making life just that bit simpler...

## Conclusion
I'm a big fan of both dynamic and static typing (for different reasons, obviously).  I see no reason why we can't get the best of both worlds, and this is what I'm trying to do with Whiley's type system.  As usual, only time will tell if it really makes sense or not.  For now, I'll let you decide...
