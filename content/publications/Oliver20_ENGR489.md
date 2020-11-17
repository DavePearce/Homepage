---
date: 2020-11-01
kind: "honsreport"
tags: ["whiley"]
title: "Profiling the Java Compiler for Improved Incremental Compiler Design"
authors: "Philip Oliver"
thesis: "Final Year Project (ENGR489)"
school: "Victoria University of Wellington"
preprint: "Oliver20_ENGR489.pdf"

---

**Abstract.** Compiling a program is a process which can take a long time, thereby breaking up a developer's workflow and productivity. Incremental compilation is a method which aims to solve this problem. Incremental compilers cache the results of previous compilations and reuse the compiled assembly or byte code of unchanged sections of a program. When designing an incremental compiler, it is vital to understand the estimated workload. Understanding which sections of the compilation pipeline find the most errors can help the designer to identify which parts require the most resources. In this project a web application has been developed to capture realistic compilation workloads using an instrumented Java compiler. Information about the stages encountered in a compilation are printed to the console from the instrumented compiler. These workloads are analysed to identify the effect on the compiler. This analysis gives insight into where in the compilation pipeline the most time is spent, during an incremental compilation session. In particular, most compilation errors occur in the parsing stage, but a large number occur in type checking for programs with use of complex type systems.




