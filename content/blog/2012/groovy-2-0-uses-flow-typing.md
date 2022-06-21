---
date: 2012-07-01
title: "Groovy 2.0 uses Flow Typing!"
draft: false
---

Groovy 2.0 has just been released, and it contains something rather interesting ... optional flow typing!  For those who don't know much about the language, [Groovy](http://en.wikipedia.org/wiki/Groovy_(programming_language)) is a JVM-based dynamically typed language which is similar to Java, but more compact.  And, being dynamically typed means that there's no need for any cumbersome type declarations.

Anyhow, I just came across a discussion of [what's in Groovy 2.0 over on InfoQ](http://www.infoq.com/articles/new-groovy-20). The main improvement is the introduction of (optional) static type checking.  A special annotation `@TypeChecked` can be placed on a class or on a single method.  This annotation indicates that the given code should be statically type checked.  More important, from my perspective, is that *flow typing* is used by the static type checker.  Here's the example from the InfoQ article:

```groovy
import groovy.transform.TypeChecked

@TypeChecked test() {
    def var = 123             // inferred type is int
    var = "123"               // assign var with a String

    println var.toInteger()   // no problem, no need to cast

    var = 123
    println var.toUpperCase() // error, var is int!
```

The flow typing algorithm tracks the flow and understands that variable `var` initially had type `int`, that this was then updated to type `String` (before the first `println`) before finally was updated to `int` again (before the second `println`).

The reason for including flow typing is highlighted by the following quote from [this early discussion dated 2011 on the idea:](http://blackdragsview.blogspot.co.nz/2011/10/flow-sensitive-typing.html)

> Coming from a dynamic language and going static often feels quite limiting. For me the main point of a static type system is to ensure the code I just wrote is not totally stupid. 

Anyhow, I won't go into any more details since the InfoQ article does a good job and [there are some slides here as well](http://www.jroller.com/melix/entry/static_type_checking_talk_from).  But, needless to say, I'm pretty excited to see this feature being used in a mainstream language...
