---
date: 2018-11-01
kind: "honsreport"
tags: ["whiley"]
title: "Whiley Memory Analyser"
authors: "Benjamin Russell"
thesis: "Final Year Project (ENGR489)"
school: "Victoria University of Wellington"
preprint: "Russel18_ENGR489.pdf"
---

**Abstract.** The Whiley Memory Analyser (WyMA) is a tool for performing a static analysis on Whiley files to evaluate their worst case memory consumption. We evaluate worst case memory consumption to avoid potential errors in systems with limited memory, and it is evaluated statically to ensure it is the absolute worst case. In embedded systems overloading memory can cause unexpected behaviour and hard to diagnose errors, in safety critical systems these issues can cause harm or damages. Whiley already performs static analysis on its code to verify specifications which include type constraints, this means that there were already procedures in place that will help our analysis. We use type constraints to determine the memory requirements of variable declarations, and we use those to analyse the requirements of the programs structures until we know the worst case memory consumption of the file as a whole. Later we used WyMA to assess the memory needs of an example Whiley file to evaluate its accuracy, and see how it can be used to improve the code by guiding refactoring.





