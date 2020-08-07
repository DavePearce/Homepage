---
date: 2013-01-01
kind: "honsreport"
tag: "whiley"
title: "Compiling Whiley Programs for a General Purpose GPU"
authors: "Melby Raurus"
thesis: "Final Year Project (ENGR489)"
school: "Victoria University of Wellington"
preprint: "Raurus13_ENGR489.pdf"

---

**Abstract.** This project investigates improving the performance of Whiley programs by executing portions of these programs on GPUs while maintaining as closely as possible the semantics of the language. Programs written in languages such as Whiley are typically not well suited to execution on GPUs which exhibit large-scale data parallelism in contrast to small-scale task parallelism seen on CPUs.  Therefore, the developed solution parallelises only the portions of programs which are likely to benefit — specifically, certain types of loops — and applies several optimisations to increase performance. The evaluation of this technique validates the approach with one benchmark exhibiting a 5.2x speed improvement, and identifies areas where the compiler can produce further improved code.




