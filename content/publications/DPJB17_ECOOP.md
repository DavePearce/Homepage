---
date: 2017-01-01
kind: "conference"
title: "Contracts in the Wild: A Study of Java Programs"
authors: "Jens Dietrich, David J. Pearce, Kamil Jezek and Premek Brada"
booktitle: "European Conference on Object Oriented Programming  (ECOOP)"
pages: "9:1--9:29"
copyright: "Schloss Dagstuhl--Leibniz-Zentrum fuer Informatik"
DOI: "10.4230/LIPIcs.ECOOP.2017.9"
preprint: "DPJB17_ECOOP_preprint.pdf"
slides: "DPJB17_ECOOP_slides.pdf"
website: "http://conf.researchr.org/home/ecoop-2017"
---

**Abstract:** The use of formal contracts has long been advocated as an approach to develop programs that are provably correct. However, the reality is that adoption of contracts has been slow in practice. Despite this, the adoption of lightweight contracts — typically utilising runtime checking — has progressed. In the case of Java, built-in features of the language (e.g. assertions and exceptions) can be used for this. Furthermore, a number of libraries which facilitate contract checking have arisen.

In this paper, we catalogue 25 techniques and tools for lightweight contract checking in Java, and present the results of an empirical study looking at a dataset extracted from the 200 most popular projects found on Maven Central, constituting roughly 351,034 KLOC. We examine (1) the extent to which contracts are used and (2) what kind of contracts are used. We then investigate how contracts are used to safeguard code, and study problems in the context of two types of substitutability that can be guarded by contracts: (3) unsafe evolution of APIs that may break client programs and (4) violations of Liskovs Substitution Principle (LSP) when methods are overridden. We find that: (1) a wide range of techniques and constructs are used to represent contracts, and often the same program uses different techniques at the same time; (2) overall, contracts are used less than expected, with significant differences between programs; (3) projects that use contracts continue to do so, and expand the use of contracts as they grow and evolve; and, (4) there are cases where the use of contracts points to unsafe subtyping (violations of Liskov Substitution Principle) and unsafe evolution.
