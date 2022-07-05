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

## Formalisation

We begin the formalisation process by defining the notion of a machine
state as follows:

```whiley
public type SVM is {
   u16 pc,
   u16 sp,
   u16[] data,
   u16[] stack,
   u8[] code
}
where sp <= |stack| && |stack| < 65536 
where pc <= |code| && |code| < 65536
```

This defines the essential components of our virtual machine.  For
simplicity, the `code` sequence is an array of bytes separate from the
`data` section (i.e. we're not following a {{<wikip
page="Von_Neumann_architecture">}}Von Neumann
Architecture{{</wikip>}}).  In addition, I'm imposed some constraints:
**(1)** on `sp` and the maximum stack size; **(2)** on `pc` and the
maximum code size.  In principle, we could limit the data size as well
(though this is not really necessary).

Now, before formalising the individual bytecodes, its useful to have a
notion of the machine being *halted*.  This is just helpful to signal
error states, and we can define it as follows:

```whiley
public property isHalted(SVM st) -> (bool r):
    return st.pc == |st.code|
```

Basically, whenever the `pc` is passed the end of the `code` size,
then we consider the machine to be "halted".  Once it reaches this
state, then execution is finished.

### Bytecodes

We now need to formalise the *semantics* of each bytecode.  That is,
specify how each bytecode should execute.  A simple example is the following:

```whiley
property evalPOP(SVM st) -> (SVM nst):
    if st.sp >= 1:
        return pop(st)
    else:
        return halt(st)
```

This specifies that evaluating a `POP` bytecode requires at least one
element on the stack, otherwise the machine halts.  If there is one
element, then its popped off.  This uses two helpers `halt` and `pop`
defined as follows:

```whiley
public property pop(SVM st) -> SVM
requires st.sp > 0:
   return st{sp:=st.sp-1}

property halt(SVM st) -> SVM:
   return st{pc:=|st.code|}
```

Whilst these could have been written inline, I find it helpful to give
them more descriptive names.  I typically refer to these low-level
building blocks as _microcodes_ and, as for a physical machine, we'll
see these are reused a lot in defining the semantics of our bytecodes.  

Another example to illustrate is the following:

```whiley
property evalADD(SVM st) -> (SVM nst):
    if st.sp >= 2:
        // Read operands
        u16 r = peek(st,1)
        u16 l = peek(st,2)
        u16 v = (l + r) % 65536
        // done
        return push(pop(pop(st)),v)
    else:
        return halt(st)
```

Here, the right-hand side `r` is taken from the first (i.e. topmost)
stack item, whilst the left-hand side `l` is from the second stack
item.  Furthermore, the addition itself must be modulo `65536` in
order that the value assigned to `v` remains within bounds.  Indeed if
the modulo operation was left out, then the verifier would highlight a
potential integer overflow (and this is where tools like Whiley
shine).  Again, our definition above uses some more microcodes:

```whiley
public property push(SVM st, u16 k) -> SVM
requires st.sp < |st.stack|:
    return st{stack:=st.stack[st.sp:=k]}{sp:=st.sp+1}

public property peek(SVM st, int n) -> u16
requires st.sp < |st.stack|
requires 0 < n && n <= st.sp:
    return st.stack[st.sp - n]
```

### Execution

## References

   * **KEVM: A Complete Semantics of the Ethereum Virtual Machine**,
     Everett Hildenbrandt *et al.*. In *Proc CSF*, 2018. [(PDF)](http://t-news.cn/Floc2018/FLoC2018-pages/proceedings_paper_513.pdf)
     
   * **Defining the Ethereum Virtual Machine for Interactive Theorem
     Provers**, Y. Hirai.  In *Proc WTSC*, 2017. [(PDF)](https://yoichihirai.com/malta-paper.pdf)
     
     
