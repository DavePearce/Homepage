---
date: 2023-04-16
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

{{<img class="text-center" src="/images/2023/HashEVM_eg1.png" width="485px" alt="???">}}


## Hashing States

 (which correspond to the intermediate variables a zkEVM
generates as the "witness")

## Wiggle Room

Can two instruction executions give the same update of the hash?
