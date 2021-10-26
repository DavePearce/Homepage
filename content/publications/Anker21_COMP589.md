---
date: 2021-11-01
kind: "honsreport"
tags: ["whiley"]
title: "Compiling Whiley for Embedded Systems"
authors: "Juan van den Anker"
thesis: "Research Project (COMP589)"
school: "Victoria University of Wellington"
preprint: "Anker21_COMP589.pdf"
---

**Abstract.** 

Whiley is a statically typed language that supports both
object-oriented as well as functional programming language
paradigms. Like many other programming languages, Whiley also supports
variables, primitive types (e.g. int, bool), methods, control-flow
statements (e.g. if, while) and many common expressions (e.g. `+`,
`-`, ). Where Whiley importantly differs from other programming
languages, is Whiley’s support for expressing specifications, Union
types, Unbounded Arithmetic and Extended Static
checking. Microcontrollers like for example an Arduino, are small
electronically based processors with extremely limited resources
compared to processors found in computers. This projects goal is to
design an develop a While Compiler plugin that translates Whiley
Applications into microcontroller compatible C-code. Although this
plugin supports only a subset of the Whiley Language Specification,
it provides support for enough language features to successfully
build, compile and execute an example application on a
microcontroller. This document provides a background of this project,
provides the design and implementation of the compiler plugin and also
provides an example application and a hardware circuit to test the
result of the compiler on an actual microcontroller. Finally, this
project provides a solution to integrate and re-use a large set of
testcases into our development cycle.






