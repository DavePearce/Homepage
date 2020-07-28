---
date: 2010-01-01
type: "conference"
title: "Understanding the Impact of Collection Contracts on Design"
authors: "Stephen F. Nelson, David J. Pearce and James Noble"
booktitle: "Conference on Objects, Models, Components, Patterns (TOOLS EUROPE)"
pages: "61--78"
copyright: "Springer"
DOI: "10.1007/978-3-642-13953-6_4"
preprint: "NPN10_TOOLS_preprint.pdf"
---

**Abstract:** Java provides a specification for a user-defined general purpose equivalence operator for objects, but collections such as Set have more stringent requirements. This inconsistency breaks polymorphism: programmers must take care to follow Set’s contract rather than the more general Object contract if their object could enter a Set. We have dynamically profiled 30 Java applications to better understand the way programmers design their objects, to determine whether they program with collections in mind. Our results indicate that objects which enter collections behave very differently to objects which do not. Our findings should help developers understand the impact of design choices they make, and guide future language designers when adding support for collections and/or equality.
