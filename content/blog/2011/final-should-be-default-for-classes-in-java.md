---
date: 2011-12-06
title: "Final should be Default for Classes in Java"
draft: false
---

We were having an interesting discussion the other day, and the issue of `final` classes came up.  For some reason, it suddenly occurred to me *that all classes should be final by default*. That is, classes should be *implicitly* final, rather than requiring an *explicit* declaration.  For example, the following should be considered invalid in my opinion:

```java
class Parent { ... }
class Child extends Parent {... } // invalid: parent implicitly final
```

In place of `final` we would have another modifier, say `open`.  This would allow us to extend classes like so:

```java
open class Parent { ... }
class Child extends Parent { ... } // valid: parent explicitly open
```

Now, the question is: *why do I think final should be the default?* It's got nothing to do with performance.  The following quotes from Josh Bloch's excellent talk on API design (see [[1](http://www.youtube.com/watch?v=aAb7hSCtvGw)][[2](http://www.infoq.com/presentations/effective-api-design)][[3](http://www.javaworld.com/javaworld/jw-01-2002/jw-0104-bloch.html)]) gives us a clue:

> "When in doubt, leave it out"
> 
> "You can always add, but you can never remove"

*What does this mean?* If you're not sure whether a function should be included, then don't include it.  That's because, once you've included a function in a public API, people will depend upon it and you'll have to maintain it.  If it's badly designed, you're stuck with it.  Sure, you can try and deprecate it --- but you'll probably end up keeping it forever anyway.

*What has all this got to do with final classes?* Well, a non-final class can be extended of course!  Any `public` or `protected` methods can be overridden and `protected` fields read/written.   More importantly, you cannot reverse the decision --- i.e. once a `public` non-final class, always a `public` non-final class.  In contrast, when using `final` as the default for classes, you can reverse your decision --- i.e. you can always open, but you can never close.

*This observation is hardly rocket science!*  Josh Bloch in his (also excellent) book *Effective Java* states* "you should make each class or member as inaccessible as possible"*.  For example, you should always prefer `private` to `protected` fields. For some reason though, he doesn't extend this to include preferring `final` to non-`final` classes.

And, it looks like there are at least [some like-minded people out there](http://cafe.elharo.com/blogroll/final-good/) and, of course, [there are those who think differently](http://www.ibm.com/developerworks/java/library/j-jtp1029/index.html) ...