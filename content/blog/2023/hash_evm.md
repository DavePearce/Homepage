---
date: 2023-04-20
title: "Introducing the HashEVM!"
draft: true
#metaimg: "images/2023/Rust_Neverland_Preview.png"
#metatxt: "Pattern matching with the never type offers exciting possibilities!"
#twitter: ""
# reddit: "https://www.reddit.com/r/rust/comments/uigljf/puzzling_strong_updates_in_rust/"
---
Recently, I have been learning more about [Zero-Knowledge Proofs
(ZKP)](https://en.wikipedia.org/wiki/Zero-knowledge_proof) and, in
particular, the [so-called
zkEVM](https://decrypt.co/resources/what-is-zkevm).  This shouldn't be
too surprising since my organisation has developed [their own
zkEVM](https://consensys.net/zkevm/).  This post isn't specifically
about that, but I think it can help us understand what a zkEVM
actually does.  At least, its helping me!

**TL;DR**.  A zkEVM encodes execution traces of the Ethereum Virtual
Machine (EVM) into [arithmetic
circuits](https://crypto.stackexchange.com/questions/92018/which-is-the-relation-between-zero-knowledge-proofs-of-knowledge-and-circuits)
over a [prime field](https://en.wikipedia.org/wiki/Finite_field).
This allows a short "proof" to be constructed that a given transaction
occurred on Layer 2.  This proof is then stored on Layer 1.  Since
this is a reasonably difficult process to understand, I'm going to
describe the "HashEVM" which is simpler but, in many ways, is quite
comparable.  Finally, we can look at some of the pros / cons of the
HashEVM compared with a zkEVM.

## Overview

Instead of using ZKP, the HashEVM generates a hash to prove a given
transaction occurred.  This hash corresponds to a specific execution
of the EVM on a given contract's bytecode starting from a given state
of the blockchain.  Since the hash is short in length (e.g. 256bits or
512bits), it can be stored compactly at Layer 1.  The data from which
the hash is generated must include the state of storage when execution
began (which is easy enough, since we can reuse the Merkle tree root)
along with details of the intermediate states arising during
execution.  For this to work, it has to be essentially impossible for
another execution of the EVM from a potentially different blockchain
state to generate the same hash (as, otherwise, fraudulent
transactions could arise).

Let's imagine we wanted to execute the contract bytecode
`0x600160020100` (i.e. `PUSH 1; PUSH 2; ADD; STOP`) for a given
blockchain state.  Roughly speaking, it looks something like this:

{{<img class="text-center" src="/images/2023/HashEVM_eg1.png" width="454px" alt="Illustrating the four bytecodes being executing with intermediate hashes being generated.">}}

Here, the first hash is determined from the storage root along with
other identifying information (e.g. contract address being executed,
message sender, calldata, value transferred, etc).  Then, after each
instruction is executed, the hash is updated to include information
about the current state of the machine at that position.

## Hashing States

   * Intermediate states are the witness.  You cannot derive the
     inputs from the witness.  But, likewise, you cannot derive the
     witness without the inputs!  Furthermore, the witness dictates
     exactly which instructions were executed.  Presumably, given an
     input hash, we need then to be able to compute the output hash to
     verify it?
   * There are _public inputs_ and _private inputs_.  Its fine to have
     the public inputs.  For example, we know the EVM starts
     computation with an empty stack and memory.  However, we need
     presumably to verify that a particular _individual_ sent the
     transaction.  Otherwise, anyone could make up a transaction and
     send it!  The private key (which must remain hidden) is the key
     piece of information we are missing.

 (which correspond to the intermediate variables a zkEVM
generates as the "witness")

## Wiggle Room

Can two instruction executions give the same update of the hash?  If
we don't include the stack length, then it could overflow with memory.
What about storage?  Do we need to encode memory as well?
