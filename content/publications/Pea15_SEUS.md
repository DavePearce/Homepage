---
date: 2015-01-01
kind: "workshop"
tag: "whiley"
title: "Integer Range Analysis for Whiley on Embedded Systems"
authors: "David J. Pearce"
booktitle: "IEEE/IFIP Workshop on Software Technologies for Future Embedded and Ubiquitous Systems (SEUS)"
pages: "26--33"
copyright: "IEEE"
DOI: "10.1109/ISORCW.2015.54"
preprint: "Pea15_SEUS_preprint.pdf"
slides: "Pea15_SEUS_slides.pdf"
website: "http://www.complang.tuwien.ac.at/seus2015/"
---

**Abstract:** Programs written in the Whiley programming language are verified at compile-time to ensure all function specifications are met. The purpose of doing this is to eliminate as many software bugs as possible and, thus, Whiley is ideally suited for use in safety-critical systems. The language was designed from scratch to simplify verification as much as possible. To that end, arithmetic types in Whiley consist of unbounded integers and rationals and this poses a problem for use in memory constrained embedded devices. However, function specifications in Whiley provide a rich source of information from which finite bounds for integer variables can be determined. In this paper, we present a technique for range analysis of integer variables in Whiley. Previous work is typically based on dataflow analysis which requires a fixed-point computation and necessitates the use of imprecise widenings to ensure termination. However, the presence of loop and data type invariants in Whiley means that loops can be handled quickly and precisely.