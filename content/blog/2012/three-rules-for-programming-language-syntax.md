---
date: 2012-01-11
title: "Three Rules for Programming Language Syntax?"
draft: false
---

I'm always pondering the question: *what makes good programming language syntax?* One thing occuring to me is that many languages often ignore the [HCI](http://wikipedia.org/wiki/Human-computer_interaction) aspect.  For me, it's a given that *the purpose of a programming language is to simplify the programmer's life, not the other way around*.

So, I thought of a few simple rules:
   * **Syntax should explain.** The purpose of syntax is to help explain a program to a human.  In particular, structure in the program should be made explicit through syntax.

   * **Syntax should be concise.** The converse to (1) is that syntax should always add to the explanation.  Syntax which does not add value simply obscures the explanation.

   * **Syntax should conform.** The are many things people learn from everyday life, and we should not force them to unlearn these things.  Doing so can confuse the explanation, especially for non-experts.


I'm sure there are plenty of others you can think of.  I pick on these because I see them being broken constantly.  Let's illustrate with some of my pet peeves:
### Colons in Type Declarations?
There are plenty of programming languages which use colons in type declarations ([ML is a classic example](http://wikipedia.org/wiki/ML_(programming_language))).  It goes something like this:

```whiley
fun f (0 : int) : int = ...
```

For me, this breaks rule (2) because those colons do not add value.  The C-family of languages is evidence that we can happily live without them.  That's not to say that C-like declaration syntax is the "one true way"; only that it demonstrates we don't need those colons!
### Function Call Braces: To Be or Not To Be?
Some programming languages require braces around the arguments to a function call, whilst others (e.g. [Haskell](http://wikipedia.org/wiki/Haskell_(programming_language))) don't.  Consider this:

```whiley

(f (g h) 1 2) y 3

```

What can you tell about the invocation structure from this line? 
 Not much, unless you happen to know from memory exactly what arguments each function takes. This violates rule (3) since functions are normally expressed with braces in mathematics.

Now, how about this:

```whiley

f(g(h),1,2)(y,3)

```

Personally, I prefer this style.  However, it would be fair to say that it violates rule (2) since the comma's are not strictly necessary.  I suppose, in fact, we have a contradiction between (2) and (3) in this case, since commas are generally used to deliniate lists in written English.
### Why Don't we Teach Polish Notation at School?*(answer: because we don't)*

Some programming languages require mathematical expressions be expressed in [Polish notation](http://wikipedia.org/wiki/Polish_notation) (a.k.a. prefix notation).  For example, in [Lisp](http://wikipedia.org/wiki/Lisp_(programming_language)), we write `(+ 1 2)` to mean `1+2`. This violates rule (3), since most people have learned mathematics at school where the usual convention applies.

Similarly in [SmallTalk](http://wikipedia.org/wiki/Smalltalk), the expression `2+3*4` is equivalent to `(2+3)*4` rather than `2+(3*4)`, as might be expected.  Again, this violates rule (3) and is particularly insidious because it's rather subtle.

The point here is not to say that e.g. one notation is *fundamentally better* than the other.  Just that, we learn one in school, so we should stick with that.
## Conclusion
Well, if you made it this far, great!  Hopefully there was some food for thought, and maybe you can suggest some other examples...
