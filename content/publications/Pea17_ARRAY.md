---
date: 2017-01-01
kind: "workshop"
tag: "whiley"
title: "Array Programming in Whiley"
authors: "David J. Pearce"
booktitle: "Workshop on Libraries, Languages and Compilers for Array Programming (ARRAY)"
pages: "17--24"
copyright: "ACM Press"
DOI: "10.1145/3091966.3091972"
preprint: "Pea17_ARRAY_preprint.pdf"
slides: "Pea17_ARRAY_slides.pdf"
website: "http://pldi17.sigplan.org/track/array-2017"
---

**Abstract:** Arrays are a fundamental mechanism for developing and reasoning about programs. Using them, one can easily encode a range of important algorithms from various domains, such as for sorting, graph traversal, heap manipulation and more. However, the encoding of such problems in traditional languages is relatively opaque. That is, such programming languages do not allow those properties important for the given problem to be encoded within the language itself and, instead, rely up on programmer-supplied comments.
This paper explores how array-based programming is enhanced by programming languages which support specifications and invariants over arrays. Examples of such systems include Dafny, Why3, Whiley, Spec# and more. For this paper, we choose Whiley as this language provides good support for array-based programming. Whiley is a programming language designed for verification and employs a verifying compiler to ensure that programs meet their specifications. A number of features make Whiley particularly suitable for array-based programming, such as type invariants and abstract properties. We explore this through a series of worked examples.