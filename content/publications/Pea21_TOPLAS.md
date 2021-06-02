---
draft: false
date: 2021-04-01
kind: "journal"
tags: ["rust"]
title: "A Lightweight Formalism for Reference Lifetimes and Borrowing in Rust"
authors: "David J. Pearce"
booktitle: "Transactions on Programming Languages and Systems"
volume: "43"
number: "1"
pages: "Article 3"
copyright: "ACM"
DOI: "10.1145/3443420"
preprint: "Pea21_TOPLAS_postprint.pdf"
---
**Abstract.** Rust is a relatively new programming language which has gained significant traction since its v1.0 release in 2015.  Rust aims to be a systems language that competes with C/C++.  A claimed advantage of Rust is a strong focus on memory safety without garbage collection.  This is primarily achieved through two concepts, namely _reference lifetimes_ and _borrowing_.  Both of these are well known ideas stemming from the literature on _region-based memory management_ and _linearity_ / _uniqueness_.  Rust brings both of these ideas together to form a coherent programming model.  Furthermore, Rust has a strong focus on stack-allocated data and, like C/C++ but unlike Java, permits references to local variables.

Type checking in Rust can be viewed as a two-phase process: firstly, a traditional type checker operates in a flow-insensitive fashion; secondly, a _borrow checker_ enforces an ownership invariant using a flow-sensitive analysis.  In this paper, we present a lightweight formalism which captures these two phases using a flow-sensitive type system that enforces _"type and borrow safety"_.  In particular, programs which are type and borrow safe will not attempt to dereference dangling pointers.  Our calculus core captures many aspects of Rust, including copy- and move-semantics, mutable borrowing, reborrowing, partial moves, and lifetimes.  In particular, it remains sufficiently lightweight to be easily digested and understood and, we argue, still captures the salient aspects of reference lifetimes and borrowing.  Furthermore, extensions to the core can easily add more complex features (e.g. control-flow, tuples, method invocation, etc).  We provide a soundness proof to verify our key claims of the calculus.  We also provide a reference implementation in Java with which we have model checked our calculus using over 500 billion input programs.  We have also fuzz tested the Rust compiler using our calculus against 2 billion programs and, to date, found one confirmed compiler bug and several other possible issues.
