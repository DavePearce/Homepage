---
date: 2013-04-21
title: "Iso-Recursive versus Equi-Recursive Types"
draft: true
---

An important component of the Whiley language is the use of <a href="http://en.wikipedia.org/wiki/Recursive_data_type">recursive data types</a>.  Whilst these are similar to the <a href="http://en.wikipedia.org/wiki/Algebraic_data_type">algebraic data types</a> found in languages like Haskell, they are also more powerful since Whiley employs a <a href="http://en.wikipedia.org/wiki/Structural_type_system">structural type system</a>. <em>So, what does all this mean? </em>Well, let's look at an example:

[whiley]
define IntList as null | {int data, IntList next}
define AnyList as null | {any data, AnyList next}

AnyList f(IntList ls):
    return ls
[/whiley]

Here, we've got two recursive data types which describe something akin to a linked list. For example, an <code>IntList</code> describes a recursive structure made up of zero or more nodes. Each node contains an <code>int data</code> field, and a <code>next</code> field to access either the next node, or <code>null</code> if the end is reached. An <code>AnyList</code> is very similar, except that its payload consists of arbitrary data, rather than just integer data (as for <code>IntList</code>).

In the above example, we see that the parameter <code>ls</code> is returned without an explicit cast. In other words, we know that <code>IntList</code> is a subtype of <code>AnyList</code>. This is a key difference from standard algebraic data types, where <code>IntList</code> and <code>AnyList</code> would always be considered distinct (i.e. unrelated) types.
<h2>Subtyping Recursive Types</h2>
The ability to have implicit subtyping relationships between recursive data types is a key strength compared with algebraic data types.  At the same time, it also presents a complex algorithmic challenge and numerous approaches have been proposed in the literature.  Previously, I have written extensively on this subject (see e.g. <a href="http://whiley.org/2011/02/16/minimising-recursive-data-types/">here</a>, <a href="http://whiley.org/2011/03/07/implementing-structural-types/">here</a> and <a href="http://whiley.org/2011/08/30/simplification-vs-minimisation-of-types-in-whiley/">here</a>).  In fact, there are two broad approaches taken to subtyping recursive data types: <em>iso-recursive</em> and <em>equi-recursive</em>.  In Whiley, and my previous writings on this topic, I have strictly followed the equi-recursive approach and I would strongly recommend this to anyone developing a recursive type system.

A good account of the iso- versus equi-recursive approaches can be found in <a href="http://www.cis.upenn.edu/~bcpierce/tapl/">Pierce's excellent book</a> [1].  The key difference between the two approaches is whether the recursion is "implicit" (equi-recursive) or "explicit" (iso-recursive).  In the equi-recursive approach, types are implemented under-the-hood as directed graphs where recursion corresponds to a cycle in the graph (see <a href="http://whiley.org/2011/02/16/minimising-recursive-data-types/">here</a> for an example).  In the iso-recursive approach, special types of the form <code>μX.T</code> are used (the so-called "mu" types). In such a type, <code>X</code> is a recursive variable used within the body <code>T</code>. For example, a mu type corresponding to our <code>IntList</code> example is: <code>μX.(null | {int data, X next})</code>.

Mu types can be "folded" and "unfolded".  To unfold a type <code>&mu;X.T</code> we generate the type <code>T[X/&mu;X.T]</code> (that's <code>T</code> with <code>X</code> replaced by <code>&mu;X.T</code>).  For example, unfolding <code>μX.(null | {int data, X next})</code> gives <code>null | {int data, μX.(null | {int data, X next}) next}</code>. Explicit operators for unfolding and folding are provided for manipulating types where <code>fold(unfold(T)) = T</code> holds for any type <code>T</code>. In the iso-recursive scheme (and unlike the equi-recursive scheme), a type and its unfolding are distinct and unrelated. To show that one mu type subtypes another, we must first fold/unfold them to have the same recursive structure, after which we can establish the subtyping relation via the so-called "Amber Rule" (see e.g. [1,2] for more on this).  

<h2>Subtyping Iso-Recursive Types</h2>

Having considered how subtyping is performed for iso-recursive types, the question is: <em>for two types which should be related, can we always fold/unfold them to reach a matching recursive structure?</em> Well, I believe the answer is no.  Here's my informal proof:
[whiley]
define LTree as null | { int data, LTree left, LTree right}
define RTree as null | { int data, RTree_b left, RTree right}
define RTree_b as null | { int data, RTree left, RTree right}
[/whiley]
Now, we have to ask ourselves the question: is there a sequence of fold/unfold operations that will transform <code>LTree</code> into <code>RTree</code> (i.e. to show that they are equivalent)?  To see why this is impossible, we consider the notion of "balance" (as in <em>balanced tree</em>). After any number of fold / unfold operations the <code>LTree</code> type will remain balanced; but, for the <code>RTree</code> type, it will never be balanced. 

For a more detailed investigation into the expressiveness of iso-recursive types, I'd suggest looking at this <a href="http://www.cse.usf.edu/~ligatti/papers/subIsoTR.pdf">recent paper</a> [2].

<h2>References</h2>
<ol>
	<li><b>Types and Programming Languages</b>, <a href="http://www.cis.upenn.edu/%7Ebcpierce">Benjamin C. Pierce. </a>The MIT Press, ISBN 0-262-16209-1.</li>
	<li><a href="http://www.cse.usf.edu/~ligatti/papers/subIsoTR.pdf">Completely Subtyping Iso-recursive Types</a>, Technical Report CSE-071012, Jeremy Blackburn Ivory Hernandez Jay Ligatti Michael Nachtigal, University of South Florida.</li>
</ol>
