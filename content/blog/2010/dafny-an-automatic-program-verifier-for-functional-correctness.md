---
date: 2010-06-07
title: "Dafny: An Automatic Program Verifier for Functional Correctness"
draft: false
---

So, last week the pl reading group chose to read [this](http://research.microsoft.com/en-us/um/people/leino/papers/krml203.pdf) paper by Rustan Leino. Ignoring the fact that a number of people in our group are interested in this area, Rustan had recently given a presentation on Dafny here (his slides are [here](http://aswec2010.massey.ac.nz/Leino-ASWEC-2010.pdf)) and this was reason we chose this paper.  Unfortunately, I wasn't here to meet him and compare Whiley with Dafny, as I was in Germany at a Dagstuhl workshop on [Relationships, Objects, Roles and Queries](http://www.dagstuhl.de/de/programm/kalender/semhp/?semnr=10152) (which was great, btw) .  C'est la vie.

Dafny is a really interesting language aimed primarily at getting close to full verification with an automated theorem prover.  The language could be loosely called an object-oriented language.  However, there are a lot of differences.  In particular, it has a flat object hierarchy (everything extends object directly, but no other form of inheritance);  this means there is no real notion of polymorphism, as found in traditional OO languages.  On the other hand, Dafny does support unbounded integers and provides proper first class functions, as well as  sets and sequences.  So, in that sense, it's quite similar to Whiley.

One big difference between the two is that Dafny also aims to support *termination analysis*.  We were speculating in the reading group that this is one of the reasons why it has a flat object hierarchy.  The issue is that termination analysis in the presence of arbitrary polymorphism is difficult.  The problem lies in detecting recursive loops.  While we can require that terminating methods are all annotated with e.g. a **terminates** modifier, and that they follow an appropriate co-/contra-variant typing protocol, this is not enough.  We must also be able to determine when one method may recursively call itself.  In such a case, we need to identify some kind of parameter which will monotonically increase (or decrease) --- to ensure we reach a [fix point](http://en.wikipedia.org/wiki/Fixed_point_combinator).  Dafny does this by generating a static call graph (which is easy enough in the absence of polymorphism) and, for recursive cycles, it requires use of the "decreasing" modifier.  This modifier identifies one (or more?) parameters which must be shown to decrease on every (recursive) iteration.

Anyway, at the moment, Dafny doesn't compile down into anything (e.g. CLR) and so is really just a verification tool, but in the future I'm sure it will.  Finally, there's also an interesting talk I found by Rustan on termination analysis [here](http://channel9.msdn.com/posts/Peli/The-Verification-Corner-Loop-Termination/).  Rustan actually uses Dafny to demonstrate the ideas.

[](http://ecn.channel9.msdn.com/o9/ch9/6/2/9/8/3/5/looptermination_ch9.mp4)
