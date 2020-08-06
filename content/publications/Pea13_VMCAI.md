---
date: 2013-01-01
kind: "conference"
title: "Sound and Complete Flow Typing with Unions, Intersections and Negations"
authors: "David J. Pearce"
booktitle: "Proceedings of the Conference on Verification, Model Checking, and Abstract Interpretation (VMCAI)"
pages: "335--354"
copyright: "Springer"
DOI: "10.1007/978-3-642-35873-9_21"
preprint: "Pea13_VMCAI_preprint.pdf"
slides: "Pea13_VMCAI_slides.pdf"
website: "http://vmcai13.di.univr.it/Home.html"
---

**Abstract:** Flow typing is becoming a popular mechanism for typing existing programs written in untyped languages (e.g. JavaScript, Racket, Groovy). Such systems require intersections for the true-branch of a type test, negations for the false-branch, and unions to capture the flow of information at meet points. Type systems involving unions, intersections and negations require a subtype operator which is non-trivial to implement. Frisch et al. demonstrated that this problem was decidable. However, their proof was not constructive and does not lend itself naturally to an implementation. In this paper, we present a sound and complete algorithm for subtype testing in the presence of unions, intersections and negations.