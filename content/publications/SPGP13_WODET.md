---
date: 2013-01-01
kind: "workshop"
title: "Balloon Types for Safe Parallelisation over Arbitrary Object Graphs"
authors: "Marco Servetto, David J. Pearce, Lindsay Groves and Alex Potanin"
booktitle: "Workshop on Determinism and Correctness in Parallel Programming (WODET)"
preprint: "SPGP13_WODET_preprint.pdf"
website: "http://wodet.cs.washington.edu/"
---

**Abstract:** Safe parallelisation of object-oriented programs requires static guarantees about the shape and/or intended usage of reachable objects. For example, transitively immutable objects lend themselves naturally to concurrent access. However, parallelising tasks which potentially mutate reachable objects is more challenging. One approach to avoiding race conditions is to ensure the reachable object graphs of any concurrently executing tasks are disjoint. Numerous systems have been developed which provide guarantees of this kind (e.g. ownership types, regions, balloons, etc).
In this paper, we build on the work of Almeida who developed balloons as a mechanism for providing strong encapsulation. Our approach is closely related to the recent work of Gordon et al. who developed a system for safe parallelisation based on isolation (i.e. balloons). Their system can safely parallelise code which mutates reachable objects. However, their system also relies on the use of destructive field reads, which go against the natural object-oriented style. Our system brings together the ideas of balloons, immutability and lent references to enable safe parallelisation over mutable data without requiring destructive field reads.