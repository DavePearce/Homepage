---
date: 2003-01-01
kind: "workshop"
title: "Online Cycle Detection and Difference Propagation for Pointer Analysis"
authors: "David J. Pearce, Paul H.J. Kelly and Chris Hankin"
booktitle: "Workshop on Source Code Analysis and Manipulation (SCAM)"
pages: "3--12"
copyright: "IEEE"
DOI: "10.1109/SCAM.2003.1238026"
preprint: "PKH03_SCAM_preprint.pdf"
---

**Abstract.**  This paper presents and evaluates a number of techniques to improve the execution time of interprocedural pointer analysis in the context of large C programs. The analysis is formulated as a graph of set constraints and solved using a worklist algorithm. Indirections lead to new constraints being added during this process.
In this work, we present a new algorithm for online cycle detection, and a difference propagation technique which records changes in a variable’s solution. Effectiveness of these and other methods are evaluated experimentally using nine common ‘C’ programs ranging between 1000 to 55000 lines of code.
