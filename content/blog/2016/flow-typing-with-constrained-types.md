---
date: 2016-08-03
title: "Flow Typing with Constrained Types"
draft: true
---

<a href="https://en.wikipedia.org/wiki/Flow-sensitive_typing">Flow-sensitive typing</a> (a.k.a. "Flow Typing") is definitely getting more popular these days. <a href="https://en.wikipedia.org/wiki/Ceylon_(programming_language)">Ceylon</a>, <a href="https://en.wikipedia.org/wiki/Kotlin_(programming_language)">Kotlin</a>, <a href="https://en.wikipedia.org/wiki/TypeScript">TypeScript</a>, <a href="https://en.wikipedia.org/wiki/Racket_(programming_language)">Racket</a>, <a href="https://en.wikipedia.org/wiki/Whiley_(programming_language)">Whiley</a> all support flow typing in some form. Then, of course, there's <a href="https://github.com/facebook/flow/wiki">Facebook Flow</a> and the list goes on!

Recently, I've made some fairly major updates to the internals of the Whiley compiler (basically, redesigning the intermediate language). In doing so, I came across an interesting problem which I wanted to get down on paper. The problem arises when flow typing meets constrained types (a.k.a. types with invariants). <em>What does a constrained type look like?</em> Here's an example in Whiley:

[whiley]
type nat is (int x) where x &gt;= 0
[/whiley]

This defines a type <code>nat</code> which contain all integer values <code>x</code>, where <code>x &gt;= 0</code>. Constrained types are quite powerful and Whiley attempts to seamlessly integrate them with flow typing. Here's a simple program to illustrate:

[whiley]
function abs(int x) -&gt; (nat r):
    if x &gt;= 0:
        return x
    else:
        return -x
[/whiley]

The type of <code>x</code> is initially <code>int</code>. On the true branch, we know <code>x &gt;= 0</code> and (roughly speaking) the compiler automatically promotes <code>x</code> to type <code>nat</code> as needed.
<h2>The Problem</h2>
An interesting challenge arises within the compiler when reasoning about constrained types and flow typing. To understand, we need to consider how flow typing works in general. The following gives a rough outline:

[whiley]
S x = ...

if x is T:
   ...
else:
   ...
[/whiley]

Here, <code>S</code> and <code>T</code> are some arbitrary types where <code>T</code> is a subtype of <code>S</code>. The compiler <em>retypes</em> variable <code>x</code> on the true branch to have type <code>S &amp; T</code> and, on the false branch, to type <code>S &amp; !T</code> (which you can think of as <code>S - T</code>). To make it concrete, consider the case for <code>int|null</code> and <code>int</code>:

[whiley]
int|null x = ...

if x is int:
   ...
else:
   ...
[/whiley]

On the true branch, <code>x</code> has type <code>(int|null)&amp;int</code> (which reduces to <code>int</code>) and, on the false branch, it has type <code>(int|null)&amp;!int</code> (which reduces to <code>null</code>).

The basic plan outlined above works pretty well, but things get interesting with constrained types. For example, let's use <code>nat</code> for the type test instead of <code>int</code> above:

[whiley]
int|null x = ...

if x is nat:
   ...
else:
   ...
[/whiley]

The type of <code>x</code> on the true branch is <code>(int|null)&amp;nat</code>, <em>but what does this reduce to?</em> A simple idea is to replace <code>nat</code> with its <em>underlying type</em> (i.e. <code>int</code>). We know this works as it's exactly what we had before. <em>But, what about the false branch?</em> Reducing <code>(int|null)&amp;!nat</code> in this way gives us <code>null</code> as before which, unfortunately, is wrong. The problem is that, on the false branch, <code>x</code> can still hold values of <code>int</code> type (i.e. <em>negative</em> values).

<h2>Mitigating Factors</h2>
The Whiley compiler already reasons correctly about flow typing in the presence of arbitrary conditionals. For example, consider this variant on our example from before:

[whiley]
int|null x = ...

if x is int &amp;&amp; x &gt;= 0:
   ...
else:
   ...
[/whiley]

