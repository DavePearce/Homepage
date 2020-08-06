---
date: 2012-01-01
kind: "thesis"
title: "Profiling Initialisation Behaviour in Java"
authors: "Stephen Nelson"
thesis: "PhD"
school: "Victoria University of Wellington"
preprint: "Nelson12_PhD.pdf"
---

**Abstract:** Freshly created objects are a blank slate: their mutable state and their constant properties must be initialised before they can be used. Programming languages like Java typically support object initialisation by providing constructor methods. This thesis examines the actual initialisation of objects in real-world programs to determine whether constructor methods support the initialisation that programmers actually perform. Determining which object initialisation techniques are most popular and how they can be identified will allow language designers to better understand the needs of programmers, and give insights that VM designers could use to optimise the performance of language implementations, reduce memory consumption, and improve garbage collection behaviour.
Traditional profiling typically either focuses on timing, or uses sampling or heap snapshots to approximate whole program analysis. Classifying the behaviour of objects throughout their lifetime requires analysis of all program behaviour without approximation. This thesis presents two novel whole-program object profilers: one using purely class modification (#prof), and a hybrid approach utilising class modification and JVM support (rprof ). #prof modifies programs using aspect-oriented programming tools to generate and aggregate data and examines objects that enter different collections to determine whether correlation exists between initialisation behaviour and the use of equality operators and collections. rprof confirms the results of an existing static analysis study of field initialisation using runtime analysis, and provides a novel study of object initialisation behaviour patterns.