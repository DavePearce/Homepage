---
date: 2013-01-01
type: "workshop"
title: "A Calculus for Constraint-Based Flow Typing"
authors: "David J. Pearce"
booktitle: "Workshop on Formal Techniques for Java-like Languages (FTFJP)"
pages: "Article 7"
copyright: "ACM Press"
DOI: "10.1145/2489804.2489810"
preprint: "Pea13_FTFJP_preprint.pdf"
slides: "Pea13_FTFJP_slides.pdf"
website: "http://types.cs.washington.edu/ftfjp2013/"
---

**Abstract:** Flow typing offers an alternative to traditional Hindley-Milner type inference. A key distinction is that variables may have different types at different program points. Flow typing systems are typically formalised in the style of a dataflow analysis. In the presence of loops, this requires a fix-point computation over typing environments. Unfortunately, for some flow typing problems, the standard iterative fix-point computation may not terminate. We formalise such a problem we encountered in developing the Whiley programming language, and present a novel constraint-based solution which is guaranteed to terminate. This provides a foundation for others when developing such flow typing systems.