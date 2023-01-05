---
date: 2023-01-04
title: "Disassembling EVM Bytecode (the Basics)"
draft: false
#metaimg: "images/2022/TokenContract_Preview.png"
#metatxt: "Verifying a token contract in Whiley helps to find problems."
#twitter: ""
# reddit: "https://www.reddit.com/r/rust/comments/uigljf/puzzling_strong_updates_in_rust/"
---

Recently, I've been looking at how to disassemble (and eventually
decompile) bytecode intended for the Ethereum Virtual Machine (EVM).
This is actually an interesting computer science problem which I
thought was worth talking more about.  My goal is just to explain the
problem and outline a simple solution
(which I've [implemented in Rust](https://github.com/DavePearce/EvmIL)).  

## Background

The EVM is a stack-based {{<wikip page="Virtual_machine">}}virtual
machine{{</wikip>}} which supports both _volatile_ and _non-volatile_
storage.  The volatile storage is called _memory_ and exists only for
the life of a transaction.  In contrast, the non-volatile storage is
just called _storage_ and persists between transactions.

Whilst instructions in the EVM are variable length, the vast majority
are only one byte in length.  The family of push instructions
(e.g. `PUSH1`, `PUSH2`, etc) are the only instructions which have
operands.  These simply push a 256bit word constructed from their
operand on the stack.  For example, the `PUSH1` instruction accepts a
one byte operand which is converted into a 256bit word by treating the
operands as the low byte.  Hence, `PUSH1 0x1a` pushes the following
word on stack:

```
0x00000000000000000000
0000000000000000000000
000000000000000000001a
```

There are a lot of details about the EVM which I'm going to ignore
here.  But, of relevance, is the fact that all branches (conditional
or unconditional) are _indirect_ where the branch destination is
loaded from the stack.  For example, if we wanted to perform an
unconditional jump to location `0x1a` then we can write this:

```
    PUSH1 0x1a
    JUMP
```

In this case, the jump destination can be determined by looking at the
previous instruction.  But, in general, it could have been loaded onto
the stack at any time in the past.  For example, this is a simple
variation on the above:

```
    PUSH1 0x1a
    PUSH1 0x80
    SLOAD
    SWAP1
    JUMP
```

This first loads `0x1a` onto the stack, then `0x80`.  The `SLOAD`
instruction loads a word from storage at the address given on top of
the stack (in this case `0x80`).  The address is popped by the
operation, and new word loaded from storage pushed on.  Say `0xff` was
currently stored at `0x80`, then after the `SLOAD` we have
`[0x1a,0xff]` on the stack (right-most element is top).  The `SWAP1`
operation swaps the top two stack items, so after this operation we
have `[0xff,0x1a]` on the stack.  Finally, as before, we perform an
unconditional branch to location `0x1a`.

### The Problem

Performing an initial disassembly of the code is actually pretty
straightforward.  We can do a linear scan of the bytecode translating
bytes (and operands where appropriate) into instructions.  This gives
us a first draft which maybe already sufficient.  But this lacks
information which, in some cases, maybe important:

   * **(Data vs Code)**.  As well as executable instructions, EVM
     bytecode can contain data.  This is often used for the contract
     creation process (where the code of the contract being created is
     stored as data), but can be used for other reasons.
     
   * **(Control-Flow)**.  Understanding the possible flow of control
     through the program can be important for some applications. 
     
   * **(Stack Height)**.  Some applications need to know the stack
     height at certain points within the program.

A linear scan of bytecodes does not reveal this information. For
example, it does not tell us which bytes represents actual code to
execute, versus which represent data.  Likewise, it does not tell us
where a given branch might jump to.  Furthermore, whilst a linear scan
can be extended to capture some of this, we really need something more
sophisticated to get reliable information.

## Analysis Overview

A simple and effective solution for disassembling EVM bytecode is to
use a {{<wikip page="Data-flow_analysis">}}data-flow
analysis{{</wikip>}}.  Whilst such analyses can be quite complex, even
a relatively simple dataflow analysis (such as described here) is
enough for many programs.

The basic idea is to simulate execution of the bytecode using a
simplified model of the machine.  In fact, we're just going to model
the machine stack here and ignore memory/storage.  However, more
sophisticated analyses may want to model memory in particular (as this
can reveal useful information).  Our model of the machine stack is
called an _abstract stack_ since it abstracts away information.  In
particular, our abstract stack will contain _concrete values_ (when
possible) and _unknown values_ (otherwise).  Consider our program from
above annotated with the state of the abstract stack before each
instruction:

{{<center>}}
|Bytecode | Abstract Stack |
|:--------|:---------------|
| `PUSH1 0x1a` | `[]` |
| `PUSH1 0x80` | `[0x1a]` |
| `SLOAD`      | `[0x1a,0x80]` |
| `SWAP1`      | `[0x1a,????]` |
| `JUMP`       | `[????,0x1a]` |
{{</center>}}

In the above, `0x1a` and `0x80` are concrete values extracted from the
`PUSH1` operands, whilst `????` represents an unknown value.  Since we
are _simulating_ bytecode execution (i.e. rather than _actually_
executing it), we don't know what value `SLOAD` will push on the
stack.  To capture this safely, we _abstract_ it with an unknown value
that represents any possible value.

The above example illustrates the main idea.  Whilst our abstract
stack contains unknown values, it also contains known values.  By
simulating how instructions manipulate the stack we can determine the
concrete destinations of branching instructions.  For example, in the
above, the analysis tells us that the `JUMP` will branch to `0x1a`.

### Control-Flow Joins

A key question arises as to what happens when different abstract
stacks reach the same position in the code.  Such a position is
typically referred to as a _join point_.  The following illustrates:

{{<center>}}
|Bytecode | Abstract Stack |
|:--------|:---------------|
| `PUSH1 0x8e` | `[]` |
| `PUSH1 0x1f` | `[0x8e]` |
| `PUSH1 0x80` | `[0x8e,0x1f]` |
| `SLOAD`      | `[0x8e,0x1f,0x80]` |
| `PUSH1 0x1a` | `[0x8e,0x1f,????]` |
| `JUMPI`      | `[0x8e,0x1f,????,0x1a]` |
| `POP` | `[0x8e,0x1f]` |
| `PUSH1 0x2f` | `[0x8e]` |
| `PUSH1 0x1a` | `[0x8e,0x2f]` |
| `JUMP`       | `[0x8e,0x2f,0x1a]` |
{{</center>}}

This code branches to position `0x1a` from two locations and, in each
case, a different abstract stack is passed along (`[0x8e,0x1f]` versus
`[0x8e,0x2f]`).  The question then: _is what abstract stack should we use
for location `0x1a`?_ There are different ways we can solve this problem, but the simplest is just to _merge_ the two stacks together in such a way that retains as much information as possible. 

When merging two abstract stacks which have the same value at the same
position, then the resulting stack has that value as well.  Otherwise,
we simply use `????` at that position (since we have no way to
represent more than one concrete value in our model).  Thus, for
example, merging`[0x8e,0x1f]` with `[0x8e,0x2f]` yields the stack
`[0x8e,????]` which safely approximates the two possible stacks at
position `0x1a`.

### Generalising the Abstraction

A further limitation with our abstract stack model is that it assumes
a _known_ height.  To understand this, consider this variation on our
program from before:

{{<center>}}
|Bytecode | Abstract Stack |
|:--------|:---------------|
| `PUSH1 0x8e` | `[]` |
| `PUSH1 0x1f` | `[0x8e]` |
| `PUSH1 0x80` | `[0x8e,0x1f]` |
| `SLOAD`      | `[0x8e,0x1f,0x80]` |
| `PUSH1 0x1a` | `[0x8e,0x1f,????]` |
| `JUMPI`      | `[0x8e,0x1f,????,0x1a]` |
| `SWAP1` | `[0x8e,0x1f]` |
| `POP` | `[0x1f,0x8e]` |
| `PUSH1 0x1a` | `[0x1f]` |
| `JUMP`       | `[0x1f,0x1a]` |
{{</center>}}

Again, this program branches to `0x1a` from two locations each of
which passes along a different stack (`[0x8e,0x1f]` versus `[0x1f]`).
The question this time: _is how do we merge stacks with different
heights?_

With our current abstract stack model we cannot actually represent the
stack going into location `0x1a`.  We need to _generalise_ it further
such that it supports stacks of different heights.  A simple way to do
this is with an [integer range](/publications/pea15_seus/) `l..r`
(where `l <= r` and the range is inclusive).  For example, the range
`0..1` represents the set `{0,1}`, whilst `0..0` represents the
constant `0`.

We then adjust our model of the stack to include its _length_ as an
integer range.  For example, the stack `[0x1a,0x80]` in our previous
model becomes `[0x1a,0x80]:2..2` (where `2..2` signals a known stack
height of `2`).  This allows us to represent the merge of
`[0x8e,0x1f]` with `[0x1f]` as `[0x1f]:1..2`.  Essentially, this
describes any stack whose top value is `0x1f` and height is either `1`
or `2`.  Whilst there are quite a few details needed to make this
work, hopefully it gives the general idea.

## Conclusion

This article has given a basic introduction to the idea of using a
dataflow analysis for reasoning about jump destination.  However, to
make a serious analysis which can handle bytecode found in the wild
needs much more work.  A good starting point for understanding this
are the following papers:

   1. **Elipmoc: advanced decompilation of Ethereum smart
      contracts**. In _Proc
      OOPSLA_, 2022. [PDF](https://yanniss.github.io/elipmoc-oopsla22.pdf)
   2. **Gigahorse: Thorough, Declarative Decompilation of Smart
      Contracts**.  In _Proc
      ICSE_, 2019. [PDF](https://yanniss.github.io/gigahorse-icse19.pdf)
      
Enjoy!
