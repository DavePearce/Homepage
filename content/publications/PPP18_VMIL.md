---
date: 2018-01-01
kind: "workshop"
tags: ["whiley"]
title: "Towards Compilation of an Imperative Language for FPGAs"
authors: "Baptiste Pauget, Alex Potanin and David J. Pearce"
booktitle: "Workshop on Virtual Machines and Language Implementations (VMIL)"
pages: "47--56"
copyright: "ACM Press"
DOI: "10.1145/3281287.3281291"
preprint: "PPP18_VMIL_preprint.pdf"
slides: "PPP18_VMIL_slides.pdf"
website: "https://2018.splashcon.org/track/vmil-2018"
---

**Abstract:** Field-Programmable Gate Arrays (FPGA’s) have been around since the early 1980s and have now achieved relatively widespread use. For example, FPGAs are routinely used for high-performance computing, financial applications, seismic modelling, DNA sequence alignment, software defined networking and, occasionally, are even found in smartphones. And yet, despite their success, there still remains something of a gap between programming languages and circuit designs for an FPGA. We consider the compilation of an imperative programming language, Whiley, to VHDL for use on an FPGA. A key challenge lies in splitting an arbitrary function into a series of pipeline stages, as necessary to expose as much task parallelism as possible. To do this, we introduce a language construct which gives the programmer control over how the pipeline is constructed.
