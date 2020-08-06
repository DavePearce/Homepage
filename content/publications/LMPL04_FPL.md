---
date: 2004-01-01
kind: "conference"
title: "Automating Optimized Table-with-Polynomial Function Evaluation for FPGAs"
authors: "Dong-U Lee, Oskar Mencer, David J. Pearce and Wayne Luk"
booktitle: "Conference on Field-Programmable Logic and its Applications (FPL)"
pages: "264--373"
copyright: "Springer"
DOI: "10.1007/978-3-540-30117-2_38"
preprint: "LMPL04_FPL_preprint.pdf"
---

**Abstract:** Function evaluation is at the core of many compute-intensive applications which perform well on reconfigurable platforms. Yet, in order to implement function evaluation efficiently, the FPGA programmer has to choose between a multitude of function evaluation methods such as table lookup, polynomial approximation, or table lookup combined with polynomial approximation. In this paper, we present a methodology and a partially automated implementation to select the best function evaluation hardware for a given function, accuracy requirement, technology mapping and optimization metrics, such as area, throughput and latency. The automation of function evaluation unit design is combined with ASC, A Stream Compiler, for FPGAs. On the algorithmic side, MATLAB designs approximation algorithms with polynomial coefficients and minimizes bitwidths. On the hardware implementation side, ASC provides partially automated design space exploration. We illustrate our approach for sin(x), log(1 + x) and 2x with a selection of graphs that characterize the design space with various dimensions, including accuracy, precision and function evaluation method. We also demonstrate design space exploration by implementing more than 400 distinct designs.