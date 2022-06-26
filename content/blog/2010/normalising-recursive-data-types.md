---
date: 2010-09-19
title: "Normalising Recursive Data Types"
draft: false
---

Recently, I came across an interesting problem with the type system I'm using in Whiley.  Specifically, the problem relates to recursive types which are equivalent but not identical.  Consider, for example, the following Whiley code:

```whiley
define Link as { int data, LinkedList next }
define LinkedList as null | Link
```

This is a fairly straightforward definition of a [linked list](http://wikipedia.org/wiki/linked_list).

Now, the question is: *what is the type of a LinkedList?* To determine this, the compiler does a [depth-first search](http://wikipedia.org/wiki/depth-first_search) from the `define` point.  So, starting from  `LinkedList`, it traverses into the body of the defintion, and then into the bodies of those datatypes it contains (i.e. `Link` in this case).  At some point during this traversal, it will encounter the name `LinkedList` again, which signals a recursive data type.  This traversal leads to the following (recursive) type:
```X < null | {int data, X next} >```
Here, the braces signify a recursive type on variable X.  Such a type essentially captures an infinite number of concrete types, which can be obtained by unfolding.  Some examples are

```
null
{int data, null next}
{int data, {int data, null next} next}
```

All of these are subtypes of the original recursive type.

Now, onto the problem at hand, which is nicely illustrated by the following Whiley code:

```whiley
int length(LinkedList list):
  if list ~= Link:
    return 1 + length(list.next)
  else:
    return 0
```

This little example shows how to determine the length of a list.  In particular, we use the `~=` type test operation to check the runtime type of `list`.  What this doing is essentially checking whether `Link` is a subtype of `LinkedList`.  The actual types in question are:

```
LinkedList ==> X < null | {int data, X next} >
Link ========> Y < {int data, null|Y next} >
```

So, *how do we test whether one is the subtype of the other?* Well, this is the thing: *we need them to be the same, but they're obviously not.*

To resolve this issue, we can normalise the recursive types.  There are different ways this could be done, but for the moment I do it by *unfactoring* them.  That is, by pulling out bits which are not recursive, like so:

```
X < null | {int data, X next} >
=====> null | X < {int data, null|X next}>
```

The key is that the second component of the derived type is now identical to the type of `Link` and we can now easily perform the subtype test.

Anyway, it's not completely clear to me whether this unfactoring process is sufficient for all possible cases or, indeed, what the complexity class of subtype testing in the presence of recursive types like these is (hopefully, it's at least decidable :).  Digging around briefly, I did come across the following paper which seems related, but I need time to digest it properly:
   * *Subtyping Recursive Types*, Roberto M. Amadio1 Luca Cardelli, TOPLAS, 1993.  [[ACM DL](http://portal.acm.org/citation.cfm?id=155231)] [[PDF](http://citeseerx.ist.psu.edu/viewdoc/download?doi=10.1.1.65.4769&rep=rep1&type=pdf)]

