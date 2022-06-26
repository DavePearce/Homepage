---
date: 2010-07-08
title: "Finding Bugs in the Real World"
draft: false
---

There's a really interesting article over on CACM about static analysis in the real world.  Here's the [Link](http://cacm.acm.org/magazines/2010/2/69354-a-few-billion-lines-of-code-later/fulltext).

The article is a report from experiences gained in the commercialisation of a tool by [Coverity](http://www.coverity.com/) which uses [static code analysis](http://wikipedia.org/wiki/static_code_analysis) to find bugs.  The tool applies a set of relatively straight-forward rules to identify problems in code (e.g. that a lock is followed by an unlock).  They need to check millions of lines of code with minimal setup, find as many real errors as possible whilst also minimising the number of *false positives*.  They can't use annotations or specifications, as this requires too much intervention (remember, they're analysing millions of lines of *existing* code).  So, the tool is unsound, but that's OK provided it finds lots of real errors:

> Like               other early static-tool researchers, we benefited from what seems               an empirical law: Assuming you have a reasonable tool, if you run               it over a large, previously unchecked system, you will always               find bugs.

Now, this all sounds fine in theory ... but the real problems stem from more practical and surprising issues:

> The problems that show up when               thousands of programmers use a tool to check hundreds (or even               thousands) of code bases do not show up when you and your               co-authors check only a few.

One of the key challenges seems to be convincing a client to use the tool, which comes down to the "pre-sale demonstration".  The problem is that there isn't much time, and the results need to be really good ... otherwise, they're not going to be interested.  The context of the trial is, of course, a very large program with an ad-hoc build system written by the client.  They'll be expecting you to get to grips with it quickly, and that the tool will work on it with minimal (if any) changes.  The massive variation in build systems, compiler compatibility and just plain wierd stuff you can do with macros and the like in C, made all of this extremely difficult.

Now, the really interesting thing is that, assuming they get past the pre-trial stage then, of course, they're going to start finding bugs in the code.  *That's the point, right?* Well, what happens when the client won't accept they have a bug?
> Arguing reliably kills sales. What to do? One               trick is to try to organize a large meeting so their peers do the               work for you. The more people in the room, the more likely there               is someone very smart and respected and cares (about bugs and               about the given code), can diagnose an error (to counter               arguments it's a false positive), has been burned by a similar               error, loses his/her bonus for errors, or is in another group               (another potential sale).

Anyway, I don't want to spoil it all for you ... it's such great stuff.  The conclusion seems to be that clients are often ignorant of subtle coding issues and are likely to stay that way because of internal politics.  That is, doing something which dramatically increases the number of known defects usually gets someone fired, not promoted ...
