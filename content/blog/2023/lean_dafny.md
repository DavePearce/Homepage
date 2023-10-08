---
date: 2023-10-01
title: "On the Two Schools of Verification"
draft: true
#metaimg: "images/2021/DeadlockDetection_Preview.png"
#metatxt: "An algorithm of mine is being used in the Abseil C++ library for dynamic deadlock detection.  So, I thought I would give an overview of how it works."
#twitterimgalt: "Illusstrating a partial ordering of mutexes"
#twittersite: "@whileydave"
#twitter: "https://twitter.com/whileydave/status/1352366798448910336"
#reddit: "https://www.reddit.com/r/cpp/comments/l6hqfi/understanding_deadlock_detection_in_abseil/"
---

Programming languages which support verification go beyond the norm in their ability to eliminate mistakes in code.  Such languages have yet to achieve widespread adoption, but we are starting to see something of an uptick.  Amazon, for example, has a world-class formal methods group which, amongst many things, recently formalized key proprties of FreeRTOS.

The history of languages supporting verification is long and winding (and I'm not going to dive into it all here).  Curiously, such systems appear to fall into one of two camps:

   * **(Hoare Logic)** The first school consists of those systems
       based around employ Hoare Logic.  Good examples include
       [Dafny](https://en.wikipedia.org/wiki/Dafny),
       [Why3](http://why3.lri.fr/) and
       [Whiley](https://en.wikipedia.org/wiki/Whiley_(programming_language)).
       Such systems have the feel of an imperative language and, for
       example, they place a lot of emphasis on loops and loop
       invariants and (typically) exploit [automated theorem
       proving](https://en.wikipedia.org/wiki/Automated_theorem_proving)
       (e.g. [Z3](https://research.nccgroup.com/2021/01/29/software-verification-and-analysis-using-z3/)).

   * **(Dependent Types)** The second school consistens of those based
       on type systems, such as [Martin-Löf type
       theory](https://en.wikipedia.org/wiki/Intuitionistic_type_theory)
       or the [Calculus of
       Constructions](https://en.wikipedia.org/wiki/Calculus_of_constructions).
       Good examples include
       [Agda](https://en.wikipedia.org/wiki/Agda_(programming_language)),
       [Idris](https://en.wikipedia.org/wiki/Idris_(programming_language))
       and
       [Lean](https://en.wikipedia.org/wiki/Lean_(proof_assistant)).
       These systems tend to have a more functional feel, often having
       taken inspiration from Haskell.
   
Perhaps surprisingly, these two camps don't talk much with each other,
and their writings often fail to acknowledge the other "faction"
exists.  So, I thought it might be interesting to dig into these two
approaches a little.

## Hoare Logic

## Dependent Types

## Conclusion
