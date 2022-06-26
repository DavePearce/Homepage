---
date: 2010-07-22
title: "Language Designers ... who wait?"
draft: true
---

Someone recently pointed me to the [Rust programming language](http://wiki.github.com/graydon/rust/) which has some very nice features, although it's still in early stage development.  In particular, the system statically prevents [null pointer errors](http://wikipedia.org/wiki/pointer_(computing)#null_pointer), and does not permit [dangling pointers](http://wikipedia.org/wiki/dangling_pointer).  The language FAQ also claims the "ability to define complex invariants that hold over data structures" ... which looks very promising, and I presume is related to the (relatively recent) research on this topic (see e.g. [this](http://doi.acm.org/10.1145/1081706.1081741), [this](http://doi.acm.org/10.1145/1348250.1348255) and [this](http://dx.doi.org/10.1016/j.scico.2005.02.004)).  However, I confess I haven't actually downloaded Rust and tried it yet ... but I will soon enough!

Anyway, it's not the details of the language that really intrigued me.  Rather, it was this comment made in the Rust language FAQ:
> *Why did you do so much work in private?* ... languages designed by committee have a poor track record. Design coherence is important. There were a lot of details to work out and the initial developer (Graydon) had this full time job thing eating up most days.

The thing is, it seems that development has been going on for the last 4 years or so in relative secrecy.  I'm undecided whether or not this is a good thing: part of me thinks it is (since releasing junk too early means people will switch off before the project gets under way); but, part of me thinks it isn't (since it takes a long time to build up a user base, and the sooner you get started the better).

The reason the above comments from the project FAQ stuck out so much, was that I'd recently been reading about [Clojure](http://clojure.org/) and came across this on Rich Hickey's [Wikipedia page](http://en.wikipedia.org/wiki/Rich_Hickey):
> He spent about 2½ years working on Clojure before releasing it to the world

There seems to be something of a parallel here, and I can only imagine his reasons were similar.  I wonder whether this kind of story is true for other languages ...