---
date: 2008-01-01
type: "conference"
title: "Java Bytecode Verification for @NonNull Types"
authors: "Chris Male, David J. Pearce, Alex Potanin and Constantine Dymnikov"
booktitle: "Conference on Compiler Construction (CC)"
pages: "229--244"
copyright: "ACM Press"
DOI: "10.1007/978-3-540-78791-4_16"
preprint: "MPPD08_CC_preprint.pdf"
website: "http://www.sable.mcgill.ca/~hendren/CC2008/"
---

**Abstract:** Java’s annotation mechanism allows us to extend its type system with non-null types. However, checking such types cannot be done using the existing bytecode verification algorithm. We extend this algorithm to verify non-null types using a novel technique that identifies aliasing relationships between local variables and stack locations in the JVM. We formalise this for a subset of Java Bytecode and report on experiences using our implementation.
