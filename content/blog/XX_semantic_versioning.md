---
date: 2020-09-24
title: "The Semantics of Sematic Versioning?"
draft: true
---

Semantic versioning is a surprisingly interesting topic when you get into it.  Recently, myself and a few colleagues have been giving it quite a bit of thought (and even wrote an [essay on it](https://whileydave.com/publications/ldp20/))!  To start with, if you haven't seen it already, check out the [manifesto for semantics versioning](https://semver.org/).  Whilst that provides quite a good overview, there is still quite a lot that's left unsaid.  There are two basic perspectives that semantic version is supposed to help with:

   1) **Library Client**.  This is perhaps the more obvious scenario.  Clients want access to library features!  But, they also want both *stability* and *protection*.  That is, they don't want future release of the library to break their code, but they do want get future releases automatically when they contain critical (e.g. security) updates.
   
   2) **Library Developer**.  On the flip-side, the developer wants *flexibility* to continue improving their API as necessary for adding new features, or addressing past mistakes.  They also want to fix bugs and apply other critical updates as necessary.

Semantic version is simply a mechanism for communication between developer and client.  And, unfortunately, it's a pretty low fidelity channel!  *But, this post is not about that*.  Rather, it is about figuring out how to make the most of semantic versioning as it is.

### What are "Breaking Changes"?

### 
