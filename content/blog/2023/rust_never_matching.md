---
date: 2023-04-16
title: "Pattern Matching in Neverland"
draft: true7
#metaimg: "images/2023/DafnyEVM_Preview.png"
#metatxt: "Formalising the EVM in Dafny allows us prove properties over bytecode sequences."
#twitter: ""
# reddit: "https://www.reddit.com/r/rust/comments/uigljf/puzzling_strong_updates_in_rust/"
---

Recently, I've been working on a tool for manipulating EVM bytecode
and assembly language.  I wanted to be able to describe _both_ low
level bytecode and assembly language using the same set of
instructions.  At the same time, I wanted instructions to be allowed
in a given context and others to be disallowed (e.g. labels only make
sense at the assembly language level, but not the bytecode level).  I
was hoping to encode this _precisely_ in stable Rust, but couldn't.
However, it turns out that you can do this on nightly where
[RFC1872](https://github.com/rust-lang/rust/issues/51085) is
implemented.  That is actually pretty neat, and I want to explain why.

## Background

A very simplified version of my bytecode system is the following:

```Rust
enum Instruction<T> {
    PUSH(usize),
    LABEL(T),
    JUMP(T),
    RET
}
```

This is a subset of the instructions I needed, but its enough to
illustrate what I was trying to achieve.  The intention is that we
instantiate `Instruction<T>` in one of two ways:

   1. `Instruction<usize>`.  This describes concrete bytecode
      instructions where exact jump destinations (expressed as byte
      offsets) are known.
   2. `Instruction<String>`.  This describes assembly language
      instructions which use labels to describe control-flow.

Thus, we can have a simple function which "assembles" assembly
language instructions into concrete instructions:

```Rust
fn assemble(&[Instruction<String>]) -> Vec<Instruction<usize>> { 
   ... 
}
```

(**Note**: To make this work properly, we'd want some error handling
but I'm ignoring that here).  Similarly, we can have a simple function
which "disassembles" concrete instructions back into assembly language
instructions:

```Rust
fn diassemble(&[Instruction<usize>]) -> Vec<Instruction<String>> { 
   ... 
}
```

The interesting thing about these two functions is that some
instructions don't make sense, depending on the context.  For example,
labels are not concrete instructions as, in effect, they are
artificial instructions used only for assembly language.  So, we
might write something like this:

```Rust
fn diassemble(insns: &[Instruction<usize>]) -> Vec<Instruction<String>> { 
   for insn in insns {
     match insn {
        ...
        LABEL(_) => { unreachable!(); }
     }
   }
   ...
}
```

This is fine as it goes, but its a shame we have to use a runtime
check.  So, I was wondering: _can we use Rust's type system to encode
these constraints?_ More specifically, so that
`Instruction::Label(usize)` cannot be instantiated, and cannot be
matched against.  The short answer is: yes!

## Stable Version

## Nightly Version

## Conclusion
