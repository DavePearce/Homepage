---
date: 2004-01-01
kind: "workshop"
title: "Efficient Field-Sensitive Pointer Analysis for C"
authors: "David J. Pearce, Paul H.J. Kelly and Chris Hankin"
booktitle: "Workshop on Program Analysis for Software Tools and Engineering (PASTE)"
pages: "37--42"
copyright: "ACM Press"
DOI: "10.1145/996821.996835"
preprint: "PKH04_PASTE_preprint.pdf"
website: "http://www.st.cs.uni-sb.de/paste/"
---

**Abstract:** The subject of this paper is flow- and context-insensitive pointer analysis. We present a novel approach for precisely modelling struct variables and indirect function calls. Our method emphasises efficiency and simplicity and extends the language of set-constraints. We experimentally evaluate the precision cost trade-off using a benchmark suite of 7 common C programs between 5,000 to 150,000 lines of code. Our results indicate the field-sensitive analysis is more expensive to compute, but yields significantly better precision.