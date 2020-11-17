---
draft: false
date: 2018-01-01
kind: "conference"
tags: ["whiley"]
title: "Rewriting for Sound and Complete Union, Intersection and Negation Types"
authors: "David J. Pearce"
booktitle: "Proceedings of the Conference on Generative Programming: Concepts & Experience (GPCE)"
pages: "117--130"
year: "2018"
copyright: "ACM Press"
DOI: "10.1145/3170492.3136042"
preprint: "Pea18_GPCE_preprint.pdf"
slides: "Pea18_GPCE_slides.pdf"
website: "http://conf.researchr.org/track/gpce-2017/gpce-2017-GPCE-2017"
---

**Abstract:** Implementing the type system of a programming language is a critical task that is often done in an ad-hoc fashion. Whilst this makes it hard to ensure the system is sound, it also makes it difficult to extend as the language evolves. We are interested in describing type systems using declarative rewrite rules from which an implementation can be automatically generated. Whilst not all type systems are easily expressed in this manner, those involving unions, intersections and negations are well-suited for this.
In this paper, we consider a relatively complex type system involving unions, intersections and negations developed previously. This system was not developed with rewriting in mind, though clear parallels are immediately apparent from the original presentation. For example, the system presented required types be first converted into a variation on Disjunctive Normal Form. We identify that the original system can, for the most part, be reworked to enable a natural expression using declarative rewrite rules. We present an implementation of our rewrite rules in the Whiley Rewrite Language (WyRL), and report performance results compared with a hand-coded solution.
