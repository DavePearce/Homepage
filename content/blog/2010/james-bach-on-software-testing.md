---
date: 2010-09-08
title: "James Bach on Software Testing"
draft: true
---

I've just been watching this YouTube presentation by [James Bach](http://www.satisfice.com/blog/):


[object]
[param]
[/param]
[param]
[/param]
[embed]
[/embed]
[/object]


James has a very tongue-in-cheek style, which I rather like, and he's obviously not a great fan of the academic establishment:
> "Testing is not part of Computer Science.  Computer Science likes to think that testing is part of Computer Science.  But, if you look at a Computer Science journal, and  you look at articles  on testing, it's like somebody treating cacti as the whole of vegetative live on the planet."

Great stuff!  And he goes on, saying how the very narrow band of topics covered in such papers don't even scratch the surface.

Anyway, I suppose the reason I'm particularly interested in this, is that testing is on my radar right now.  Over the last few months, I've been rebuilding the Whiley compiler almost from scratch.  The test cases that I generated with the previous version have really been the stuff legends are made of.  I just couldn't have done it without them.  After months of work, I'm finally getting back to the point where the number of test cases which pass is (almost) as many as passed in the old version ... !!  Of course, the theory is that I can now go way beyond what I had before ... but only time will tell if that's really true.

When you have a lot of test cases, you start to have a lot of questions.  Running through the test cases takes a while, so naturally you want to know *whether there is any redundancy in the tests*.  Fewer tests means fewer seconds wasted waiting to run through them.  *But how to find this redundancy?* I don't really have an answer to this.  Sure, I could eyeball them, make a call that two tests are "essentially the same", and delete one.  But, I don't do this.  Because, I know how hard it is to make the right call.  Typically, I write a test to test one thing, and it ends up testing heaps of other *really useful things*.

Another useful plan is to have multiple test suites.  A "full" suite which, maybe, runs over night; then, a "small" suite for day-to-day development.  And, perhaps maybe a middling one as well, just for good measure.  *But, how do I sample the tests to make the smaller suites?* Again, no idea.  Good tooling could really make a difference to all these problems ...