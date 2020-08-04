---
draft: false
date: 2000-01-01
title: "GILK"
subtitle: "A dynamic instrumentation tool for the Linux kernel.  This uses binary instrumentation to enable a stock (i.e. without any source code modification) kernel to be instrumented whilst in execution."
tag: "gilk"
---

The GILK project was part of my master's thesis at [Imperial College London](https://www.imperial.ac.uk/). The project is all about dynamic instrumentation of the Linux Kernel. This means that a stock (i.e. without any source code modification) kernel can be instrumented whilst in execution! This is possible because the tool performs binary analysis on the kernel image to determine where it is safe to instrument. Furthermore, it makes use of the non-premtive property of the kernel to ensure that the instrumentation is safely updated. The tool employs a technique called dynamic code splicing to add low overhead instrumentation. Essentially, this involves overwriting instructions at the instrumentation point with a branch instruction to the instrumentation patch. To maintain correct kernel behaviour, those instructions which were overwritten are relocated into the instrumentation patch. There are some challenges for this method which are particular to the Intel x86 architecture and which, we believe, were first addressed by GILK.

The tool is driven through a GTK interface and uses a kernel module to perform the actual instrumentation. The binary analysis is made possible by a custom disassembler, which provides more information than can be obtained with libopcodes.  Since completing my master's thesis, some further work was done using GILK to measure IP packet arrival times. 

