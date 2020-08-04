---
draft: false
date: 2010-01-01
type: "journal"
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

**History.** The code released was originally based on that by Prof. Gary Haggard from Bucknell. He had developed a version in C over several years which was very efficient.  Gary was visiting me at Victoria University of Wellington in 2007, and we decided to reimplemented his code in C++. This proved beneficial, since the new version is more modular and extensible. New features added included a proper cache replacement system + heuristics, different edge-selection heuristics and the ability to break up an intermediate graph when it is no longer biconnected.  Since then, [Prof Gordon Royle](https://github.com/DavePearce/DynamicTopologicalSort) provided lots of help with the design and development of the tool; he has also been using it to investigate flow polynomials, and recently discovered a counter-example to a 25-year old conjecture by Dominic Welsh.  More recently, in 2009, Gary an myself computed the Tutte polynomial of the [Truncated Icosahedron](http://en.wikipedia.org/wiki/Truncated_icosahedron), which represents something of a landmark for us. This took about one week to compute on a grid of 150 machines and, for those particularly interested in such things, the polynomial is [here](../../files/ti_poly.txt) and some more information can be found [here](../../publications/TI10.pdf).