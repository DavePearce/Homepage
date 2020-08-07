---
date: 2016-01-01
kind: "thesis"
tag: "whiley"
title: "Lifetime Analysis for Whiley"
authors: "Sebastian Schweizer"
school: "University of Kaiserslautern"
thesis: "MSc"
preprint: "Schweizer16_MSc.pdf"

---

**Abstract.** Safety critical environments require high programming standards. Verification is a way to prove absence of certain faults and to make sure that a program meets a given specification. Unfortunately, most modern programming languages do not actively support verification.  External tools are necessary and there is no good integration with the development process.

hiley is a programming language that aims to popularize verification. Functions can be annotated with pre- and postconditions and the compiler ships with a verifier that ensures that the implementation satisfies the specification. Verification is automatic, the programmer only needs to provide loop invariants. Verified Whiley programs are guaranteed to be free of runtime failures like index-out-of-bounds access in arrays and nullpointer dereferences.

Rust is a new programming language that aims to be fast and safe, and it is classified as a systems programming language. Rust introduces a revolutionary concept of ownership and lifetimes that allows for automatic and safe memory management without using garbage collection. A lifetime roughly states how long a dynamically allocated portion of memory can be used. Each allocation belongs to a unique owner, and as soon as the owner’s lifetime ends the memory will be freed, without the need for garbage collection.  Memory safety is guaranteed by using static checks at compile time.

This thesis extends the Whiley programming language to introduce a concept of lifetimes similar to Rust. But both programming languages have a different focus. We therefore need to adapt the concept such that it fits Whiley’s environment. Our extension involves changes to the language syntax and several parts of the compiler and intermediate code formats. A main challenge is the treatment of lifetimes for subtyping with recursive types and in method invocations.  Whiley currently compiles to bytecode for the Java Virtual Machine (JVM), using its garbage collector to deallocate memory. There is an experimental compiler for Whiley that generates C code, but it is not yet able to deallocate dynamically allocated memory.  We show how the compiler can use lifetimes for memory management without garbage collection, though the actual implementation for this part is left as future work. This allows one to greatly improve the Whiley to C compiler, which is necessary to run Whiley programs on embedded devices that do not have enough resources to execute the JVM.





