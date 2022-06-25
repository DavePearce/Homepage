---
date: 2011-07-06
title: "Regular Tree Automata"
draft: false
---

In my quest to understand the theory of recursive types in more detail, the notion of regular tree languages and [Tree Automata](http://wikipedia.org/wiki/Tree_automaton) has cropped up.  These have been used, amongst other things, for describing [XML schemas](http://wikipedia.org/wiki/XML_schema).  They are also useful for describing recursive types as well!

A nice example of a regular tree language is one for minimising *boolean expressions*.  The  constructors of the language are `True`,  `False`, `Not/1`, `And/2` and `Or/2`.  The regular tree language looks thus:

```
Expr -> True
     | False
     | Not(Expr)
     | And(Expr,Expr)
```

This should look pretty familiar to anyone who's studied context-free and/or regular languages before.  A simple (deterministic) bottom-up tree automata can then be defined using the following state transitions:

```
True --> q1            Not(q0) --> q1
False --> q0           Not(q1) --> q0
And(q0,q0) --> q0      And(q1,q0) --> q0
And(q0,q1) --> q0      And(q1,q1) --> q1
```

I guess the interesting question is how this differs from general regular languages.  They appear to be very similar --- for example, they support minimisation, intersection and union (amongst other things).  Likewise, the relationship with context-free grammar is interesting ... although I haven't got to the bottom of that yet.

Anyway, a good reference on this subject is the book [Tree Automata Techniques and Applications](http://tata.gforge.inria.fr/).