---
date: 2014-05-15
title: "Loop Invariants and Do/While Statements"
draft: false
---

Recently, I encountered what I thought was a [bug in the Whiley Compiler](https://github.com/Whiley/WhileyCompiler/issues/343). The issue related to the current treatment of do/while loops and [loop invariants](http://en.wikipedia.org/wiki/Loop_invariant). Having now spent a fair bit of time researching the issue, the answer is not so clear.

The problem manifested itself when I tried to verify the following simple function:

```whiley
function sum([int] xs) => int
requires |xs| > 0:
    //
    int i = 0
    int r = 0
    do:
       r = r + xs[i]
       i = i + 1
    while i < |xs| where i >= 0
    //
    return r
```

The verifier complains about a possible out-of-bounds error on the access `xs[i]`. You can see this for yourself over on [WhileyLabs](http://whileylabs.com). However, looking at the code, we can see that there is no problem. Since the list `xs` cannot be empty, the first iteration through the loop cannot cause an out-of-bounds error. *So, why doesn't the verifier see this?*

The problem is that the verifier only examines the loop body once. Furthermore, it cannot assume that loop condition is true when doing this. Thus, the verifier reasons (incorrectly) that `i < |xs|` might not hold during the loop. In fact, it should be treating the first iteration of the loop as a special case, and then the remaining iterations in the usual way (i.e. assuming the condition is `true` as it passes through the loop body).

Thinking about what should happen, I realised that I could test this out by manually unrolling the loop as follows:

```whiley
function sum([int] xs) => int
requires |xs| > 0:
    //
    int i = 0
    int r = 0
    //
    r = r + xs[i]
    i = i + 1
    while i < |xs| where i >= 0:
       r = r + xs[i]
       i = i + 1
    //
    return r
```

Now, this function is essentially identical to the original and the Whiley compiler now verifies this is correct.

An interesting question about the unrolling above is that it does not require the loop invariant to hold on entry to the loop. This is quite a departure from the way we think about verifying While loops (see [here](/2013/01/29/understanding-loop-invariants-in-whiley/) and [here](/2014/05/02/loop-invariants-and-break-statements/) for more on that). In fact, we could still require that the loop invariant holds on entry and the above program would still verify. The question is: *what is the right point for the loop invariant to hold?*

To answer this question, I spent some time looking at similar tools:
   * [VCC](http://research.microsoft.com/en-us/projects/vcc/).  This requires that loop invariants for do/while loops hold on entry (see the [language reference for more](http://vcc.codeplex.com/wikipage?title=Loops%20and%20Invariants&referringTitle=Documentation)).  However, it's interesting to note that the developers have discussed this exact issue themselves, and concluded that it's [best to keep it consistent as for While loops](http://vcc.codeplex.com/discussions/271749).

   * [GPUVerify](http://multicore.doc.ic.ac.uk/tools/GPUVerify/).  This also requires that loop invariants for do/while loops hold on entry (see [the documentation](http://multicore.doc.ic.ac.uk/tools/GPUVerify/docs/advanced_features.html)).

   * [ESC/Java](http://kindsoftware.com/products/opensource/ESCJava2/). This also requires that loop invariants for do/while loops hold on entry (though I had to play around with some examples to be sure of this).

   * [Dafny](http://research.microsoft.com/en-us/projects/dafny/).  This tool does not support do/while loops!

   * [Frama-C](http://frama-c.com/).  This permits loop invariants to hold at the end of the loop.  In fact, it goes even further and allows them to hold at any point within the loop!  (see the [documentation](http://frama-c.com/acsl.html) for more).

   * [Spec#](http://research.microsoft.com/en-us/projects/specsharp/).  This requires loop invariants for do/while loops hold on entry (see [this tutorial](http://specsharp.codeplex.com/wikipage?title=Tutorial)).


Despite this seemingly overwhelming evidence that people believe do/while loop invariants should hold on entry, I'm still not convinced.  The fact is, not requiring them to hold on entry is more flexible (although it may at first be a little confusing).  I think new users can be taught how to understand do/while invariants by building on what they already know. That is, they first learn the standard rule for `while` loops; then, they can move on to do/while loops by, at first, physically unrolling them into `while` loops. Eventually, then, it becomes second nature.

In building the Whiley system, I'm starting to think increasingly about how to go about teaching people to use it. My own experiences (such as above) suggest things are often not straightforward, and care is needed to properly explain the different situations. Currently, I have written a significant chunk of the "[Getting Started with Whiley](https://whiley.org/pdfs/GettingStartedWithWhiley.pdf)" tutorial and this includes a reasonable introduction to verification. However, I think a tutorial dedicated solely to verification is really essential ...

**UPDATE:** SPARK 2014 also permits loop invariants to appear anywhere within the loop body (see [here](http://docs.adacore.com/spark2014-docs/html/ug/spark_2014.html#loop-invariants)); thanks to [Raphael_Amiard](http://www.reddit.com/user/Raphael_Amiard) for that pointer!
