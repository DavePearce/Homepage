---
date: 2013-01-29
title: "Understanding Loop Invariants in Whiley"
draft: true
---

In this article, I'll look at a common problem one encounters when verifying programs: namely, <em>writing <a href="http://en.wikipedia.org/wiki/Loop_invariant">loop invariants</a></em>.  In short, a loop invariant is a property of the loop which:
<ol>
	<li>holds on entry to the loop;</li>
	<li>holds after the loop body is executed;</li>
	<li>holds when the loop finishes.</li>
</ol>
Loop invariants can be tricky to get right but, without them, the verification will probably fail.  Let's consider a very simple example:

[whiley]
define nat as int where $ &gt;= 0

nat counter(int count):
    i = 0
    while i &lt; count:
        i = i + 1
    return i
[/whiley]

This program does not verify.  In order to get it to verify, we need to add a loop invariant.  The need for loop invariants arises from <a href="http://en.wikipedia.org/wiki/Hoare_logic">Hoare's rule</a> for <code>while</code>-loops.  The key issue is that the verifier <em>does not know anything about any variable modified within a loop, other than what the loop condition and/or invariant states</em>.

In our example above, the loop condition only tells us that <code>i &lt; count</code> during the loop, and that <code>i &gt;= count</code> after the loop (in fact, we can be more precise here but the verifier cannot).  Knowing that <code>i &gt;= count</code> is not enough to prove the function's post-condition (i.e. that <code>i &gt;= 0</code>).  This is because <code>count</code> is an arbitrary <code>int</code> which, for example, may be <em>negative</em>.

Therefore, to get our example to verify, we need a loop invariant that explicitly states <code>i</code> cannot be negative:

[whiley]
nat counter(int count):
    i = 0
    while i &lt; count where i &gt;= 0:
        i = i + 1
    return i
[/whiley]

The loop invariant is specified on the <code>while</code> loop with the <code>where</code> keyword.  In this case, it simply states that <code>i</code> is always <code>&gt;=0</code>.  Whilst this might seem obvious to us, it is unfortunately not so obvious to the verifier!  In principle, we could employ a simple form of <a href="http://en.wikipedia.org/wiki/Static_program_analysis">static analysis</a> to infer this loop invariant (although, currently, Whiley does not do this).  Unfortunately, in general, we will need to write loop invariants ourselves.

To explore a slightly more complex example, I've put together a short video which illustrates using Whiley to verify a program which sums a list of natural numbers:

<center><iframe width="420" height="315" src="http://www.youtube.com/embed/WwnxHugabrw" frameborder="0" allowfullscreen></iframe></center>

Finally, if you're interested in trying this out for yourself, the easiest way to install Whiley is through the <a href="http://whiley.org/tools/wyclipse/">Eclipse Plugin</a>. Have fun!