---
date: 2010-09-23
title: "Whiley gets a While Loop!"
draft: true
---

Finally, in the upcoming release of Whiley, I have added support for both `while` and `for` loops --- which is about time.  Here's an example:

```whiley
define nat as int where $ >= 0

nat sum([nat] list):
   r=0
   i=0
   while i < |list| where r >= 0:
       r = r + list[i]
       i = i + 1
   return r
```

This code sums a list of [natural numbers](http://wikipedia.org/wiki/natural_number) which, of course, produces a natural number.  It illustrates a few points: firstly, the syntax for `while` loops is as expected; secondly, `while` loops may be given [loop invariants](http://wikipedia.org/wiki/loop_invariant) (i.e. the `where` clause).

We can rewrite this example using a `for` loop as follows:

```whiley
define nat as int where $ >= 0

nat sum([nat] list):
   r=0
   for l in list where r >= 0:
       r = r + l
   return r
```
This does what you'd expect and serves to illustrate that loop invariants can be given to `for` loops as well.