In this case, the Whiley compiler will correctly conclude that <code>x</code> has type <code>int|null</code> on the false branch. <em>Then why not just expand constrained types like this and be done?</em> That's a good question. The answer is that, if we expand types in this way, <em>we lose nominal information about them</em>. For example, we'd lose the connection between <code>x</code> and type <code>nat</code> above, as <code>x</code>'s type on the either branch would be in terms of <code>int</code> and <code>null</code> only.

<em>So, do we really need this nominal information?</em> The answer is, technically speaking, no we don't.  Expanding types in this way is how the Whiley compiler currently works. But, nominal information helps with providing good error messages and, turns out, that's important!

<h2>The Solution?</h2>
My proposed solution stems from ideas currently being used in the Whiley compiler, namely the concept of <em>maximal</em> and <em>minimal</em> consumption of types. The idea is that the maximal consumption of a type is the largest set of values it could consume. For type <code>nat</code>, the maximal consumption is <code>int</code>. The minimal consumption is the exact oppostite --- the smallest set of values it must consume. For type <code>nat</code>, this is <code>void</code> because <code>nat</code> does not consume all possible integers. Note that the minimal consumption is not always <code>void</code>. For example, the minimal consumption for <code>null|nat</code> is <code>null</code> because <code>null</code> values are <em>always</em> consumed.

This probably seems a little confusing right now, but it will start to make sense! The key idea behind my solution is the introduction of two new operators over types, namely <code>⌈T⌉</code> (ceiling) and <code>⌊T⌋</code> (floor) for representing maximal and minimal consumption for a type <code>T</code>. With these, we can now correctly type our program from before <em>without losing nominal information</em>:

[whiley]
int|null x = ...

if x is nat:
   ...
else:
   ...
[/whiley]

On the true branch, <code>x</code> is given the type <code>(int|null) &amp; ⌈nat⌉</code>, whilst on the false branch it's given the type <code>(int|null) &amp; !⌊nat⌋</code>. Here, the underlying type for <code>(int|null) &amp; ⌈nat⌉</code> is <code>int</code>, whilst for <code>(int|null) &amp; !⌊nat⌋</code> it's <code>int|null</code>.

The point of these new operators is that they allow us to delay calculating the underlying type for <code>x</code> <em>until we need it</em>. In other words, they allow us to retain nominal information for as long as possible.

<h2>Observations</h2>

These two operators are interesting and it turns out there are few observations we can make about them:

<ul>
	<li>For any <strong>primitive type</strong> <code>T</code>, we have that <code>T</code> is equivalent to both <code>⌊T⌋</code> and <code>⌈T⌉</code>.</li>

	<li>For any <strong>negation type</strong> <code>!T</code>, we have that <code>⌊!T⌋</code> is equivalent to  <code>!⌈T⌉</code> and <code>⌈!T⌉</code> is equivalent to  <code>!⌊T⌋</code>.</li>

        <li>For any <strong>union type</strong> <code>T1 || T2</code>, we have that <code>⌊T1 || T2⌋</code> is equivalent to <code>⌊T1⌋ || ⌊T2⌋</code>, whilst <code>⌈T1 || T2⌉</code> is equivalent to <code>⌈T1⌉ || ⌈T2⌉</code>.</li>

        <li>For any <strong>intersection type</strong> <code>T1 && T2</code>, we have that <code>⌊T1 && T2⌋</code> is equivalent to <code>⌊T1⌋ && ⌊T2⌋</code>, whilst <code>⌈T1 && T2⌉</code> is equivalent to <code>⌈T1⌉ || ⌈T2⌉</code>.</li>

        <li>For any <strong>nominal type</strong> <code>N</code> declared as <code>T where e</code>, we have that <code>⌊N⌋</code> is equivalent to <code>void</code> and <code>⌈T⌉</code> is equivalent to <code>T</code>.</li>

</ul>

<h2>Conclusion</h2>

Using these two new operators provides a simple way to reason about flow typing over constrained types.  The next job for me is to implement this within the Whiley compiler!

<h2>References</h2>
Here's an interesting paper on constrained types:
<ul>
	<li><b>Constrained types for object-oriented languages</b>, Nathaniel Nystrom, Vijay Saraswat, Jens Palsberg, Christian Grothoff. In <em>Proceedings of OOPSLA</em>, 2008. (<a href="http://dl.acm.org/citation.cfm?id=1449800">LINK</a>)</li>
</ul>