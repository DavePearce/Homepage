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

## References

   * **KEVM: A Complete Semantics of the Ethereum Virtual Machine**,
     Everett Hildenbrandt *et al.*. In *Proc CSF*, 2018. [(PDF)](http://t-news.cn/Floc2018/FLoC2018-pages/proceedings_paper_513.pdf)
     
   * **Defining the Ethereum Virtual Machine for Interactive Theorem
     Provers**, Y. Hirai.  In *Proc WTSC*, 2017. [(PDF)](https://yoichihirai.com/malta-paper.pdf)
     
     
