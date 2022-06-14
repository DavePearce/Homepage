---
date: 2017-03-28
title: "Property Syntax in Whiley"
draft: false
---

Recently, I gave a demo which showed off thew new "Property Syntax" in Whiley. Whilst this is still in the `devel` branch it will make its way, soon enough, into the next release. I thought it would be interesting to give a quick taste of the syntax.

To understand the purpose of *properties*, let's take a simple example as would be currently written. That of the `indexOf(int[],int)` function:

```whiley
function indexOf(int[] items, int item) -> (int r)
// If valid index returned, element matches item
ensures r >= 0 ==> items[r] == item
// If invalid index return, no element matches item
ensures r <  0 ==> all { i in 0..|items| | items[i] != item }
// Return value is between -1 and size of items
ensures r >= -1 && r < |items|:
    //
    int i = 0
    while i < |items|
        where i >= 0
        where all { k in 0 .. i | items[k] != item }:
        //    
        if items[i] == item:
            return i
        i = i + 1
    //
    return -
```

This is one of my standard examples, as it covers of the main aspects of verification in Whiley. (Note, if you're not that familiar with verification or loop invariants in Whiley, I'd suggest first looking [here](/2015/09/22/introductory-lecture-on-verification-in-whiley/) and/or [here](/2013/01/29/understanding-loop-invariants-in-whiley/)).

One problem above is the verbosity of the specification and loop invariant, and there's even some repetition between them. So, we can now define a `property` to help:

```whiley
property contains(int[] xs, int x, int n)
// Some element of the array matches x
where some { k in 0..n | xs[k] == x }
```

This property states something like: *the array `xs` contains value `x` at some index between `0...n` (exclusive)*. The idea is that a "property" is an attribute of some data which holds, as opposed to a function which transforms input data into output. This means we can rewrite `indexOf()` to use the `contains()` property as follows:

```whiley
property contains(int[] xs, int x, int n)
// Some element of the array matches x
where some { k in 0..n | xs[k] == x }

function indexOf(int[] items, int item) -> (int r)
// If valid index returned, element matches item
ensures r >= 0 ==> items[r] == item
// If invalid index return, no element matches item
ensures r <  0 ==> !contains(items,item,|items|)
// Return value is between -1 and size of items
ensures r >= -1 && r < |items|:
    //
    int i = 0
    while i < |items|
        where i >= 0 && !contains(items,item,i):
        //    
        if items[i] == item:
            return i
        i = i + 1
    //
    return -1
```

This is actually much neater than before!

One may wonder why we don't just use a function instead of a property.  Unfortunately, a function does not convey the right meaning and, from the perspective of verification, properties are treated in a fundamentally different way than for functions (which are treated as [uninterpreted functions](https://en.wikipedia.org/wiki/Uninterpreted_function)).
