---
date: 2012-04-10
title: "Are Checked Exceptions Always Caused by I/O?"
draft: false
---

Recently, I've had the pleasure of working with Eclipse and trying to build a plugin.  On the whole, I have to confess,* I find that Eclipse is an extremely well-designed and considered piece of software*.  The biggest problem, I suppose, is that it is designed for a wide variety of tasks and this means you spend a lot of time deciphering some rather abstract abstractions!  And, sure, the documentation could be better --- but on the whole it is pretty good.

Anyway, this post is not really about Eclipse plugins.  One of things I had to do, was integrate my own build framework into that of Eclipse.  This immediately caused problems because of the Eclipse [CoreException](http://help.eclipse.org/indigo/index.jsp?topic=%2Forg.eclipse.platform.doc.isv%2Freference%2Fapi%2Forg%2Feclipse%2Fcore%2Fruntime%2FCoreException.html).  This [checked exception](http://en.wikipedia.org/wiki/Exception_handling#Checked_exceptions) is required on a large number of methods.  The problem is that my build framework requires the standard `java.io.IOException` on several methods, but `CoreException` does not extend this.  Since I don't want my build framework to depend on Eclipse, I'm forced into doing something unusual.  For example, tunneling `CoreException`s through my framework as `IOException`s and vice-versa through Eclipse.  One could perhaps argue that `CoreException` should (or should not) implement `IOException` --- *but, that's not what this post is really about*.

*So, what is this post about?* Well, this issue got me thinking: *are checked exceptions always caused by I/O?*

Let's try a simple thought experiment to get us thinking about this.  Imagine a program running on bare metal *which does not perform any I/O whatsoever*.  The question is: *in what ways can this program go wrong (i.e. crash)?* There are some obvious ones:
   * **Segmentation Fault (or similar)**.  For example, it tried to dereference a `null` pointer, divide by zero or access an array out-of-bounds, etc.

   * **Out-of-memory.** The program ran out of memory and e.g. dumped its core.

   * **Infinite Loop.** The program entered into some kind of infinite loop and continued forever.

   * **Deadlock.** The program managed to enter some kind of dead lock (or even a [live lock](http://en.wikipedia.org/wiki/Deadlock#Livelock)) and failed to make progress.


Maybe there are some others, but this mostly covers it (remember, I/O is not permitted so we can't e.g. block indefinitely on a socket).  *I want to argue that none of these things should cause a checked exception.* You might think that's obvious because e.g. in Java none of these things is a checked exception (e.g. `NullPointerException`, `ArithmeticException`, `OutOfMemoryError`, etc, are unchecked).  But, as usual, it is more subtle than it first looks...

*So, how could the above things cause a checked exception?* Well, the programmer will obviously try and protect against them happening.  For any given function, there are two main ways this can play out:
   * In some cases, he/she will simply conclude that: *if the function is written correctly and called with valid arguments, an error could never happen* (e.g. divide-by-zero, out-of-bounds, infinite loop, etc). In these cases, he/she generally won't to do anything specific (except perhaps a little bit of argument checking).

   * In other cases, he/she will conclude that: *this function must accept all inputs, but some of them are incorrect*.  A good example is a `parse(String)` function which parses a string and e.g. converts it into a data structure. In the case of a malformed string, it must report a `SyntaxError`.  The programmer may decide to implement this as a checked exception, so that any callers of the function are forced to acknowledge this possibility.


These two cases may seem similar, but there is an important difference: in one case, our function only accepts "correct" inputs; in the other, it must also accept "incorrect" inputs.  Still, this seems fairly straightforward ...

Consider again our thought experiment in the context of the `parse(String)` function, which `throws SyntaxError` (a checked exception).  Here's the line of reasoning: Q) *Under what circumstances could a `SyntaxError` be thrown?* A) If the input string was invalid;  Q) *How could the input string be invalid?* A) If the programmer constructed an invalid string; Q) *How could the programmer have constructed an invalid string?* A) **If he/she had made a programming error**.  The thing is, since there is no I/O, the programmer has complete control over exactly what strings are constructed.  If an invalid string was constructed, it is because the programmer made a mistake somewhere.  *That's the only possibility*.

If you've made it this far, great!  Hopefully, you're starting to get the idea: **without I/O, there's really no need for checked exceptions**.  The problem, of course, is that we do have I/O.  But, actually, it's worse than that.  I won't complain about having to write `throws IOException` on a function which can `throw` an `IOException`.  The problem is having to write it on a function which definitely *cannot* throw an `IOException`.  For example, I might call `parse()` from a setting where the input string is always syntactically valid.  But, I cannot tell the compiler this --- so I'll probably have to catch the `SyntaxError` and just discard it.  Maybe that's not so bad, but it's definitely one of the reasons checked exceptions have a bad reputation.

Finally, the other reason people get upset with checked exceptions is when they are used incorrectly.  Based on my above analysis, my conclusion is that Eclipse's `CoreException` should extend `IOException`.  In fact, I think probably all checked exceptions should extend `IOException` ...
