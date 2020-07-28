---
date: 2008-01-01
type: "conference"
title: "Patterns for ADT Optimisation"
authors: "David J. Pearce and James Noble"
booktitle: "Proceedings of the conference on Pattern Languages of Programs (PLoP)"
pages: "Article 26"
copyright: "ACM Press"
DOI: "10.1145/1753196.1753227"
preprint: "PN08_PLOP_preprint.pdf"
website: "http://hillside.net/plop/2008/"
---

**Abstract:** Operations on abstract data types can be classified as either queries or updates — those that either query the current state, or update it. Modern object-oriented programming languages require classes/interfaces to support a predefined set of such operations. This presents a challenge for software designers, since a fixed interface can severely restrict the opportunities for optimisation. In this paper, we present two common patterns — Specific Query Optimisation and Generalised Query Optimisation — for optimising such operations. The first requires specific knowledge of which operation to optimise beforehand, whilst the latter provides more leeway in this regard. These patterns are commonly occurring in software, and we find numerous instances of them within the Java standard libraries.