---
date: 2011-01-01
kind: "conference"
tag: "jpure"
title: "JPure: a Modular Purity System for Java"
authors: "David J. Pearce"
booktitle: "Conference on Compiler Construction (CC)"
pages: "104--123"
copyright: "Springer"
DOI: "10.1007/978-3-642-19861-8_7"
preprint: "Pea11_CC_preprint.pdf"
website: "http://www.complang.tuwien.ac.at/cc2011"
---

**Abstract:** Purity Analysis is the problem of determining whether or not a method may have side-effects. This has applications in automatic parallelisation, extended static checking, and more. We present a novel purity system for Java that employs purity annotations which can be checked modularly. This is done using a flow-sensitive, intraprocedural analysis. The system exploits two properties, called freshness and locality, to increase the range of methods that can be considered pure. JPure also includes an inference engine for annotating legacy code. We evaluate our system against several packages from the Java Standard Library. Our results indicate it is possible to uncover significant amounts of purity efficiently.
