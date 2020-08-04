---
draft: false
date: 2007-01-01
type: "journal"
title: "Efficient Field-Sensitive Pointer Analysis for C"
authors: "David J. Pearce, Paul H.J. Kelly and Chris Hankin"
booktitle: "ACM Transactions on Programming Languages and Systems (TOPLAS)"
volume: "30"
number: "1"
year: "2007"
copyright: "ACM Press"
DOI: "10.1145/1290520.1290524"
preprint: "PKH07_TOPLAS_preprint.pdf"
---
**Abstract.** The subject of this paper is flow- and context-insensitive pointer analysis. We present a novel approach for precisely modelling struct variables and indirect function calls. Our method em- phasises efficiency and simplicity and is based on a simple language of set constraints. We obtain an `O(v^4)` bound on the time needed to solve a set of constraints from this language, where v is the number of constraint variables. This gives, for the first time, some insight into the hardness of performing field-sensitive pointer analysis of C. Furthermore, we experimentally evaluate the time versus precision trade-off for our method by comparing against the field-insensitive equivalent. Our benchmark suite consists of 11 common C programs ranging in size from 15,000 to 200,000 lines of code. Our results indicate the field-sensitive analysis is more expensive to compute, but yields significantly better precision. In addition, our technique has been integrated into the latest release (version 4.1) of the GNU Compiler GCC. Finally, we identify several previously unknown issues with an alternative and less precise approach to modelling struct variables, known as field-based analysis.

**Notes.** This algorithm is currently used in the [GNU C Compiler (GCC)](https://github.com/gcc-mirror/gcc/blob/master/gcc/tree-ssa-structalias.c) and in the [godoc](https://github.com/golang/tools/blob/master/go/pointer/doc.go) tool for [Go](https://golang.org).  An implementation of the algorithm in C++ is availale [here](static/files/pcs-2.1-060204-01.tgz).  You can also find discussion of the GCC implementation in this paper:

   * Structure Aliasing in GCC, Dan Berlin.  In Proceedings of the GCC Developers Summit, 2005.  [PDF](https://gcc.gnu.org/wiki/HomePage?action=AttachFile&do=get&target=2005-GCC-Summit-Proceedings.pdf)
