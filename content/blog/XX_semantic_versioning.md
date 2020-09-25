---
date: 2020-09-24
title: "The Semantics of Sematic Versioning?"
draft: true
---

Semantic versioning is a surprisingly interesting topic when you get into it.  Recently, myself and a few colleagues have been giving it quite a bit of thought (and even wrote an [essay on it](https://whileydave.com/publications/ldp20/))!  To start with, if you haven't seen it already, check out the [manifesto for semantics versioning](https://semver.org/).  Whilst that provides quite a good overview, there is still quite a lot that's left unsaid.  There are two basic perspectives that semantic version is supposed to help with:

   1) **Downstream**.  This is perhaps the more obvious scenario.  Downstream developers (clients) want access to the library features offered by (upstream) dependencies!  But, they also want both *stability* and *protection*.  That is, they don't want future releases of a library to break their code but (ideally) they want to get future releases automatically when they contain critical (e.g. security) updates.
   
   2) **Upstream**.  On the flip-side, upstream (library) developers want *flexibility* to continue improving their libraries as necessary to add new features, or address past mistakes.  They also want to fix bugs and apply other critical updates as and when they arise.

At its most basic leve, semantic versioning is simply a communication mechanism between upstream and downstream developers.  Unfortunately, at the moment, it is often a pretty low fidelity channel!  *But, this post is not about that*.  Rather, it is about figuring out how to make the most of semantic versioning as it is.

### Economics

A fundamental aspect of semantic versioning is *trust*.  Downstream developers must trust upstream developers to not break the protocol (e.g. by putting out minor releases with breaking changes).  When trust is lost, clients quickly become hesitant to upgrade and the _lag_ between a new release and them upgrading increases.  This is a completely rational response as clients must balance the costs of upgrading against their benefits.  For example, if upgrading requires only a few minor tweaks to your code base, but offers important security patches then it seems worth it.  But, if upgrading requires significant changes to your code (e.g. because library developer decided randomly to refactor the API) and the only benefit is some features you don't need, then it doesn't.

_We can view all this through the lens of economic theory and treat it as a market system_.  Then, trustworthy upstream developers should succeed where others fail, etc.  In some sense, it seems like that's it all sorted out!  But, the reality is different as, unfortunately, mistakes are made all the time by developers we think should be trustworthy (see examples below).  The problem is that, as it stands, the system is not efficient.  That is: (1) downstream developers have real difficulties determining what the costs and benefits are; (2) upstream developers cannot easily tell if they've inadvertently made breaking changes (more on this below).  *In thinking about this, we're interested in what techniques could be brought to bear on this information-poor environment.*

### Breaking Changes

What are "Breaking Changes" Anyway?

Whilst this is always detrimental when an upgrade contains critical security fixes.  But, it

  * licences
  * Contracts
  


### 
