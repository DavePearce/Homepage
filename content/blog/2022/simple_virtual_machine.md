---
date: 2022-06-28
title: "Formalising a Simple Virtual Machine"
draft: true
# metaimg: "images/2022/AuctionContract_Preview.png"
# metatxt: "Verifying a smart contract in Whiley finds lots of problems."
#twitter: ""
# reddit: "https://www.reddit.com/r/rust/comments/uigljf/puzzling_strong_updates_in_rust/"
---

Since starting my new role at [ConsenSys](https://consensys.net/), I
have become interested in formalising virtual machines.  For example,
there are already some formalisations of the [Ethereum Virtual
Machine](https://ethereum.org/en/developers/docs/evm/) (e.g. in
[K](https://github.com/runtimeverification/evm-semantics),
[Lem](https://github.com/pirapira/eth-isabelle),
[Coq](https://github.com/ivan71kmayshan27/coq-evm)).  So, I figured it
would be interesting to explore VM formalisation in a tool like Whiley
(or similarly Dafny).

## Overview

In this post, I'm going to formalise a *Simple Virtual Machine (SVM)*
which has only a few instructions.  The goal is just to illustrate the
technique and to show that, using the formalisation, we can prove
interesting properties (e.g. that certain optimisations are safe).

The machine state includes a _stack_ of `u16` words and a _memory_
store of `u16` words.  In addition, we have a _program counter_ (`pc`)
and a _stack pointer_ (`sp`), along with a sequence of bytecodes.  To
illustrate, here are descriptions for a few example bytecodes:

|Bytecode | Description  | Stack | Memory |
|:-------:|:-------------|:--|:-------|
| `NOP`   | _No-operation_ | `... ⟹   ...`     ||
| `PUSH w`  | _Push (immediate) word onto stack_ | `... ⟹   w ...`     ||
| `POP`   | _Pop word from stack_ | `... w ⟹   ...` ||
| `STORE` | _Store word in memory_ | `... a w ⟹   ...` | `data[a] = w` |
| `LOAD`  | _Load word from memory_ | `... a ⟹   w ...` | `w = data[a]` |
| `ADD`   | _Add words together_ | `... v w  ⟹   u ...` | `u = (v+w) % 0xffff` |

This provides a fairly typical description for a bytecode machine
(e.g. similar in style to that for the
[JVM](https://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5)).
Each bytecode typically has an effect on the stack, and may also
effect the memory.  The effect on the stack is described as a rewrite
popping zero or more elements on the stack, and pushing zero or more
elements on the stack.  For example, the effect `... w ⟹ ...` for
`POP` states that, for the bytecode to execute, there must be at least
one word `w` on the stack and, afterwards, that word is removed (but
everything else remains the same).  Likewise, the memory effect for
`STORE` is that the word `w` is written to address `a` in the `data`
memory.

Whilst the above description is fairly clear and easy to follow, there
are still some questions.  For example, what happens if an address `a`
is _out of bounds_ for the `data` store?  Likewise, can the stack ever
be full?  We might also be interested in checking whether an
optimisation _preserves_ the program's semantics (e.g. that `PUSH 0;
POP` is equivalent to an empty code sequence).  With these questions
in mind, it is useful to try and formalise our description in a
machine-readable (and checkable) manner.  To do this, I am of course
going to use Whiley. However, it is worth pointing out that other
systems could easily be used as well,
(e.g. [Dafny](https://dafny.org/), the [K
framework](https://kframework.org/), etc).

## References

   * **KEVM: A Complete Semantics of the Ethereum Virtual Machine**,
     Everett Hildenbrandt *et al.*. In *Proc CSF*, 2018. [(PDF)](http://t-news.cn/Floc2018/FLoC2018-pages/proceedings_paper_513.pdf)
     
   * **Defining the Ethereum Virtual Machine for Interactive Theorem
     Provers**, Y. Hirai.  In *Proc WTSC*, 2017. [(PDF)](https://yoichihirai.com/malta-paper.pdf)
     
     
