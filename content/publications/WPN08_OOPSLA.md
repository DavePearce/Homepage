---
date: 2008-01-01
type: "conference"
title: "Caching and Incrementalisation for the Java Query Language"
authors: "Darren Willis, David J. Pearce and James Noble"
booktitle: "Conference on Object-Oriented Programming Systems, Languages & Applications (OOPSLA)"
pages: "1--17"
copyright: "ACM Press"
DOI: "10.1145/1449955.1449766"
preprint: "WPN08_OOPSLA_preprint.pdf"
website: "http://www.oopsla.org/oopsla2008//"
---

**Abstract:** Many contemporary object-oriented programming languages support first-class queries or comprehensions. These language extensions make it easier for programmers to write queries, but are generally implemented no more efficiently than the code using collections, iterators, and loops that they replace. Crucially, whenever a query is re-executed, it is recomputed from scratch. We describe a general approach to optimising queries over mutable objects: query results are cached, and those caches are incrementally maintained whenever the collections and objects underlying those queries are updated. We hope that the performance benefits of our optimisations may encourage more general adoption of first-class queries by object-oriented programmers.

