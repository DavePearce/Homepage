---
date: 2000-01-01
kind: "thesis"
title: "Instrumenting the Linux Kernel"
tags: ["gilk"]
authors: "David J. Pearce"
thesis: "MEng"
school: "Imperial College of Science, Technology and Medicine, University of London"
preprint: "Pearce00_MEng.pdf"
---

**Abstract:** An instrumentation tool has been developed for the Linux kernel that uses the relatively new technique of runtime code splicing. This permits instrumentation of a running system, without any source code modification, by redirecting the flow of control to pass through carefully written code patches.

The result is a tool that can insert arbitrary code before and after almost every basic block of an executing Linux kernel. A new twist on the original technique has been developed, called local bounce allocation, that overcomes some of the problems encountered on variable length architectures. Furthermore, the tool has been demonstrated to produce results that are at least as accurate as an existing instrumentation tool whilst providing a major improvement upon it.
