---
date: 2014-05-02
title: "Loop invariants and Break Statements"
draft: false
---

In this article, I'll look at some interesting issues relating to the use of `break` statements within loops, and how this affects the idea of a [loop invariant](http://en.wikipedia.org/wiki/Loop_invariant). For some general background on writing loop invariants in Whiley, see my [previous post](/2013/01/29/understanding-loop-invariants-in-whiley/).  To recap the main points, here's a simple function which requires a loop invariant to verify:

```whiley
function indexOf([int] items, int item) => (int|null r)
// If return is integer, then value at that index must match item
ensures r is int ==> items[r] == item:
    //
    int i = 0
    //
    while i < |items| where i >= 0:
        if items[i] == item:
            return i
        i = i + 1
    //
    return null
```

This function has the simple loop invariant `i >= 0`, which is required to ensure the access `items[i]` is within bounds. For any loop invariant, we must:
   * *Ensure that the loop invariant holds on entry to the loop*.  Since `i == 0` on entry to the above, loop `i >= 0` must hold.

   * *Assuming condition and loop invariant hold at the beginning of loop body, ensure loop invariant holds at end of body*.  Since we assume `i >=0` at the start of the above loop then we know `i+1 >= 0` holds at the end.


With these two properties in place, we can safely assume that the loop invariant holds when the loop has finished regardless of how many iterations were executed.
## Break Statements
Now, the question is: *how do `break` statements fit within this concept of a loop invariant?*  In fact, there are two competing approaches which I refer to as *strict* versus *non-strict*:
   * **Strict Approach**: *the loop invariant must be established immediately before the `break` statement*.  This means we can continue to assume that the loop invariant holds after the loop has completed.

   * **Non-Strict Approach**: *the loop invariant does not need to hold immediately before the break statement*.  This means we cannot continue to assume the loop invariant holds after the loop as completed.  Instead, we must assume that either it holds or whatever held just before the break holds.  This is the approach taken in [Dafny](http://research.microsoft.com/en-us/projects/dafny/), [JML](http://en.wikipedia.org/wiki/Java_Modeling_Language) and [frama-C](http://frama-c.com/).  See also [1] below.


I would argue that the latter approach is strictly more flexible, but can be more confusing as one needs to know what holds before the `break`.  The general suggestion for making this approach less confusing is to give explicit `assert` statements at the point of a `break`.  Here's an example:

```whiley
function find([int] items, int item) => (int r)
// Return value is within bounds of items or one past
ensures 0 <= r && r <= |items|
// If return within bounds then value at index must be item
ensures r != |items| ==> items[r] == item:
    //
    int i = 0
    //
    while i < |items| where 0 <= i:
        if items[i] == item:
            assert 0 <= i && i < |items|
            assert items[i] == item
            break
        i = i + 1
    // done
    return i
```

This function is not correctly specified yet and will fail verification.  The intention is that it returns the lowest index `i` where `items[i] == item`, or it returns `|items|` if no such index exists. A `break` statement is used to exit the loop as soon as a matching index is found. We have included an assertion to clarify what is known to hold at that point.

The above function is failing because the loop invariant is not strong enough to establish the function's postcondition.  Specifically, the following is known at the `return` statement:

`(i >= |items| && 0 <= i)`
`||`
`(0 <= i && i < |items| && items[i] == item)`
We can see that each of these disjuncts comes from the two different paths out of the loop: the top disjunct is for normal termination; the bottom disjunct is for abrupt termination via the `break` statement. At this point, we can now understand the reason why the program fails verification. Given `(i >= |items| && 0 <= i)` we cannot conclude `i == |items|` as required for the postcondition.

To resolve this problem, we need to strengthen the loop invariant in our program using what I refer to as an *overriding invariant* (because the invariant overrides or subsumes the condition).  We can do this like so:

```whiley
    ...
    while i < |items| where 0 <= i && i <= |items|:
       ...
```

This may seem a little unintuitive. However, it does the trick as we get `(i >= |items| && 0 <= i && i<= |items|)` for the normal path through the loop. From that we can correctly conclude that `i == |items|`.
## Conclusion
In verifying "real-world" programs, we have to support the full range of language constructs, such as `break`. With some care, this is not too hard. However, we always need to keep in mind whether or not what's left is really a usable tool or not!!

## References
   * Java Program Verification via a Hoare Logic with Abrupt Termination, Marieke Huisman , Bart Jacobs. In *Proceedings of Fundamental Approaches to Software Engineering (FASE)*, 2000. [[LINK](http://citeseerx.ist.psu.edu/viewdoc/summary?doi=10.1.1.34.8093)]

