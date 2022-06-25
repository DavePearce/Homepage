---
date: 2011-10-26
title: "Fall-Through by Default for Switch Statements?"
draft: false
---

The [switch statement](http://en.wikipedia.org/wiki/Switch_statement) has a long history, and most languages support it or something similar.  In my experience, I found it to be very useful --- both for conciseness, and also improving performance.  With the recent release of Java 7, you can finally [switch over strings](http://code.joejag.com/2009/new-language-features-in-java-7/).

In Whiley, the syntax for switch statements currently looks like this:

```whiley
for token in modifiers:
    modifier = null
    switch token.id:
       case "PUBLIC":
       case "public":
          modifier = ACC_PUBLIC
          break
       case "FINAL":
       case "final":
          modifier = ACC_FINAL
          break
       ...
       default:
          throw SyntaxError("invalid modifier",token)
```

(This excerpt is based on my [Java Assembler/Disassembler](https://github.com/Whiley/wybench) benchmark)

Now, I'm having something of a dilemma over this syntax: *should I support fall-through case statements or not?*

As you can see from above, my initial reaction was: *yes*.  But, I'm starting to think this is a really bad idea and there are a few reasons:
   * Obviously, fall-through by default is dangerous as its easy to forget the `break`.  I've already done this a few times!

   * Generally, I think fall-through is the exception, not the rule.  That is,  most cases do require the `break` and, hence, fall-through by default causes additional verbosity.

   * The overloaded `break` statement can be annoying when you actually want to break out of a loop.


Now, of course, I'm not the first to think along these lines (see e.g. [[1]](http://c2.com/cgi/wiki?IsBreakStatementArchaic)[[2]](http://stackoverflow.com/questions/188461/switch-statement-fallthrough-should-it-be-allowed)[[3]](http://javascript.crockford.com/code.html)) and, in fact, I'm probably being a bit slow on the uptake here!

Several languages (e.g. [Go](http://en.wikipedia.org/wiki/Go_%28programming_language%29), [CoffeeScript](http://jashkenas.github.com/coffee-script/#switch), [Pascal](http://en.wikipedia.org/wiki/Switch_statement#Pascal)) do not support fall-through by default. For example, Go supports an explicit `fallthrough` statement, which might look something like this in Whiley:

```whiley
for token in modifiers:
    modifier = null
    switch token.id:
       case "PUBLIC":
          fallthrough
       case "public":
          modifier = ACC_PUBLIC
       case "FINAL":
          fallthrough
       case "final":
          modifier = ACC_FINAL
       ...
       default:
          throw SyntaxError("invalid modifier",token)
```

Go is quite interesting as it provides [multi-match case statements](http://golangtutorials.blogspot.com/2011/06/control-structures-go-switch-case.html) as well, which might look like something like this in Whiley:

```whiley
for token in modifiers:
    modifier = null
    switch token.id:
       case "PUBLIC","public":
          modifier = ACC_PUBLIC
       case "FINAL","final":
          modifier = ACC_FINAL
       ...
       default:
          throw SyntaxError("invalid modifier",token)
```

I really like this syntax (which I think is [originally from Pascal)](http://en.wikipedia.org/wiki/Switch_statement#Pascal).  In Pascal, you can provide ranges as well which certainly makes sense.

So, all things considered, I'm convinced it is a mistake to support fall-through by default in Whiley.  Fortunately, it's not too late for me to correct this!

The **big question** then is: *do I support explicit fallthrough, multi-match cases or both?*