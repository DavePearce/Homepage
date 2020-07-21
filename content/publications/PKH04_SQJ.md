---
date: 2004-01-01
type: "journal"
title: "Online Cycle Detection and Difference Propagation: Applications to Pointer Analysis"
authors: "David J. Pearce, Paul H.J. Kelly and Chris Hankin"
booktitle: "Software Quality Journal"
volume: "12"
number: "4"
pages: "209--335"
copyright: "Kluwer Academic Publishers"
DOI: "10.1023/B:SQJO.0000039791.93071.a2"
---

**Abstract.** This paper presents and evaluates a number of techniques to improve the execution time of interprocedural pointer analysis in the context of C programs. The analysis is formulated as a graph of set constraintsand solved using a worklist algorithm. Indirections lead to new constraints being added during this procedure.The solution process can be simplified by identifying cycles, and we present a novel online algorithm for doingthis. We also present a difference propagation scheme which avoids redundant work by tracking changes to eachsolution set. The effectiveness of these and other methods are shown in an experimental study over 12 common‘C’ programs ranging between 1000 to 150,000 lines of code.