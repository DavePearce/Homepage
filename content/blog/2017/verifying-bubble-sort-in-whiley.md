---
date: 2017-12-19
title: "Verifying Bubble Sort in Whiley"
draft: false
---

Bubble sort is a classic sorting algorithm with lots of well-known issues. It's been a long time since I thought much about this algorithm. But, it turns out to be an interesting verification example for Whiley, as it has some interesting loop invariants. The algorithm is pretty simple: *it loops through an array swapping adjacent items which are incorrectly ordered, and repeats until nothing changes*. For example, in one iteration the following swaps would occur:

{{<img class="text-center" src="/images/2017/BubbleSort.png" width="25%">}}

We can see that, after this iteration, the resulting array is not yet in sorted order and, thus, we must repeat the process.
## Specification
Right, we want to try and verify the bubble sort algorithm.  *But, what does this mean?* Well, let's think about it in terms of types.  We want our `sort()` function to take a parameter of *array type* and return a result of *sorted array type*.  In Whiley we have a primitive integer array type already (i.e. `int[]`), but we need to define a sorted integer array type.  We do this as follows:

```whiley
type sorted_ints is (int[] arr)
where all { i in 1..|arr| | arr[i-1] <= arr[i] }
```

This roughly says that every element in the array must be larger than the one below it (if it exists). Using this, we can give the signature of our `sort()` function as follows:

```whiley
function sort(int[] items) -> (sorted_ints result)
```

Whilst this is a pretty good start, it is definitely not a complete specification. For example, we haven't said that the resulting array must have the same size as the original. Or, for that matter, that it should contain the same elements as the original (though perhaps in a different order). Still, for now, that will do.
## Implementation
Right, the next job is to actually implement the algorithm. My starting point is this:

```whiley
function sort(int[] items) -> (sorted_ints result):
    //
    bool clean
    //
    do:
        // reset clean flag
        clean = true
        int i = 1
        // look for unsorted pairs
        while i < |items|: 
            if items[i-1] > items[i]:
               // found unsorted pair
               clean = false
               int tmp = items[i-1]
               items[i-1] = items[i]
               items[i] = tmp
            i = i + 1
    while !clean
    // Done
    return items
```

This implementation is pretty straightforward using a do-while loop to ensure we go through at least one iteration, and then continue until we no longer find unsorted pairs. You can punch this into [whileylabs.com](http://whileylabs.com) and you should find it compiles (though it won't verify).
## Verification
At this point, we've specified and implemented our `sort()` function. Now, we want to *verify* that the implementation meets its specification. That is, get a compile-time guarantee that the resulting array is sorted. You can think of this is as being similar to type checking our program, though it's a bit more involved. If we try and compile the program on whileylabs.com with verification enabled, we'll see the following:

{{<img class="text-center" src="/images/2017/BubbleSort_1.png" width="50%">}}

This is telling us that the verifier believes the expression `i-1` could be negative at this point, thus potentially leading to an index-out-of-bounds error at runtime. As programmers we can deduce that, in fact, since `i` starts at `1` and is always incremented this can never happen (you also need to know that integers in Whiley are unbounded and don't wrap around). But, the verifier needs help here and, to give it the necessary clue, we need to provide a *loop invariant*:

```whiley
        ...
        while i < |items| where i >= 1: 
            ...
```

The loop invariant is necessary because the verifier *cannot reason about variables modified within a loop*.  For such variables, the only information available to the verifier is that given in the loop condition or the loop invariant or the variable's type invariant.  

Having resolved the verification problem around `items[i-1]`, the verifier now reports the error "`type invariant not satisfied`":

{{<img class="text-center" src="/images/2017/BubbleSort_2.png" width="50%">}}

Again, this error is being reported because the verifier cannot reason precisely about loops.  We need to provide another loop invariant, *but what should it be?*  By the time control reaches the `return` statement, we need the array to be sorted.  But, clearly, at an arbitrary point during the loop nest this is not necessarily true.  What we can say is that, for a given index `i`, we know all elements below it are correctly sorted *when the `clean` flag holds*.  We can update the code as follows:

```whiley
property sorted(int[] arr, int n)
where all { i in 1..n | arr[i-1] <= arr[i] }

...

function sort(int[] items) -> (sorted_ints result):
    ...
        while i < |items| 
        where i >= 1
        where clean ==> sorted(items,i): 
            ...
    while !clean
    clean ==> sorted(items,|items|)
    // Done
    return items
```

Here, we've added a property `sorted(int[] arr, int n)` which describes the concept of an array `arr` being in sorted order up to (but not including) a bound `n`.  Thus, when the entire array is sorted we have `n == |arr|`.  In addition, we've now split the loop invariant for the inner loop across two lines using repeated `where` clauses, and these are just conjuncted together by the verifier.

At this point, the `sort()` implementation now passes verification after a short while.  The complete implementation looks like this:

```whiley
// define the type of natural numbers
type nat is (int n) where n >= 0

// define concept of partially sorted array
property sorted(int[] arr, int n)
where all { i in 1..n | arr[i-1] <= arr[i] }

// define type of sorted array
type sorted_ints is (int[] arr) where sorted(arr,|arr|)

function sort(int[] items) -> (sorted_ints result):
    //
    bool clean
    //
    do:
        // reset clean flag
        clean = true
        int i = 1
        // look for unsorted pairs
        while i < |items|
        where i >= 1
        where clean ==> sorted(items,i): 
            if items[i-1] > items[i]:
               // found unsorted pair
               clean = false
               int tmp = items[i-1]
               items[i-1] = items[i]
               items[i] = tmp
            i = i + 1
    while !clean
    where clean ==> sorted(items,|items|)
    // Done
    return items
```

## Conclusion
We've seen here how to verify an implementation of bubble sort in Whiley.  Whilst there are some subtle details, the process is not really that painfull.  Certainly, making good use of the various features provided in Whiley (e.g. type invariants, properties, etc) makes things easier.  Also, we need to be careful in interpreting what "verified" means here.  It does not mean, for example, that the program cannot run out of memory!  Likewise, the Whiley verifier does not check termination of loops and, hence, a verified program may still go into an infinite loop (though, by manual inspection, we can see our implementation does not).  Finally, our specification is not as complete as it could be since, for example, it does even not require that the resulting array is the same size as the original.  But, despite these limitations, there is still a lot of value to be gained from verifying our code.