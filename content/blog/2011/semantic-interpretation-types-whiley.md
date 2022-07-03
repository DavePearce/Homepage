---
date: 2011-07-29
title: "A Semantic Interpretation of Types in Whiley"
draft: true
---

An interesting and intuitive way of thinking about a type system is using a <em>semantic interpretation</em>.  Typically, a set-theoretic model is used where a type <code>T</code> is a subtype of <code>S</code> iff every element in the set described by <code>T</code> is in the set described by <code>S</code>.
<h2>The Semantic Model</h2>
The starting point is to define our notion of types <code>T</code> and values <code>V</code>:
<ul>
	<li><code>T ::= null | int | any | [T] | {T1 f1, ... Tn fn} | T1 ∨ T2</code></li>
</ul>
<ul>
	<li><code>V ::= null | i | [V1,...,Vn] | {f1: V1, ... f2: V2}</code></li>
</ul>
This is a simplified notion of the types and values found in Whiley.  For example, I've left out sets and function references and ignored recursive types altogether.

We can define our semantic model as follows using an acceptance relation <code>T</code> |= <code>V</code>, which holds if value <code>V</code> is in the set described by type <code>T</code>.
<ul>
	<li><code>null</code> |= <code>null</code></li>
	<li><code>any</code> |= <code>V</code></li>
	<li><code>int</code> |= <code>i</code>, <strong>if</strong> i ∈ <em>I</em> (the set of all integers)</li>
	<li><code>[T]</code> |= <code>[V1,...Vn]</code>, <strong>if</strong> ∀1≤i≤n.[<code>T</code> |= <code>Vi</code>]</li>
	<li><code>{T1 f1, ..., Tn fn}</code> |= <code>{f1: V1, ... fn: Vn}</code>, <strong>if</strong> <code>T1</code> |= <code>V1</code>, ... <code>Tn</code> |= <code>Vn</code></li>
	<li><code>T1 ∨ T2 </code> |= <code>V</code>, <strong>if</strong> <code>T1</code> |= <code>V</code> <strong>or</strong> <code>T2</code> |= <code>V</code></li>
</ul>
<strong>Note:</strong> this model could be made more advanced by supporting <a href="http://en.wikipedia.org/wiki/Subtype_polymorphism#Record_types">width subtyping</a> --- but its enough for now.

Finally, we can give a semantic notion of subtyping where <code>T1</code> |= <code>T2</code> holds if ∀<code>V</code>.[<code>T1</code> |= <code>V</code> <strong>implies</strong> <code>T2</code> |= <code>V</code>].  In otherwords, <code>T1</code> |= <code>T2</code> if <code>T1</code> is a subtype of <code>T2</code>.
<h2>The Subtyping Algorithm</h2>
Now that we have an "intuitive" model of what types should mean, we want to compare that against an actual algorithm for subtype testing.  The following pseudo-code outlines the basic algorithm used in Whiley:

[whiley]
// Check whether t1 is a subtype of t2
bool isSubtype(Type t1, Type t2):
   // rule 1
   if t2 is any:
       return true
   // rule 2
   else if t1 == t2:
       return true
   // rule 3
   else if t1 is [t3] &amp;&amp; t2 is [t4]:
       return isSubtype(t3,t4)
   // rule 4
   else if t1 is {t3 f3, ..., Tn fn} &amp;&amp;
            t2 is {s3 f3, ..., Sn fn}:
       for i in 3..n:
           if !isSubtype(ti,si):
               return false
       return true
   // rule 5
   else if t1 is (t3 ∨ t4):
       return isSubtype(t3,t2) &amp;&amp; isSubtype(t4,t2)
   // rule 6
   else if t2 is (t3 ∨ t4):
       return isSubtype(t1,t3) || isSubtype(t1,t4)
   // rule 7
   else:
       return false
[/whiley]

Thus, for example, <code>isSubtype(int,any)</code> holds under rule 1, whilst <code>isSubtype(int,int ∨ null)</code> holds by rules 6+2.
<h2>The Question</h2>
<center><em>Is the subtyping algorithm sound and complete with respect to our semantic model?</em></center>
In some sense, the whole point of the semantic model is to let us ask this question.  We can break this down into two separate questions of <em>soundness</em> and <em>completeness</em>:
<blockquote><strong>Soundness.</strong> If <code>isSubtype(T1,T2)</code> then <code>T1</code> |= <code>T2</code>.</blockquote>
<blockquote><strong>Completeness.</strong> If <code>T1</code> |= <code>T2</code> then <code>isSubtype(T1,T2)</code>.</blockquote>
Considering these questions separately simplifies the problem.  I'm not going to provide any proofs, but it's relatively easy to see that <code>isSubtype()</code> is sound.  The more interesting question is whether or not it is complete.

In fact, it turns out that the <code>isSubtype()</code> algorithm as given is <em>not complete</em>.  A simple counter-example is sufficient to show this.  Let <code>T1</code> = <code>{int ∨ null x}</code> and <code>T2</code> = <code>{int x} ∨ {null x}</code> .  Then, <code>T1</code> |= <code>T2</code>, but <code>isSubtype(T1,T2)</code> does not hold.  This is because rule 6 requires <code>isSubtype(T1,{int x})</code> and <code>isSubtype(T1,{null x})</code> (neither of which hold).

The problem is that <code>isSubtype()</code> is not <em>distributive</em> over records.  An interesting question is how we can fix it, but that's a story for another day!

If you're interested in learning more about this, I've worked through the full system in <a href="http://homepages.ecs.vuw.ac.nz/~djp/files/ECSTR10-23.pdf">this paper</a>.  Also, the following reference provides a good introduction to semantic subtyping:
<ul>
	<li>"<strong>A Gentle Introduction to Semantic Subtyping"</strong>, Giuseppe Castagna and Alain Frisch.  In Proceedings of the ACM Conference on Principles and practice of declarative programming (PPDP), 2005. [<a href="http://portal.acm.org/citation.cfm?id=1069793">ACM Link</a>][<a href="http://citeseerx.ist.psu.edu/viewdoc/download?doi=10.1.1.65.8026&rep=rep1&type=pdf">PDF</a>]</li>
</ul>