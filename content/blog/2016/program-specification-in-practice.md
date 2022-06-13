---
date: 2016-09-01
title: "Program Specification in Practice?"
draft: false
---

Recently, as part of our Programming Languages Reading group, we looked at the paper "Contracts in Practice" by Estler *et al.,* (see [here](http://se.ethz.ch/~meyer/publications/methodology/contract_analysis.pdf) for a copy).  This is quite an interesting paper and the authors perform an empirical investigation as to how contracts are used by programmers in practice.  They dig out some data sets based around [JML](https://en.wikipedia.org/wiki/Java_Modeling_Language), [Code Contracts](https://www.microsoft.com/en-us/research/project/code-contracts/) in C# and, of course, Eiffel.  These were pretty extensive and accounted for around 260M Lines Of Code spread throughout about 7700 revisions of the programs in their data sets.

With their experiments, the authors made lots of interesting findings and I'm not going to discuss them all here.  But, as an example, here's one of their findings:

> "The usage of specifications tends to be stable over time, except for the occasional turbulent phases where major refactorings are performed"


This perhaps reflects our intuition about contracts.  Namely, that they describe aspects of the method in question, but are far from complete.  They allow a lot of wiggle room in the implementation and, in many cases, changes in the implementation don't lead to changes in the contract(s).

Now, one of the paper's findings seemed very interesting to me.  Namely this:

> 
> There is no strong preference for certain kinds of specification elements (preconditions, postconditions, class invariants); but preconditions, when they are used, tend to be larger (have more clauses) than postconditions


Somehow, from my perspective, this finding seemed surprising.  So, I thought it would be interesting to take a look at some Whiley code to see how that stacks up.  A good piece that I have (mostly) verified is the `Array` module found in the standard library (see [here](https://github.com/Whiley/WhileyStdLib/blob/master/src/whiley/lang/Array.whiley)).  Here's an example function from that module:

```whiley
public function lastIndexOf(int[] xs, int x) -&gt; (int|null idx)
// If int returned, element at this position matches x
ensures idx is int ==&gt; xs[idx] == x
// If int returned, element at this position is last match
ensures idx is int ==&gt; all { i in (idx+1) .. |xs| | xs[i] != x }
// If null returned, no element in items matches item
ensures idx is null ==&gt; all { i in 0 .. |xs| | xs[i] != x }:
    //
    int i = |xs|
    //
    while i &gt; 0
    where i &lt;= |xs|
    // No element seen so far matches item
    where all { j in i..|xs| | xs[j] != x }:
        //
        i = i - 1
        if xs[i] == x:
            return i
    //
    return null
```

The exact details of the specifications here are not super-important.  But, hopefully you'll notice something interesting straightaway: *there are no preconditions*.  In fact, this is something of a trend in the `Array` module, where almost all functions have many more postconditions than preconditions.  In fact, of the 9 functions in that module which are specified, the average number of precondition clauses is `0.6` compared with `3.0` for postcondition clauses.  That's quite a big difference!

Of course, this is not a scientific study.  But, it was interesting nonetheless.  And, it means I should really spend more time looking at other benchmarks written in Whiley...
