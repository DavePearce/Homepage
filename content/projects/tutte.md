---
draft: false
date: 2006-01-01
tags: ["tutte"]
title: "Tutte Polynomials"
description: "Tutte polynomials play an import role in graph theory, combinatorics, matroid theory, knot theory, and experimental physics.  This project involved developing a highly efficient algorithm (implemented in C++) for computing Tutte polynomials which was later incorporated into Mathematica and Sage."
icon: "../icons/tutte.png"
banner: "images/graphs.jpg"
---

**Overview.** [Tutte polynomials](http://en.wikipedia.org/wiki/Tutte_polynomial) play an important role in graph theory, combinatorics, matroid theory, knot theory, and experimental physics. For example, the polynomials can be evaluated to find the number of [spanning trees](http://en.wikipedia.org/wiki/Spanning_tree) in a graph, the number of forests in a graph, the number of connected spanning subgraphs, the number of spanning subgraphs, and the number of acyclic orientations. In addition, Tutte polynomials specialise to [chromatic polynomials](http://en.wikipedia.org/wiki/Chromatic_polynomial), flow polynomials, [Jones polynomials](http://en.wikipedia.org/wiki/Jones_polynomial) for alternating links, and partition functions of the [q-state Potts model](http://en.wikipedia.org/wiki/Potts_model) from statistical physics.

While Tutte polynomials have many applications, there are few practical algorithms available for computing them for graphs of sufficient size. Prof. Gary Haggard has paved the way by developing the most efficient algorithm currently available for this, based on his earlier work on computing Chromatic Polynomials. The algorithm relies on various optimisations and heuristics to obtain good performance.

In these pages, you can find an implementation of Gary's algorithm in C++ developed by myself and Gary. We hope that this code may be useful to the algorithms community and that it will eventually lead to an efficient method for computing the Tutte polynomials of large graphs.

**History.** The code released was originally based on that by Prof. Gary Haggard from Bucknell. He had developed a version in C over several years which was very efficient.  Gary was visiting me at Victoria University of Wellington in 2007, and we decided to reimplemented his code in C++. This proved beneficial, since the new version is more modular and extensible. New features added included a proper cache replacement system + heuristics, different edge-selection heuristics and the ability to break up an intermediate graph when it is no longer biconnected.  Since then, [Prof Gordon Royle](https://github.com/DavePearce/DynamicTopologicalSort) provided lots of help with the design and development of the tool; he has also been using it to investigate flow polynomials, and recently discovered a counter-example to a 25-year old conjecture by Dominic Welsh.  More recently, in 2009, Gary an myself computed the Tutte polynomial of the [Truncated Icosahedron](http://en.wikipedia.org/wiki/Truncated_icosahedron), which represents something of a landmark for us. This took about one week to compute on a grid of 150 machines and, for those particularly interested in such things, the polynomial is [here](../../files/ti_poly.txt) and some more information can be found [here](../../publications/TI10.pdf).
