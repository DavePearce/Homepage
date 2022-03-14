---
date: 2022-03-14
kind: "conference"
title: "On the Termination of Borrow Checking in Featherweight Rust"
authors: "Étienne Payet, David J. Pearce and Fausto Spoto"
booktitle: "Proceedings of the Nasa Formal Methods Symposium (NFM)"
preprint: "PPS22_NFM_preprint.pdf"
---

**Abstract:** A distinguished feature of the Rust programming language
is its ability to deallocate dynamically-allocated data structures as
soon as they go out of scope, without relying on a garbage
collector. At the same time, Rust lets programmers create references,
called _borrows_, to data structures. A static borrow checker enforces
that borrows can only be used in a controlled way, so that automatic
deallocation does not introduce dangling references.  Featherweight
Rust provides a formalisation for a subset of Rust where borrow
checking is encoded using flow typing.  However, we have identified a
source of non-termination within the calculus which arises when typing
environments contain cycles between variables.  In fact, it turns out
that well-typed programs cannot lead to such environments --- but this
was not immediately obvious from the presentation.  This paper defines
a simplification of Featherweight Rust, more amenable to formal
proofs. Then it develops a sufficient condition that forbids cycles
and, hence, guarantees termination.  Furthermore, it proves that this
condition is, in fact, maintained by Featherweight Rust for well-typed
programs.
