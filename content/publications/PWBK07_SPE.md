---
draft: false
date: 2007-01-01
kind: "journal"
tags: ["djprof"]
title: "Profiling with AspectJ"
authors: "David J. Pearce, Matthew Webster, Robert Berry and Paul H.J. Kelly"
booktitle: "Software: Practice and Experience"
volume: "37"
number: "7"
pages: "747--777"
copyright: "Wiley"
DOI: "10.1002/spe.788"
preprint: "PWBK07_SPE_preprint.pdf"
---
**Abstract.** This paper investigates whether AspectJ can be used for efficient profiling of Java programs. Profiling differs from other applications of AOP (e.g. tracing), since it necessitates efficient and often complex interactions with the target program. As such, it was uncertain whether AspectJ could achieve this goal. Therefore, we investigate four common profiling problems (heap usage, object lifetime, wasted time and time-spent) and report on how well AspectJ handles them. For each, we provide an efficient implementation, discuss any trade-offs or limitations and present the results of an experimental evaluation into the costs of using it. Our conclusions are mixed. On the one hand, we find that AspectJ is sufficiently expressive to describe the four profiling problems and reasonably efficient in most cases. On the other hand, we find several limitations with the current AspectJ implementation that severely hamper its suitability for profiling.
