---
date: 2011-06-13
title: "Language Complexity?"
draft: false
---

Some languages are complex, others are simple ... right?  C++ versus just about anything else is a good example here.  But, it begs the question: *what makes a language complex?*

So, I've just been reading [Bruce Eckel's Artima article on Scala](http://www.artima.com/weblogs/viewpost.jsp?thread=328540).  It's actually a nice article, and I enjoyed it.  But, one thing bugged me --- and, it's nicely summarised in this quote:
> But you can see from the code above that learning Scala should be a lot easier than learning Java! There's none of the horrible Java ceremony necessary just to write  "Hello, world!" -- in Scala you can actually create a one-line script ...

Somehow, that's not the impression of Scala I get from the article.  It's almost as if removing `public static void` makes a language easy.  I don't think ceremony has much to do with what makes a language simple or not.  Sure, it reduces the amount of *finger typing*, but that's something quite different.

From my perspective, a language is easy to work with if there is a clear mental model behind it.  This is something one can draw upon to understand why certain constructs exist, and when they should be used.  Take the idea of `traits` as covered by Bruce:
> We can also mix in behavior using *traits*. A trait is much like  an interface, except that traits can contain method definitions, which  can then be combined when creating a class.

This is not helping me to understand what traits really are.  The questions are: *why would I use them?**when does it make sense to use them?**when does it not make sense to use them?* Now, I'm not trying to argue that traits are (or are not) a complex feature.  The point is: *this explanation does not lead me to conclude that they are "simple"*.  That's because it doesn't give any insight into the mental model behind them.

Teaching students how to program is a good way to pick up on what concepts are difficult.  To get them to really understand something, you need to build up a mental model. For example, in explaining references/pointers, the old boxes-and-arrows diagram goes a long way.  In Java, however, there are a number of thorny issues:
   * **Subtyping**.  Understanding when subtyping does or does not apply is a challenge.  Sure, students can memorise the subtype relation for primitives, and you can say "if A extends B then A is a *subtype* of B", etc.  But, this doesn't help construct a mental model of what subtyping is and what it's for.  For primitives, we have a nice rule-of-thumb: *a numeric type `T1` can flow into another `T2` provided there's no loss of precision* (of course, [this isn't always true though](http://stackoverflow.com/questions/1293819/why-does-java-implicitly-without-cast-convert-a-long-to-a-float)).  But, what's the mental model here?  Well, the first question that comes out is: *why do different types (e.g. `short` versus `int`) have different ranges?* Unfortunately, this only really makes sense to students who already understand binary representation of numbers!  For objects, you start with a model of static versus dynamic (i.e. runtime) type which, for the most part, I think is easy enough.

   * **Dynamic Dispatch**.  Given an inheritance hierarchy, understanding which method will be invoked in a given situation is a struggle for many.  This is compounded by the fact that it's very difficult to give precise rules about it (i.e. have you read the [JLS](http://www.cs.columbia.edu/~sedwards/papers/gosling2000java.pdf) on this topic recently?).  To build a mental model you need to talk about method signatures, static and dynamic types, etc.  Up to a point, it's not too bad.  But, then throw in generics + erasure and the mental model really starts to get complicated ...

   * **Interfaces vs Abstract Classes**. This is another classic although, again, at least there's an easy rule-of-thumb: *prefer interfaces and use abstract classes only for code reuse*.  But, what's the mental model behind this?  Well, students typically start out with the question: *why can you only extend one class?* From there, things can quickly get out of hand as you explain the pitfalls of multiple inheritance ...


(As I'm sure you know, there are plenty of other interesting examples in Java that we could talk about here)

Anyway, that's my 2c on language complexity ...