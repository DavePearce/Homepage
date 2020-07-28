---
date: 2009-01-01
type: "thesis"
title: "Mocha: Type Inference for Java"
authors: "Chris Male"
thesis: "MSc"
school: "Victoria University of Wellington"
preprint: "Male09_MSc.pdf"
---

**Abstract:** Static typing allows type errors to be revealed at compile time, which reduces the amount of maintenance and debugging that must occur later on. However, often the static type information included in source code is duplicate or redundant. Type inference resolves this issue by inferring the types of variables from their usage, allowing much of the type information to be omitted. This thesis formally describes Mocha, an extension to Java, which supports the inference of both local variable and field types. Mocha has unique support for local variables having different types at different program points which allows variables to be retyped due to certain conditional statements. It also includes a procedure for handling the effect Java exceptions have on the type inference process.