---
date: 2016-04-21
title: "Contractive and Uninhabited Types in Whiley"
draft: false
---

An interesting feature of Whiley is that it supports true [recursive types](https://github.com/Whiley/WhileyCompiler/issues/631).  These are surprisingly tricky to get right, and recently we came across some interesting examples that the Whiley compiler should (but doesn't) check for.
## Recursive Types
The following illustrates the syntax for recursive types in Whiley:

```whiley
type Link is {any data, LinkedList next}
type LinkedList is null | Link
```

The type `{any data, LinkedList next}` indicates a record with two fields `data` and `next`, whilst `null | Link` indicates a *union* of `null` and `Link`. So, a `LinkedList` is either `null` or a record of type `{any data, LinkedList next}`. A simple function operating over `LinkedList`s is given as follows:

```whiley
function length(LinkedList list) -> int:
    //
    if l is null:
        return 0
    else:
        return 1 + length(list.next)
```

This returns the number of links in the list. The runtime type test operator, `is`, distinguishes the two cases. Since Whiley employs [flow typing](https://en.wikipedia.org/wiki/Flow-sensitive_typing), variable `list` is automatically retyped to `{any data, LinkedList next}` on the false branch.
## Subtyping
With this in mind, let us now think about subtyping between recursive types in Whiley:

```whiley
// A linked list with at least one link
type NonEmptyList is {any data, LinkedList next}

// A linked list containing integer data
type IntList is {int data, IntList next}
```

Both of the above types are implicit subtypes of `LinkedList}` Thus, for example, any variable of type `NonEmptyList` or `IntList` can be passed into the function `length()`.
## Contractive or Uninhabitable?
The Whiley Compiler should accept the above recursive types without trouble. However, it is possible to write recursive types which should be rejected by the compiler. For example:

```whiley
type Invisible is Invisible | Invisible
```

This type does not correspond to any possible runtime value in Whiley (i.e. is uninhabited) and, hence, the compiler should report a syntax error here. Likewise, consider another example:

```whiley
type InfList is { InfList next, int data }

function get(InfList l) -> (InfList r, int d):
   return l.next, l.data
```

In languages with lazy evaluation (like Haskell) such a type would be pretty reasonable. However, in Whiley, all values are trees of *finite* height --- so it is physically impossible construct an value of type `InfList`. So, the compiler should report an error in such case.
## Conclusion
Recursive types are pretty tricky to implement and get right.  The implementation in the Whiley compiler is now almost four years old, and it generally works pretty well.  But, the compiler isn't currently checking for types which are uninhabitable (see issue [#631](https://github.com/Whiley/WhileyCompiler/issues/631) raised for this).  Still work to do then ...
