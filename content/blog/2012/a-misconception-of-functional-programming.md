---
date: 2012-09-06
title: "A Misconception of Functional Programming?"
draft: false
---

Recently, I came across an article entitled "[Useful Pure Functional Programming](http://me-hunz.blogspot.co.uk/2012/09/useful-pure-functional-programming.html)" which talks about the advantages of [functional programming](http://en.wikipedia.org/wiki/Functional_programming).  However, something struck me about the way the author thinks about functional programming:
> "Living for a long time in the context of an imperative world made me get used to think in a specific sequential way ... On the other hand, in the pure functional world, I'm forced to think in a way to transform data."

The author is arguing that thinking about execution in a sequential notion is somehow inherently connected with imperative languages.  The first "imperative" example given in the article is a simple loop in Java:

```java
int sum(int[] list) {
  int result = 0;
  for (int i : list)
    result += i;
  return result;
}
```

The thing is, for me, this example *could equally be written in a pure functional language*.  Sure, it doesn't look like [Haskell](http://www.haskell.org/) code --- but then Haskell isn't the only pure functional language. For example, in Whiley, it would look like this:

```whiley
int sum([int] list):
    result = 0
    for i in list:
        result = result + i
    return result
```

This is a [pure function](http://en.wikipedia.org/wiki/Pure_function) in the strongest sense of the word (i.e. it always returns the same result given the same arguments, does not have [side effects](http://en.wikipedia.org/wiki/Side_effect_(computer_science)) and, hence, is [referentially transparent](http://en.wikipedia.org/wiki/Referential_transparency_(computer_science))).  This function is pure because, in Whiley, compound data structures (e.g. lists, sets, maps, etc) have *value semantics* and behave like primitive data (e.g. `int`) rather than as references to data (like in Java).

## Functional Style
Now, I think the author of the original article got confused about the difference between functional *languages* and functional *style* (i.e. the use of functions as the primary mechanism for expressing and composing computation).  Sure, the functional style favours (amongst other things) recursion over looping.  But, that doesn't prevent functional languages from including looping constructs.

The key is that many [imperative languages support the functional style](http://en.wikipedia.org/wiki/Functional_programming#Functional_programming_in_non-functional_languages).  *In other words, it's not something exclusive to functional programming languages* (although they perhaps support it better).  We need to try and distinguish these two things better, in my opinion, to avoid too much confusion around the difference between functional and imperative languages.

The thing is, *some people don't like the functional style*.  For example, I always prefer to use loops when I can (such as for the `sum()` example above) because they are clear and easy to understand.  But, for some things (e.g. traversing a linked list), I like recursion better.  *That's my style*.  The problem is that people with an imperative streak, like me, often believe they have to completely change their style to use a functional programming language.  *But, that shouldn't need to be the case*.  It's just a shame mainstream functional languages make imperative-style programming so difficult.  If it was easier, then people could migrate slowly without having to rewire their brain from the outset ...

Thoughts?
