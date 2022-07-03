---
date: 2011-01-26
title: "The Case Against Structural Subtyping ... ?"
draft: true
---

My previous post on <a href="http://whiley.org/2010/12/13/why-not-use-structural-subtyping/">structural subtyping</a> generated quite a few <a href="http://www.reddit.com/r/programming/comments/ekmtf/why_dont_more_languages_use_structural_subtyping/">comments</a> over on <a href="http://reddit.com">reddit</a>.  There were a lot of mixed opinions as to the pros and cons of having a [[structural type system]] instead of a [[Nominal typing|nominal type system]]. To help me digest and ponder it all, I thought I'd discuss the main themes here in more detail.
<h2>Issues</h2>
I'll start by looking at the various issues people highlighted with structural subtyping, and provide a few comments of my own later.
<ul>
	<li><strong>Programmer Intent</strong>.  Perhaps the biggest problem raised with structural subtyping is that programmer intent may be lost.  In a nominal type system, the names of types captures their intent (to some extent), and the system prevents information flows that don't make sense. In Java, one might create a class <code>Length</code> with an <code>int amount</code> field, which holds the length in centimeters.  In a nominal type system, an instance of a different class (say <code>Time</code>) which happens to have an <code>int amount</code> field as well, cannot flow into a variable of type <code>Length</code>.  Thus, one cannot accidentally mix up lengths and times.  With a structural subtyping system, this is not true because an instance of a structure with a field <code>int amount</code> can be used interchangeably as a <code>Length</code> or a <code>Time</code>.</li>
	<li><strong>Invariants</strong>.  Another important problem is that structural subtyping makes it harder to enforce invariants over data structures.  Suppose we're implementing a <code>Date</code> class in a language with nominal typing, like Java.  We might have fields <code>day</code>, <code>month</code> and <code>year</code> which work as expected.  There are several  invariants amongst these fields, such as <code>1 &lt;= day &lt;= 31</code> and, <code>1 &lt;= month &lt;= 12</code> (and these can obviously be refined, e.g. <code>1 &lt;= day &lt;= 28 if month == 2</code>).  In a language like Java, we can easily ensure these invariants are enforced by making the fields private and adding specific getters, and setters which specifically check against invalid values.  In a structural subtyping system, it's not clear exactly how one would enforce such invariants and still retain the advantages of structural typing.  We can provide some kind of data-hiding mechanism in the language to ensure access to fields is controlled --- but this rather defeats the purpose of structural typing as objects are no longer easily interchanged.</li>
	<li><strong>Performance</strong>.  Another cited issue with structural subtyping is that it may incur a performance hit.  The argument is that, if you cannot determine the static offsets of all fields in a structure, then you are forced to employ some kind of dictionary (i.e. [[hash table]]) lookup on every field access.   See <a href="http://whiley.org/2011/01/14/one-approach-efficient-structural-subtyping/">my earlier post</a> for more on this problem.  <strong>Note</strong>, this problem is similar, for example, to that of implementing Java interfaces efficiently (see e.g. <a href="http://domino.research.ibm.com/comm/research_people.nsf/pages/dgrove.hpcn01.html">this</a>).</li>
	<li><strong>Error Messages</strong>.  Generating error messages in a structural type system is something of a challenge.  This is because, in general, you can only report the entire structure involved, rather than just report its name (since structures have no name).</li>
</ul>
<h2>Comments</h2>
In my opinion, many of the issues raised above can be adequately resolved with a little bit of care and thought.  Let's consider the easy ones first:
<ul>
	<li><strong>Programmer Intent.</strong> Whilst I agree this is an issue, units of measure in languages like Java are often passed around simply as <code>int</code>s anyway;  also, we can protect ourselves by using more meaningful field names (e.g. <code>amountInCms</code>, instead of <code>amount</code>)   or even by using [[Type system#Existential_types|existential types]]  in  situations where we are concerned about potential mix ups (see <a href="http://www.cs.utexas.edu/%7Ewcook/Drafts/2009/essay.pdf">this paper</a> for more).</li>
	<li><strong>Performance</strong>.  Whilst there may be some performance hit, it is likely to be negligible for a well engineered language.  In particular, the approach discussed in <a href="http://whiley.org/2011/01/14/one-approach-efficient-structural-subtyping/">this post</a>, including the comment made by Daniel Yokomizo will go quite a way towards minimising overhead.</li>
	<li><strong>Error Messages</strong>.  Whilst I have encountered this problem myself during development of Whiley, I don't think it is hard to fix.  My current solution is to retain nominal information from <code>define</code> statements, and use that purely for error reporting.  There are some problematic issues here, however.  For example: [whiley]
define T1 as int
define T2 as int

int f(T1 x, T2 y):
   if x &gt; y:
      z = x
   else:
      z = y
   // what nominal info to retain here?
   return z
[/whiley]

The problem here is that we want to retain some nominal type information for the variable <code>z</code> --- either <code>T1</code> or <code>T2</code>.  After the <code>if</code> statement, we must either choose one name to retain, or retain both using some kind of union.
A similar issue is that, when variables are assigned raw values there is no possible nominal information we can retain.  However, in such circumstances, it's unlikely that a particularly complex structure is being assigned --- meaning the type will be fairly simple anyway,</li>
</ul>
Now, the issue of maintaing invariants in a structural subtyping system appears (to me at least) to be the hardest of all.  Here's my take on it:
<ul>
	<li><strong>Invariants</strong>.  One obvious approach here is to use some kind of [[Type system#Existential_types|existential type]] to implement information hiding  (again, see <a href="http://www.cs.utexas.edu/%7Ewcook/Drafts/2009/essay.pdf">this paper</a> for more on this).  What this does, is to provide a mechanism whereby we can hide the fields for part or all a record.  This means the fields require getters and setters, and invariants can be enforced through them (i.e. in exactly the same way as for a nominal type system; indeed, the only real advantage of using existential types over nominal types here is that we can expose some parts of a record and they will then be structurally subtyped).  <br><br><em>Neither of these two solutions are really satisfying for me!</em>  Now, all of this discussion (from my perspective at least) is in the context of the <a href="http://whiley.org">Whiley</a> language.  The aim of this language is to make invariants first-class entities which are checked at compile-time by the compiler.  In such a setting, the invariants can be explicitly written as part of the structural type, thereby eliminating this problem altogether!  For example, with the <code>Date</code> class from before, we might have: [whiley]
define Date as { int day,int month,int year } where
 0&lt;=day &amp;&amp; day&lt;=31 &amp;&amp; 0&lt;=month &amp;&amp; month&lt;=12 &amp;&amp; ...
[/whiley]

The beauty of this, is that we can now only interchange <code>Date</code>s with structures that have suitable invariants as well.  However, <em>the invariants need not match exactly</em>.  For example:

[whiley]
// a date with no invariant
define DumbDate as { int day, int month, int year }
// a date in Februrary
define FebDate as Date where $.month == 2

DumbDate f(Date x):
    return x

DumbDate g(FebDate y):
    return f(y)
[/whiley]

Here, we see that records can flow into variables requiring structural subtypes with invariants <em>which are no stricter</em>.  This gives an interesting advantage over the nominal type solution to this problem... </li>
</ul>
Righto, that's enough thinking for now!!