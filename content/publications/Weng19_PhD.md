---
date: 2019-01-01
kind: "external"
tag: "whiley"
title: "Efficient compilation of a verification-friendly programming language"
authors: "Min Hsien (Sam) Weng"
thesis: "PhD"
school: "University of Waikato"
---

**Abstract.** This thesis develops a compiler to convert a program written in the verification friendly programming language Whiley into an efficient implementation in C. Our compiler uses a mixture of static analysis, run-time monitoring and a code generator to and faster integer types, eliminate unnecessary array copies and de-allocate unused memory without garbage collection, so that Whiley programs can be translated into C code to run fast and for long periods on general operating systems as well as limited-resource embedded devices. We also present manual and automatic proofs to verify memory safety of our implementations, and benchmark on a variety of test cases for practical use. Our benchmark results show that, in our test suite, our compiler effectively reduces the time complexity to the lowest possible level and stops all memory leaks without causing double-freeing problems. The performance of implementations can be further improved by choosing proper integer types within the ranges and exploiting parallelism in the programs.


