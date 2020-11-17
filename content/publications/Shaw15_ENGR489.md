---
date: 2015-01-01
kind: "honsreport"
tags: ["whiley"]
title: "Identifying Redundant Test Cases"
authors: "Marc Shaw"
thesis: "Final Year Project (ENGR489)"
school: "Victoria University of Wellington"
preprint: "Shaw15_ENGR489.pdf"

---

**Abstract.** This project investigates tracing the method call information of Java JUnit Tests and using the information to identify redundant tests in a test suite. There are a variety of different methods implemented (and experimented on) to identify redundancy within a suite. The techniques involve retrieving different information from the tests and analysing it differently. The experiments show two main points. First, that a set of the method calls work well as a heuristic.  This allowed for a pipeline approach to be implemented where the output of the heuristic is pipelined into more thorough analysis. Second, they showed that taking into account for the parameter information of the method calls gave insight into the context of the call. This increased the confidence that the identified tests were truly redundant.



