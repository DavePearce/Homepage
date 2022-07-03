---
date: 2012-01-18
title: "Connecting the Dots on the Future of Programming Languages"
draft: true
---

Yesterday, I serendipitously came across two things which got me thinking about the future of programming languages:
<ol>
	<li>The first was an excellent article entitled <a href="http://herbsutter.com/welcome-to-the-jungle/">"Welcome to the Hardware Jungle"</a> by Herb Sutter.  This article is about the coming advent our multicore overlords.  Whilst this might sound like something you've heard before, it's actually well worth the read.  His argument is that heterogeneous massively-multicore computing is fast becoming the norm, and there is no turning back.  I found the article quite scary, as I can't imagine programming in the extreme environment suggested.  I also have to question whether everyday applications will really benefit from massive multicore.  But, clearly, I can see that quite a few will.</li>
	<li>The second was the following (short) youtube video of [[Simon Peyton Jones]] and [[Erik Meijer]] discussing the space of programming languages: <p><center><iframe width="420" height="315" src="http://www.youtube.com/embed/iSmkqocn0oQ?feature=player_embedded" frameborder="0" allowfullscreen></iframe></center></p> Simon talks about how both imperative and functional programming languages are trying to reach some kind of "Nirvana", but coming at it from different directions.</li>
</ol>
Now, the question is: <em>how do they connect together?</em> Well, essentially, I think the "Nirvana" space the Simon talks about is exactly the space we need to deal with the Hardware Jungle.  In other words, I think it's the space most suitable for distributed and parallel computing.

<em>What do we know about this “Nirvana” space?</em> In the video, Simon talks about <em>safe</em> and <em>unsafe</em> languages. He says explicitly that safe means having limited or highly controlled <a href="http://en.wikipedia.org/wiki/Side_effect_%28computer_science%29">side-effects</a>. Languages which are unsafe by his categorisation include Java, C#, C, and the majority of languages traditionally thought of as imperative or object oriented. They are unsafe because one cannot reason about the result of any given method in isolation from others. That is, methods may read/write shared state (via the heap) and, to reason about them, we must know: (1) what shared state is actually accessed; (2) what value it has when it is accessed. These three requirements make it very difficult know what’s going on, particularly if something else could modify the state at any point. More importantly, I believe, <em>is that these requirements make it very difficult for the compiler to reason about what’s going on</em>.

<em>What does this have to do with the Hardware Jungle?</em>  Well, I believe there are two important points here:
<ol>
	<li>To move the right data for a given computation onto the right core, we must know exactly what state that computation will access.</li>
	<li>For massively multi-core systems, Humans will not be capable of optimally mapping resources onto cores.  We will increasingly rely on sophisticated algorithms to do this for us.  Typically, such algorithms will be embedded in the compiler and/or runtime system.</li>
</ol>

These two points taken together imply it must be possible to automatically determine what state a given computation will access.  <em>The easiest way of doing this is to aggressively restrict the state that can be accessed by requiring <a href="http://en.wikipedia.org/wiki/Functional_programming#Pure_functions">pure functions</a></em>.  Furthermore, I believe it is not sufficient to rely on the programmer to ensure his/her functions are pure --- the compiler must do this for us.  This is because race-conditions are already notoriously difficult to debug <em>and in the Hardware Jungle things will get seriously crazy</em>.

This leads me to the final and, I think, most important question:

<blockquote>
Which mainstream programming languages currently support pure functions and/or other mechanisms for aggressively limiting side-effects? 
</blockquote>

[[Haskell (programming language)|Haskell]] is clearly one example, [[D (programming language)|D]] is another.  <em>But, what else?</em>  


