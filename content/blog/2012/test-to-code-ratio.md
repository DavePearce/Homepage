---
date: 2012-03-08
title: "Test to Code Ratio"
draft: false
---

I've just been watching the following talk over on InfoQ: [Software Quality --- You know it when you see it](http://www.infoq.com/presentations/Software-Quality-You-Know-It-when-You-See-It).  Thanks to Craig over at [SoftViz](http://softvis.wordpress.com/) for pointing me to it.  The talk is quite interesting, with the focus being primarily around using innovative visualizations of software to gauge quality.

*But, that's not what I want to talk about*.  Rather, there was one thing in particular the presenter said which I found intriguing.  He was talking about the *test-to-code-ratio ---* the number of *Lines of Production Code (LPC)* versus the number of *Lines of Test Code (LTC)*.  A ratio of e.g. 1:4 indicates that, for every Line of Production Code, there are 4 Lines of Test Code.

Now, here's the thing: *the presenter claimed that for Java (and possibly .Net), the ratio should be roughly 1:1, where as for Ruby it should be around 1:2 or even 1:3*.  I should emphasise that he based these claims on the research of others (although it's not clear exactly who).  And, he went on to discuss the reason for the higher ratio required for Ruby (and other dynamically typed languages): *that the greater expressivity of these languages makes it harder to write tests for them.*

If you follow this blog at all, you'll know I'm a fan of static typing.  In fact, my language, [Whiley](http://whiley.org), is about going even further along that spectrum.  One of the main advantages claimed by proponents is that static typing catches errors ahead of time.  In contrast, many detractors claim that, since static typing only catches a small class of error, you still have to rigorously test your code anyway --- so why burden yourself with static types?  Naturally, then, the above claim about the test-to-code ratio of Java versus Ruby leads to the question: *in looking at the test-to-code ratio, are we also looking at the trade-off between static and dynamic types?* Because, if we are, then it might seem to indicate that, actually, static typing does quite a lot for us.

But, obviously, it's not that simple.  For example, it could well be that Ruby programs are, on average, significantly shorter than their equivalent Java programs.  If this ratio was, say, 1:3 (that is, Java programs are three times longer than Ruby programs) then the burden of having to write more tests wouldn't seem so bad...
