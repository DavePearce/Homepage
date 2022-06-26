---
date: 2010-12-14
title: "Modular Purity Analysis for Java"
draft: false
---

Well, after an agonizing wait I finally heard the news that my paper on purity analysis was accepted to the [Conference on Compiler Construction, 2011](http://www.complang.tuwien.ac.at/cc2011/).  Obviously, I'm stoked!  The paper is:
   * **JPure: a Modular Purity System for Java**.  David J. Pearce.  In *Proceedings of the Conference on Compiler Construction*, 2011.  [[PDF](http://homepages.ecs.vuw.ac.nz/%7Edjp/files/CC11a.pdf)]


A [pure function](http://wikipedia.org/wiki/Pure_function) is one which has no observable [side-effects](http://wikipedia.org/wiki/Side_effect_(computer_science)).  For example, it cannot modify any state that existed prior to the function being called.  Similarly, it may not read or write from I/O.  There are lots of advantages from knowing which functions are pure.  For example, it allows the compiler (or bytecode optimiser) to perform certain optimisations that would otherwise be considered unsafe (see e.g. [this](http://citeseerx.ist.psu.edu/viewdoc/download;jsessionid=85DD615CAB9598700B76D69696BF4FCB?doi=10.1.1.24.1447&rep=rep1&type=pdf), [this](http://citeseerx.ist.psu.edu/viewdoc/download?doi=10.1.1.90.2478&rep=rep1&type=pdf) and [this](ftp://130.88.199.10/pub/apt/papers/ZhaoRogersKirkhamWatson_ICOOOLPS2008_purity.pdf)).  Purity information can also help with *automatic parallelisation* (e.g. [this](http://citeseerx.ist.psu.edu/viewdoc/download?doi=10.1.1.96.6514&rep=rep1&type=pdf) and [this](http://portal.acm.org/citation.cfm?id=1806596.1806638)), and *software verification* (e.g. [this](http://citeseerx.ist.psu.edu/viewdoc/download?doi=10.1.1.4.9622&rep=rep1&type=pdf) and [this](http://citeseerx.ist.psu.edu/viewdoc/download?doi=10.1.1.61.1997&rep=rep1&type=pdf)).

Right, so pure functions are useful ... but, how do they work?  A simple way of including purity in a language like Java is through annotations.  Here, we annotate methods with `@Pure` to signal they are pure functions.  Then, we want a type checker for the annotations, to ensure we use them correctly.  Typically this would operate by examining the body of every method marked `@Pure`, and checking:
   * For every invocation, the static type of the method being invoked is marked `@Pure`.

   * The method makes no field assignments.

   * If the method overrides a method annotated `@Pure`, then it is also annotated `@Pure`.


These rules ensure that the method has no side-effects, provided the `@Pure` annotations on other methods are themselves correct.  However, these rules are extremely restrictive.  Consider a very simple Java method:

```java
class Example {
 private ArrayList<String> items = ...;

 boolean contains(String item) {
    for(String s : items) {
       if(s.equals(item)) { return true; }
    }
   return false;
}}
```

This method cannot be annotated `@Pure`, even though it clearly is a pure function.  Why?  Well, because it uses an `Iterator` object to traverse `items`, and `Iterator.next()` cannot be annotated `@Pure` (since implementations of `Iterator` must be able update their internal state here).

However, whilst the `contains()` method above is not considered pure under rules (1-3), it does not break our original requirements for purity (given at the top of this article).  That's because `contains()` doesn't modify any state that existed before the method was called --- only state *created* during its execution.  All we need to do is extend our rules for checking purity to allow for this ...

... which is exactly what my paper is all about.  But, I won't spoil it for you here ...  it's all in [the paper](/publications/pea11_cc/)!!
