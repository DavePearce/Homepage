---
date: 2023-04-16
title: "Introducing the HashEVM!"
draft: true
#metaimg: "images/2023/Rust_Neverland_Preview.png"
#metatxt: "Pattern matching with the never type offers exciting possibilities!"
#twitter: ""
# reddit: "https://www.reddit.com/r/rust/comments/uigljf/puzzling_strong_updates_in_rust/"
---
Recently, I have been learning more about [Zero-Knowledge
Proofs](https://en.wikipedia.org/wiki/Zero-knowledge_proof) and, in
particular, the [so-called
zkEVM](https://decrypt.co/resources/what-is-zkevm).  This shouldn't be
too surprising since my organisation has developed [their own
zkEVM](https://consensys.net/zkevm/).  This post isn't specifically
about that, but I think it can help us understand what a zkEVM
actually does.  At least, its helping me!

**TL;DR**.  A zkEVM encodes EVM traces into [arithmetic
circuits](https://crypto.stackexchange.com/questions/92018/which-is-the-relation-between-zero-knowledge-proofs-of-knowledge-and-circuits)
over prime fields, allowing it provide a short "proof" that a
transaction occurred on layer 2.  This is a reasonably difficult
process to understand.  Instead, the HashEVM produces a proof using a
hashing technique which is easier to understand and, in many ways,
quite comparable.

## Overview



