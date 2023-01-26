---
date: 2023-01-26
title: "Formalising the Ethereum Virtual Machine in Dafny"
draft: true
# metaimg: "images/2022/TokenContract_Preview.png"
# metatxt: "Verifying a token contract in Whiley helps to find problems."
#twitter: ""
# reddit: "https://www.reddit.com/r/rust/comments/uigljf/puzzling_strong_updates_in_rust/"
---

Since starting at [ConsenSys](https://consensys.net/), the main project I have been involved with is a [formalisation of the Ethereum Virtual Machine in Dafny](https://github.com/ConsenSys/evm-dafny).  Our goals share some similarity with Runtime Verification's [KEVM](https://github.com/runtimeverification/evm-semantics) project.  We want a formal semantics of the EVM which is also executable meaning, for example, we can test it against the [Ethereum common tests](https://github.com/ethereum/tests).  Using Dafny, however, offers some advantages over the [K framework](https://kframework.org/):

   * **(Verification)** With Dafny, we can verify security properties over EVM bytecode directly (see more below).  For example, we can verify simple properties (e.g. no arithmetic overflow/underflow) as well as more complex properties (e.g. that an  invariant between fields of the contract always holds).
   * **(Documentation)** Our formalisation in Dafny gives a relatively concise and readable description of the Ethereum Virtual Machine.  In my opinion, it compares well against the [Yellow Paper](https://ethereum.github.io/yellowpaper/paper.pdf) and the official [Python spec](https://github.com/ethereum/execution-specs).
   * **(Certified Compilation)** 

## Bytecode Semantics

```dafny
function method Add(st: State): (st': State)
requires st.IsExecuting() {
    if st.Operands() >= 2
    then
        var lhs := st.Peek(0) as int;
        var rhs := st.Peek(1) as int;
        var res := (lhs + rhs) % TWO_256;
        st.Pop().Pop().Push(res as u256).Next()
    else
        State.INVALID(STACK_UNDERFLOW)
}
```

## Verification Example

## Conclusion


