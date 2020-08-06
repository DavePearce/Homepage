---
draft: false
date: 2010-01-01
kind: "journal"
tag: "tutte"
title: "Computing Tutte Polynomials"
authors: "Gary Haggard, David J. Pearce and Gordon Royle"
booktitle: "ACM Transactions on Mathematical Software"
volume: "37"
number: "3"
pages: "article 24"
copyright: "ACM Press"
DOI: "10.1145/1824801.1824802"
preprint: "HPR10_TOMS_preprint.pdf"
---

**Abstract:** The [Tutte polynomial](https://en.wikipedia.org/wiki/Tutte_polynomial) of a graph is a 2-variable polynomial graph invariant of considerable importance in both combinatorics and statistical physics. It contains several other polynomial invariants, such as the chromatic polynomial and flow polynomial as partial evaluations, and various numerical invariants such as the number of spanning trees as complete evaluations. However, despite its ubiquity, there are no widely-available effective computational tools able to compute the Tutte polynomial of a general graph of reasonable size. In this paper we describe the implementation of an algorithm that exploits isomorphisms in the computation tree to extend the range of graphs for which it is feasible to compute their Tutte polynomials, and we demonstrate the utility of the program by finding counterexamples to a conjecture of Welsh on the location of the real flow roots of a graph.

**Notes.** The latest version of my C++ implementation is available [here](../../files/tuttepoly-v0.9.18.tgz) and also on [GitHub](https://github.com/DavePearce/TuttePoly).  A Java implementation is maintained separately [here](https://github.com/klapaukh/JTuttePoly).  Finally, the algorithm has also been incorporated into both [Mathematica](https://mathworld.wolfram.com/TuttePolynomial.html) and [Sage](https://doc.sagemath.org/html/en/reference/graphs/sage/graphs/tutte_polynomial.html).
