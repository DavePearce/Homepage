---
date: 2011-01-01
type: "thesis"
title: "OwnKit: Ownership Inference for Java"
authors: "Constantine Dymnikov"
thesis: "MSc"
school: "Victoria University of Wellington"
preprint: "Dymnikov11_MSc.pdf"
---

**Abstract:** Object ownership allows us to statically control run-time aliasing in order to provide a strong notion of object encapsulation. Unfortunately in order to use ownership, code must first be annotated with extra type information. This imposes a heavy burden on the programmer, and has contributed to the slow adoption of ownership. Ownership inference is the process of reconstructing ownership type information based on the existing ownership patterns in code. This thesis presents OwnKit — an automatic ownership inference tool for Java. OwnKit conducts inference in a modular way: by only considering a single class at the time. The modularity makes our algorithm highly scalable in both time and memory usage.