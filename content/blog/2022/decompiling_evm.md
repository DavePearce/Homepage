---
date: 2022-12-18
title: "Disassembling EVM Bytecode (the Basics)"
draft: true
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

# Background

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
0x000000000000000000000000000000000000000000000000000000000000001a
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
    MLOAD
    SWAP1
    JUMP
```

This first loads `0x1a` onto the stack, then `0x80`.  The `MLOAD`
instruction loads a word from memory at the address given on top of
the stack (in this case `0x80`).  The address is popped by the
operation, and new word loaded from memory pushed on.  Say `0xff` was
currently stored at `0x80`, then after the `MLOAD` we have
`[0x1a,0xff]` on the stack (right-most element is top).  The `SWAP1`
operation swaps the top two stack items, so after this operation we
have `[0xff,0x1a]` on the stack.  Finally, as before, we perform an
unconditional branch to location `0x1a`.

# The Problem

Performing an initial disassembly of the code is actually pretty
straightforward.  We can do a linear scan of the bytecode translating
bytes (and operands where appropriate) into instructions.  This gives
us a first draft which maybe already sufficient.  But, it lacks some
key information which, in some cases, maybe needed:

   * **(Data vs Code)**.  As is typical for binary programs, EVM
     bytecode can contain data portions.  These are often used during
     the contract creation process, but can be used for many other
     reasons.
   * **(Control-Flow)**.  Understanding the possible flow of control
     through the program can be important for some applications. 
   * **(Stack Height)**.  Some applications need to know the stack
     height at certain points within the program.

A linear scan of the bytecodes does not reveal, for example, which
bytes represents actual code to execute, versus which represent data.
Likewise, it does not tell us where a given branch might go to.
Furthermore, whilst a linear scan can be extended to do some of this,
we really need something more sophisticated to get reliable information.

## Notes

For every branch instruction, the location that is branched to must
store the instruction `JUMPDEST` to signal t



