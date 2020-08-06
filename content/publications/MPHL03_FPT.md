---
date: 2003-01-01
kind: "conference"
title: "Design Space Exploration with A Stream Compiler"
authors: "Oskar Mencer, David J. Pearce, Lee Howes and Wayne Luk"
booktitle: "Conference on Field-Programmable Technology (FPT)"
pages: "270--277"
copyright: "IEEE"
DOI: "10.1109/FPT.2003.1275757"
preprint: "MPHL03_FPT_preprint.pdf"
website: "http://www.icfpt.org/fpt2003/"
---

**Abstract:** We consider speeding up general-purpose applications with hardware accelerators. Traditionally hardware accelera- tors are tediously hand-crafted to achieve top performance. ASC (A Stream Compiler) simplifies exploration of hard- ware accelerators by transforming the hardware design task into a software design process using only ’gcc’ and ’make’ to obtain a hardware netlist. ASC enables programmers to customize hardware accelerators at three levels of abstraction: the architecture level, the functional block level, and the bit level. All three customizations are based on one uniform representation: a single C++ program with custom types and operators for each level of abstraction.
This representation allows ASC users to express and reason about the design space, extract parallelism at each level and quickly evaluate different design choices. In addition, since the user has full control over each gate-level resource in the entire design, ASC accelerator performance can always be equal to or better than hand-crafted designs, usually with much less effort. We present several ASC benchmarks, including wavelet compression and Kasumi encryption.
