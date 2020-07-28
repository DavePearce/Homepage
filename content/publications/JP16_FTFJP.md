---
date: 2016-01-01
title: "A Mechanical Soundness Proof for Subtyping over Recursive Types"
type: "workshop"
authors: "Timothy Jones and David J. Pearce"
booktitle: "Workshop on Formal Techniques for Java-like Languages (FTFJP)"
pages: "Article 1"
copyright: "ACM Press"
DOI: "10.1145/2955811.2955812"
preprint: "JP16_FTFJP_preprint.pdf"
website: "http://2016.ecoop.org/track/FTfJP-2016"
---

**Abstract:**  Structural type systems provide an interesting alternative to the more common nominal typing scheme. Several existing languages employ structural types in some form, including Modula-3, Scala and various extensions proposed for Java. However, formalising a recursive structural type system is challenging. In particular, the need to use structural coinduction remains a hindrance for many. We formalise in Agda a simple recursive and structural type system with products and unions. Agda proves useful here because it has explicit support for coinduction and will raise an error if this is misused. The implementation distinguishes between inductively and coinductively defined types: the former corresponds to a finite representation, such as found in source code or the internals of a compiler, while the latter corresponds to a mathematical ideal with which we can coinductively define relations and proofs that are easily applied back to the inductive interpretation. As an application of this, we provide a mechanised proof of subtyping soundness against a semantic embedding of the types into Agda.