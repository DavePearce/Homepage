---
date: 2008-01-01
type: "thesis"
title: "The Java Query Language"
authors: "Darren Willis"
thesis: "MSc"
school: "Victoria University of Wellington"
preprint: "Willis08_MSc.pdf"
---

**Abstract:** This thesis describes JQL, an extension to Java which provides object querying. Object querying is an abstraction of operations over collections, including operations that combine multiple collections, which would otherwise have to be manually implemented. Such manual implementations are ‘low-level’; they force developers to specify how an operation is done, rather than what the operation to do is. Many operations over collections can easily be expressed as queries. JQL provides a Java-like syntax for expressing these queries, an optimizing query evaluator that can dynamically reconfigure query evaluation, and a caching system that allows querying to replace common collection operations with incrementally cached versions.