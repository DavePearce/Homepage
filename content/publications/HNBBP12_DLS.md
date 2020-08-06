---
date: 2012-01-01
kind: "conference"
title: "Patterns as Objects in Grace"
authors: "Michael Homer, James Noble, Kim Bruce, Andrew Black and David J. Pearce"
booktitle: "Dynamic Languages Symposium (DLS)"
pages: "17--28"
copyright: "ACM Press"
DOI: "10.1145/2384577.2384581"
preprint: "HNBBP12_DLS_preprint.pdf"
website: "http://www.dynamic-languages-symposium.org/dls-12/"
---

**Abstract:** Object orientation and pattern matching are often seen as conflicting approaches to program design. Object oriented programs place type-dependent behaviour inside objects and invoke it via dynamic dispatch, while pattern matching programs place type-dependent behaviour outside data structures and invoke it via multiway conditionals (case statements). Grace is a new, dynamic, object-oriented language designed to support teaching: to this end, Grace needs to support both styles. In this paper we explain how this conflict can be resolved gracefully: by modelling patterns and cases as partial functions, reifying those functions as first-class objects, and then building up complex patterns from simpler ones using pattern combinators. We describe our design for pattern matching in Grace, and its implementation as an object-oriented framework.