---
date: 2011-09-03
title: "Namespaces in Whiley"
draft: false
---

With the upcoming v0.3.10 release of Whiley, the way `import` statements are interpreted has changed in a fairly significant manner.  The primary purpose of this is to give better support for [namespaces](http://wikipedia.org/wiki/Namespace_(computer_science)). The following illustrates what's changed:

```whiley
import whiley.lang.Math

bool check(int x, int y):
    return max(x,y) == x
```

Previously, the above code would compile with function `max()` being imported from `whiley.lang.Math`.  In other words, an `import` statement automatically imports all names from a given module.  However, this gives relatively little control over namespaces and quickly leads to [namespace pollution](http://bytebaker.com/2008/07/30/python-namespaces/).

Therefore, in the upcoming release of Whiley, the semantics of `import` statements has been brought more in line with [Python](http://wikipedia.org/wiki/Python_(programming_language)).  Thus, the above would not compile as is.  Instead, we would need to write:

```whiley
import whiley.lang.Math

bool check(int x, int y):
    return Math.max(x,y) == x
```

This all makes sense, and I'm absolutely happy with the choice to do this.  However, as usual, there are some hidden issues I didn't foresee.
## Root Concepts
The first issue with the above change came out from actually writing code using it!  In particular, I was working on my bytecode disassembler benchmark and constructed a module `ClassFile` as follows:

```whiley
define ClassFile as ...
define Reader as ...
define Writer as ...
```

This gives rise to the types `ClassFile.Reader` and `ClassFile.Writer`, both of which make sense.  But, it also gives rise to the type `ClassFile.ClassFile`, which frankly is rather cumbersome.  That's because a `ClassFile` is both a key concept in my design and, coincidently, a namespace as well.  Of course, I could prossibly rename `ClassFile` to be module `Class` as follows:

```whiley
define File as ...
define Reader as ...
define Writer as ...
```
This gives rise to the types `Class.Reader`, `Class.Writer` and `Class.File`.  This is better, but I suspect such renaming won't always fit well with the top-level design of a program.  I imagine Python must suffer from this problem as well, so I'll have to look into it more ...

## Processes and Messages
Another problem with the above change to `import` statements, is how it affects process messages.  The following illustrates:

```whiley
import whiley.io.File

[byte] ::readFile(String filename):
    fr = File.Reader(filename)
    return fr.read()
```

This all looks fairly sensible, right?  Well, currently, it doesn't compile.  The reason becomes apparent if we look inside the `File` module:

```whiley
package whiley.io

define Reader as process { ... }

Reader ::Reader(String filename):
    return spawn { ... }

[byte] Reader::read():
     ...
```

What we see is that `read()` is a message declared on process type `File.Reader`.  In Java terms, `read()` is a `static` method which accepts an argument of type `File.Reader`.  And, therein lies the problem.  To get our `readFile()` example to compile we need to write this:

```whiley
import whiley.io.File

[byte] ::readFile(String filename):
    fr = File.Reader(filename)
    return fr.(File.read)()
```

Or, alternatively, we could write it as this:

```whiley
import whiley.io.File
import read from whiley.io.File

[byte] ::readFile(String filename):
    fr = File.Reader(filename)
    return fr.read()
```

I find this somewhat annoying.  However, it's not clear how much of a problem it really is.  That's because, in practice, we'd probably want to define `File.Reader` as an `interface` like so:

```whiley
package whiley.io

define Reader as interface { [byte] read() }

Reader ::Reader(String filename):
    proc = spawn { ... }
    return { this: proc, read: &read }

[byte] Reader::read():
     ...
```

An `interface` is a special kind of record with an explicit field `this`.  Then, when we access the field `read`, `this` is automatically used as the receiver.  With `Reader` implemented as above, our original incantation of `readFile()` would actually compile.  That's because, in this case, `fr.read()` corresponds to an *indirect message send*, where as before it was a *direct message send*.

On the whole, I'm not sure what I'm complaining about!  Implementing `Reader` as an `interface` versus a `process` is much of a muchness.  There is a minor issue of performance as, for a process you get a static method invocation.  But, I'm probably just splitting hairs ...