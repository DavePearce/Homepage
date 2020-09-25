---
date: 2020-09-24
title: "The Semantics of Sematic Versioning?"
draft: true
---

Semantic versioning is a surprisingly interesting topic when you get into it.  Recently, myself and a few colleagues have been giving it quite a bit of thought (and even wrote an [essay on it](https://whileydave.com/publications/ldp20/))!  To start with, if you haven't seen it already, check out the [manifesto for semantics versioning](https://semver.org/).  Whilst that provides quite a good overview, there is still quite a lot that's left unsaid.  There are two basic perspectives that semantic version is supposed to help with:

   1) **Downstream**.  This is perhaps the more obvious scenario.  Downstream developers (clients) want access to the library features offered by (upstream) dependencies!  But, they also want both *stability* and *protection*.  That is, they don't want future releases of a library to break their code but (ideally) they want to get future releases automatically when they contain critical (e.g. security) updates.
   
   2) **Upstream**.  On the flip-side, upstream (library) developers want *flexibility* to continue improving their libraries as necessary to add new features, or address past mistakes.  They also want to fix bugs and apply other critical updates as and when they arise.

At its most basic level, semantic versioning is simply a communication mechanism between upstream and downstream developers.  Unfortunately, at the moment, it is often a pretty low fidelity channel!  *But, this post is not about that*.  Rather, it is about figuring out how to make the most of semantic versioning as it is.

### Economics

A fundamental aspect of semantic versioning is *trust*.  Downstream developers must trust upstream developers to not break the protocol (e.g. by putting out minor releases with breaking changes).  When trust is lost, clients quickly become hesitant to upgrade and the _lag_ between a new release and them upgrading increases.  This is a completely rational response as clients must balance the costs of upgrading against their benefits.  For example, if upgrading requires only a few minor tweaks to your code base, but offers important security patches then it seems worth it.  But, if upgrading requires significant changes to your code (e.g. because library developer decided randomly to refactor the API) and the only benefit is some features you don't need, then it doesn't.

_We can view all this through the lens of economic theory and treat it as a market system_.  Then, trustworthy upstream developers should succeed where others fail, etc.  In some sense, it seems like that's it all sorted out!  But, the reality is different as, unfortunately, mistakes are made all the time by developers we think should be trustworthy (see examples below).  The problem is that, as it stands, the system is not efficient.  That is: (1) downstream developers have real difficulties determining what the costs and benefits are; (2) upstream developers cannot easily tell if they've inadvertently made breaking changes (more on this below).  *In thinking about this, we're interested in what techniques could be brought to bear on this information-poor environment.*

### Breaking Changes

An important question here is: _what are "breaking changes" Anyway?_  Knowing this is somehow key to the entire system working smoothly. Some thoughts:

  * _Should a change of license be considered a breaking change?_
  
  * _Should a bug-fix which changes an implementation's behaviour be considered a breaking change?_
  
  * _Should a change which degrades system performance be considered breaking?_
  
  * _Should a change to the contract of a method be considered breaking?_
 
  * _Should a change in the _purity_ of a method be considered breaking?_

  * _Should a change in the order of elements returned by a method be considered breaking?_

Most of these could be considered breaking changes in certain
situations (i.e. depending on the client's needs). The point is that
library developers do not often consider such changes when choosing
version numbers.  _But, with appropriate tooling, maybe they could._

**TODO:** Talk about examples where a bug-fix caused problems because
clients assumed stuff.

  * Talk about bug-fix which caused problems because clients made
    assumptions. What if a method documentation makes no claims about
    the ordering (i.e. because it has no documentation)?_

### Tooling

The [RevAPI tool](https://revapi.org/) provides an interesting example here.  If you haven't come across it before, this tool compares two versions of a Jar file and identifies breaking changes.  For example, reducing the visibility of a method would be considered a breaking change.  Likewise, removing a public declaration or modifying a public class so that it no longer implements some interface.  _This is actually awesome!_  What the tool considers as breaking changes is not the issue.  No, it's what it _doesn't_ consider as breaking which is more interesting to us.  For example, when a method no longer accepts `null` for some parameter, or moves from [linear to quadractic time](https://en.wikipedia.org/wiki/Time_complexity), or returns the elements of an array in a different order, etc.

Of course, we have to be reasonable.  One tool cannot do everything, and there are some _fundamentally hard_ problems here.  On the contrary, RevAPI gives us a glimmer of hope that tooling can be useful in the fight for market efficient semantic versioning.  There should be tools, and lots of 'em!  Both upstream _and_ downstream developers should be using them to spot inadvertent breaking changes, or gauge the cost of upgrades.  But, wait! (we hear you cry) ... there already are tools!  Yes, indeed. [Elm Bump](https://medium.com/@Max_Goldstein/how-to-publish-an-elm-package-3053b771e545), [rust-semverver](https://github.com/rust-dev-tools/rust-semverver), and [clirr](http://clirr.sourceforge.net/) to name a few.  But, they are fairly shallow in their assessment of breaking changes.  We can do better than this.  There is a wealth of techniques from fields like [static analysis](https://en.wikipedia.org/wiki/Static_program_analysis) and automated testing which could easily be repurposed. 

_This is a call to arms!_

### Crazy Ideas

To get you thinking, here are some crazy ideas put forward already in various academic papers:

* Approaches based on testing existing clients.

* Scan my code base to see whether anything could be affected. Slicing or tree shaking.

* Other static analysis.

### Related Articles

   * [Why Semantic Versioning Isn't](https://gist.github.com/jashkenas/cbd2b088e20279ae2c8e)

   * [Semantic Versioning Sucks! Long Live Semantic Versioning](https://developer.okta.com/blog/2019/12/16/semantic-versioning)
