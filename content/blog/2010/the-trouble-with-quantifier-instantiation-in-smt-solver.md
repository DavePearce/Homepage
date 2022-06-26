---
date: 2010-06-22
title: "The Trouble with Quantifier Instantiation in an SMT Solver? Triggers."
draft: false
---

So, i've been recently working through the quantifier instantiation mechanism in [wyone](http://whiley.org/wyone).  This is a tricky, yet important topic.  There's been a lot of interesting research in this area as well (see further reading below for some examples), although I found the papers often hard work at times.

Anyway, the basic idea is pretty straightforward.  Let's imagine we're testing the following formula for [satisfiability](http://en.wikipedia.org/wiki/Satisfiability_Modulo_Theories):

```
in(x,xs) && x < 0 && forall Y [ in(Y,xs) ==> Y >= 0 ]
```

In this formula, the `forall` expression enforces a constraint that every element in the "collection" `xs` must be positive.  On the other hand, we have a variable `x` which is known to be in `xs`, and is negative.  Therefore, we can easily see that there is a contradiction here.  *But, how does the SMT solver show this?* The answer is through [quantifier instantiation](http://en.wikipedia.org/wiki/Universal_instantiation).  To do this, it constructs a binding from quantified variables to ground literals (or possibly ground formulas).  Once a suitable binding is found, it instantiates the quantifier.  For our example, a good binding would be `[Y->x]` and using this to instantiate our quantifier gives:

```
in(x,xs) && x < 0 && forall Y [ in(Y,xs) ==> Y >= 0 ]
=> && in(x,xs) ==> x >= 0
=> && x >= 0
```

At this point, the arithmetic theory of the SMT solver will kick in and immediately derive a contradiction from `x<0 && x>=0`.

The difficulty in this process stems from the problem of finding a good binding.  A lot of work in this area has been done, particularly with systems like Prolog which use [unification](http://en.wikipedia.org/wiki/Unification_%28computing%29).  Modern SMT solvers usually rely on so-called *triggers* to generate the binding.  A trigger is essentially just a pattern match which generates bindings.  In our example, you could trigger on `in(X,Y)` literals:  when you saw one in a quantified formula, you'd try and generate a binding by looking at all existing ground instances of it. In our example, that would lead to unifying `in(x,xs)` against `in(Y,xs)` and that would generate the required binding.

This approach to quantification has numerous drawbacks.  First and foremost, it can be very expensive to determine a binding as this requires a potentially exponential search through all possible trigger matches.  Secondly, the problem arises when the SMT solver doesn't use the exact trigger you need to get the contradiction.  This is particularly likely to happen when the formula involves triggering on arithmetic expressions.  For example:

```
f(x-1,x) && !g(x-1,x) && forall X,Y [ f(X,X+Y) ==> g(X,X+Y) ]
```

(Ok, this is a slightly artificial example, but it makes the point)

The challenge here is to obtain the binding `[X->x-1, Y->1]` which will lead us to the contradiction.  Certainly, this is possible ... but the problem of unification is suddenly much harder as we must be prepared to make complex expression rewrites during unification.  At some point, the SMT solver may give up searching for matches, and you'll be forced to write formulas in a way that helps it find them.

Anyway, that's all for now.  But, I'd be quite interested to know how far existing solvers like Z3 or Simplify go in this department ...

## Further Reading

   * **E-matching for Fun and Profit**, [Michał Moskal](http://portal.acm.org/author_page.cfm?id=81367593542&coll=GUIDE&dl=GUIDE&trk=0&CFID=94491488&CFTOKEN=50134837), [Jakub Łopuszańsk](http://portal.acm.org/author_page.cfm?id=81367596192&coll=GUIDE&dl=GUIDE&trk=0&CFID=94491488&CFTOKEN=50134837)i, Joseph R. Kiniry. *Electronic Notes in Theoretical Computer Science (ENTCS)*, 198(2): 19-35, 2008. [[PDF](http://kind.ucd.ie/documents/published/MoskalKiniry07.pdf)]

   * **Efficient E-matching for SMT Solvers**, Leonardo de Moura and Nikolaj Bjorner. In *Proceedings of the 21st international conference on Automated Deduction*, LNCS 4603:183-198, 2007. [[PDF](http://citeseerx.ist.psu.edu/icons/pdf.gif;jsessionid=B43237574994909B06DFAC7293CE8438)]

   * **Programming with triggers**, [Michał Moskal](http://portal.acm.org/author_page.cfm?id=81367593542&coll=GUIDE&dl=GUIDE&trk=0&CFID=92476326&CFTOKEN=78919110).  In *Proceedings of the Workshop on Satisfiability Modulo Theories*, pages 20--29, 2009. [[PDF](http://research.microsoft.com/en-us/um/people/moskal/pdf/prtrig.pdf)]

