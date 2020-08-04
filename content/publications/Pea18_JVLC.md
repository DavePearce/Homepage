---
draft: false
date: 2018-01-01
type: "journal"
tag: "whiley"
title: "On Declarative Rewriting for Sound and Complete Union, Intersection and Negation Types"
authors: "David J. Pearce"
booktitle: "Journal of Visual Languages & Computing"
volume: "50"
pages: "84--101"
year: "2018"
copyright: "Elsevier"
DOI: "10.1016/j.jvlc.2018.10.004"
preprint: "Pea18_JVLC_preprint.pdf"
---

**Abstract.**  Implementing the type system of a programming language is a critical task that is oftendone in an ad-hoc fashion.  Whilst this makes it hard to ensure the system is sound, italso makes it difficult to extend as the language evolves. We are interested in describing type systems using rewrite rules from which an implementation can be automaticallygenerated. Whilst not all type systems are easily expressed in this manner, those involving unions, intersections and negations are well-suited for this. For example, subtyping in such systems is naturally expressed as a maximal reduction over the intersection oftypes involved.  In this paper, we consider a relatively complex type system involving unions, inter-sections and negations developed previously in the context of type checking. This system was not developed with rewriting in mind, though clear parallels are immediately apparent from the original presentation.  For example, the system presented requiredtypes be first converted into a variation on Disjunctive Normal Form.  However, some aspects of the original system are more challenging to express with rewrite rules.  Weidentify that the original system can, for the most part, be reworked to enable a natural expression using rewrite rules.  We present an implementation of our rewrite rules inthe Whiley Rewrite Language (WyRL), and report performance results compared witha hand-coded solution. We also present an implementation of our system in the Rascal rewriting system, and find different trade offs.