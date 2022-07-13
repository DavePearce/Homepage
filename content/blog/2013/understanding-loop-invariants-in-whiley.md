---
date: 2013-01-29
title: "Understanding Loop Invariants in Whiley"
draft: false
---

In this article, I'll look at a common problem one encounters when verifying programs: namely, *writing {{<wikip page="Loop_invariant">}}loop invariants{{</wikip>}}*.  In short, a loop invariant is a property of the loop which:

>   1. holds on entry to the loop;
>   2. holds after the loop body is executed;
>   3. holds when the loop finishes.

Loop invariants can be tricky to get right but, without them, the verification will probably fail.  Let's consider a very simple example:

```whiley
define nat as int where $ >= 0

nat counter(int count):
    i = 0
    while i < count:
        i = i + 1
    return i
```

This program does not verify.  In order to get it to verify, we need to add a loop invariant.  The need for loop invariants arises from {{<wikip page="Hoare_logic">}}Hoare's rule{{</wikip>}} for `while`-loops.  The key issue is that the verifier *does not know anything about any variable modified within a loop, other than what the loop condition and/or invariant states*.

In our example above, the loop condition only tells us that `i < count` during the loop, and that `i >= count` after the loop (in fact, we can be more precise here but the verifier cannot).  Knowing that `i >= count` is not enough to prove the function's post-condition (i.e. that `i >= 0`).  This is because `count` is an arbitrary `int` which, for example, may be *negative*.

Therefore, to get our example to verify, we need a loop invariant that explicitly states `i` cannot be negative:

```whiley
nat counter(int count):
    i = 0
    while i < count where i >= 0:
        i = i + 1
    return i
```

The loop invariant is specified on the `while` loop with the `where` keyword.  In this case, it simply states that `i` is always `>=0`.  Whilst this might seem obvious to us, it is unfortunately not so obvious to the verifier!  In principle, we could employ a simple form of {{<wikip page="Static_program_analysis">}}static analysis{{</wikip>}} to infer this loop invariant (although, currently, Whiley does not do this).  Unfortunately, in general, we will need to write loop invariants ourselves.

To explore a slightly more complex example, I've put together a short video which illustrates using Whiley to verify a program which sums a list of natural numbers:

{{<youtube id="WwnxHugabrw">}}

Finally, if you're interested in trying this out for yourself, the easiest way to run Whiley is through the [whileylabs](http://whileylabs.com) website. Have fun!
