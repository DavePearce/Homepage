---
date: 2020-09-24
title: "The Semantics of Semantic Versioning?"
draft: true
---

Semantic versioning is a surprisingly interesting topic when you get into it.  Recently, myself and a few colleagues have been giving it some thought (and even wrote an [essay on it](https://whileydave.com/publications/ldp20/))!  If you haven't seen it already, check out the [manifesto for semantics versioning](https://semver.org/).  Whilst that provides a nice overview, there is a lot left unsaid.  There are two different perspectives on semantic versioning:

   1) **Downstream**.  This is perhaps the more obvious scenario.  Downstream developers (clients) want access to the library features offered by (upstream) developers!  They also want both *stability* and *protection*.  That is, they don't want future releases of a library to break their code but (ideally) they want to get future releases automatically (e.g. for critical security updates).
   
   2) **Upstream**.  On the flip-side, upstream (library) developers want *flexibility* to continue improving their libraries with new features, refactorings, etc.  They also want to fix bugs and security vulnerabilities as and when they arise.

In some sense, semantic versioning is just a communication mechanism between upstream and downstream developers.  Now, a three point version number is (at best) a low fidelity communication channel.  *But, this post is not about that*.  Rather, it is about figuring out how to make the most of semantic versioning as it is.

### Economics

An important aspect of semantic versioning is *trust*.  Downstream developers must trust upstream developers not to break the protocol (e.g. by putting out minor releases with breaking changes).  When trust is lost, clients become hesitant to upgrade and the _lag_ between a new release and the client upgrading increases.  This makes sense as clients balance the costs of upgrading against their benefits.  For example, if upgrading requires only a few minor tweaks to your code base, but offers important security patches then it seems worth it.  But, when upgrading requires significant changes to your code (e.g. because library developer decided randomly to refactor the API) and the only benefit is some features you don't need --- it doesn't.

_We can view all this through the lens of economic theory and treat it as a market system_.  Then, trustworthy upstream developers should succeed where others fail, etc.  This seems like that's it all sorted out!  But, the reality is different as, unfortunately, mistakes are made all the time by developers we think should be trustworthy (see examples below).  The problem is that the system is not yet efficient because: 

  1) **Downstream developers** have real difficulties determining what the costs and benefits are. 
  
  2) **Upstream developers** cannot easily tell when they inadvertently make breaking changes (more on this below).

*In thinking about this, we're interested in what techniques could be brought to bear on this to make the market system more efficient.*

### Breaking Changes

An important question here is: _what are "breaking changes" anyway?_  Knowing this is somehow key to a smoothly functioning system. Some thoughts:

  * _Should a change of license be considered a breaking change?_
  
  * _Should a bug-fix which changes an implementation's behaviour be considered a breaking change?_
  
  * _Should a change which degrades system performance be considered breaking?_
  
  * _Should a change to the contract of a method be considered breaking?_
 
  * _Should a change in the _purity_ of a method be considered breaking?_

  * _Should a change in the order of elements returned by a method be considered breaking?_

Most of these could be considered breaking changes in certain
situations (i.e. depending on the client). The point is that upstream
developers often ignore such changes when choosing version numbers.
_But, with appropriate tooling, maybe they could._

**TODO:** Talk about examples where a bug-fix caused problems because
clients assumed stuff.

  * Talk about bug-fix which caused problems because clients made
    assumptions. What if a method documentation makes no claims about
    the ordering (i.e. because it has no documentation)?_

### Tooling

[RevAPI](https://revapi.org/) provides food-for-thought here.  If you haven't come across it before, this tool compares two versions of a Jar file and identifies certain kinds of breaking change.  Examples of breaking changes include: reducing the visibility of a method; removing a `public` declaration; or, modifying a `public` class so that it no longer implements some interface.  _This is actually awesome!_ _People should use this stuff all the time!_

Our interest here is not what the tool thinks _are_ breaking changes, but what it _doesn't_.  For example, when a method no longer accepts `null` for some parameter, or moves from [linear to quadractic time](https://en.wikipedia.org/wiki/Time_complexity), or returns the elements of an array in a different order, etc.  Ok, we have to be reasonable --- one tool cannot do everything and these are _hard_ problems.  Still, RevAPI offers a glimmer of hope that semantics versioning could be much more than it currently is.  And, there are others: 
[Elm Bump](https://medium.com/@Max_Goldstein/how-to-publish-an-elm-package-3053b771e545), [rust-semverver](https://github.com/rust-dev-tools/rust-semverver), and [clirr](http://clirr.sourceforge.net/) to name a few.  

_So, there should be tools, and lots of 'em!_  Both upstream _and_ downstream developers should be using them to spot inadvertent breaking changes, or to gauge the cost of upgrades.  Whilst current tools are fairly shallow in their assessment of breaking changes, there is a wealth of techniques from fields like [static analysis](https://en.wikipedia.org/wiki/Static_program_analysis) and automated testing which could be repurposed. 

### Conclusion

Well, that's enough for now!!  If you're interested in this kind of thing, you should check out our [essay](https://whileydave.com/publications/ldp20/) which goes into way more detail.

And finally, just to get you thinking, here are some crazy ideas that people are already thinking about:

* Approaches based on testing existing clients.

* Scan my code base to see whether anything could be affected. Slicing or tree shaking.

* Other static analysis.

### Related Articles

Here are a few related articles on semantic versioning which are definitely worth a read!

   * [Why Semantic Versioning Isn't](https://gist.github.com/jashkenas/cbd2b088e20279ae2c8e)

   * [Semantic Versioning Sucks! Long Live Semantic Versioning](https://developer.okta.com/blog/2019/12/16/semantic-versioning)
