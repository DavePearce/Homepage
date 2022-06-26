---
date: 2010-12-13
title: "Why not use Structural Subtyping?"
draft: false
---

Modern programming languages generally use what is known as a [Nominal type system](http://wikipedia.org/wiki/Nominal_typing).  This means types are associated with explicit names and subtyping relationships are explicit in the code (e.g. via `extends` or `implements`).  This approach has the advantage of being relatively simple to implement;  however, at the same time, it is quite restrictive because the way types are structured is essentially fixed.  

An alternative (and far less common) approach is to use a [Structural type system](http://wikipedia.org/wiki/Structural_type_system).  In this case, types are not associated with explicit names and subtyping relationships are not declared explicitly.  Rather, types are defined by their structure and subtyping is implicit between types of related structure.  Consider this simple example:
```whiley
define Point as { int x, int y }
define Point3D as { int x, int y, int z }
```
Whilst there is no explicit connection between `Point` and `Point3D`, there is an *implicit* one --- namely, that `Point3D` is a subtype of `Point`.  This means we can pass `Point3D` instances into variables of type `Point`, without any need for casting.  Likewise, we can define an implicit super type of `Point`:
```whiley
define RealPoint as { real x, real y }
```
These definitions may be spread across different files, different libraries or different programs --- i.e. there is no connection between them other than their structure.  In fact, we might even have a different name for the same type:
```whiley
define XYpoint as { int x, int y }
```
In a structural type system, there is no difference at all between `Point` and `XYpoint` --- they are the exactly same type.

The advantages of structural subtyping seem fairly evident to me.  For example, suppose you're working with some library and want to pass your types directly into its functions.  In a nominal type system, this can be a real problem unless (by chance) we can inherit or implement types from the library.  In a structural type system, this is easy as we just need to make sure our types have the right fields!  (Of course, encapsulation is an issue here, as we may not know exactly what fields are required).

Here's another take on the [benefits of structural subtyping](http://draconianoverlord.com/2010/01/17/caller-side-structural-typing.html).  So, my question is: *why don't more languages employ structural subtyping?*  Scala is probably the best example of one that (at least, to some degree) does --- see [here](http://debasishg.blogspot.com/2008/06/scala-to-java-smaller-inheritance.html) and [here](http://infoscience.epfl.ch/record/138931/files/2009_structural.pdf).  Also, there is the "M Programming Language" (see [here](http://www.theregister.co.uk/2008/10/10/dial_m_for_microsoft/), [here](http://community.bartdesmet.net/blogs/bart/archive/2009/02/16/the-m-programming-language-part-1-structural-typing.aspx) and [here](http://www.infoq.com/presentations/Codename-M;jsessionid=C9F8F48D897CD71847E19263BB53C234) for more), which has excellent support for structural subtyping.  

Finally, there has been a fair amount of academic research on structural subtyping in the context of languages like Java:

   * "*Integrating Nominal and Structural Subtyping*", Donna Malayeri and Jonathan Aldrich.  In Proceedings of ECOOP, 2008. [[PDF](http://www.cs.cmu.edu/~donna/public/ecoop08.pdf)]

   * "*Whiteoak: Introducing structural typing into Java*", Joseph (Yossi) Gil and Itay Maman.  In Proceedings of OOPSLA, 2008. [[PDF](http://whiteoak.sourceforge.net/Whiteoak-OOPSLA08-0608.pdf)]

