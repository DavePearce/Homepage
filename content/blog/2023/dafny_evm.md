---
date: 2023-01-26
title: "Formalising the Ethereum Virtual Machine in Dafny"
draft: true
# metaimg: "images/2022/TokenContract_Preview.png"
# metatxt: "Verifying a token contract in Whiley helps to find problems."
#twitter: ""
# reddit: "https://www.reddit.com/r/rust/comments/uigljf/puzzling_strong_updates_in_rust/"
---

Since starting at [ConsenSys](https://consensys.net/), the main project I have been involved with is a [formalisation of the Ethereum Virtual Machine in Dafny](https://github.com/ConsenSys/evm-dafny) called the "DafnyEVM".  Our goals share some similarity with Runtime Verification's [KEVM](https://github.com/runtimeverification/evm-semantics) project.  We want a formal semantics of the EVM which is executable so that, for example, we can test it against the [Ethereum common tests](https://github.com/ethereum/tests).  Using Dafny, however, offers some advantages over the [K framework](https://kframework.org/):

   * **(Verification)** With Dafny, we can verify security properties
     over EVM bytecode directly.  For example, we can verify simple
     properties (e.g. no arithmetic overflow/underflow) as well as
     more complex properties (e.g. that an invariant between fields of
     the contract always holds).

   * **(Documentation)** Our formalisation in Dafny gives a relatively
     concise and readable description of the Ethereum Virtual Machine.
     In my opinion, it compares well against the [Yellow
     Paper](https://ethereum.github.io/yellowpaper/paper.pdf) and the
     official [Python
     spec](https://github.com/ethereum/execution-specs) (with the
     caveat that the DafnyEVM remains a work-in-progress).
     
   * **(Compilation)** In principle, we could develop a compiler for a
     high-level language using the DafnyEVM which guarantees that
     generated code is correct.  A good example of this is the
     {{<wikip page="CompCert">}}CompCert{{</wikip>}} compiler
     developed for the C language.

Whilst the DafnyEVM remains a work-in-progress, it can already pass a
large number of the [Ethereum common
tests](https://github.com/ethereum/tests).  So, let's take a look
inside ...

## Machine State

An executing EVM contains various key components of the executing
state such as _gas_, _pc_, _stack_, _code_, _memory_, and
_worldstate_.  Roughly speaking, we implement this in Dafny like so:

```dafny
  datatype ExecutingEvm = EVM(
    gas: nat, 
    pc: nat, 
    stack: Stack, 
    code: Code,
    mem: Memory, 
    world: WorldState, 
    ...
  )
```

On top of this, we have a notion of the _machine state_ which maybe an
executing EVM (as above), or a terminated EVM (e.g. having executed a
`RETURN` or `REVERT` instruction, or failed with some kind of error):

```dafny
  datatype State = OK(evm: ExecutingEvm) 
      | REVERTS(gas:nat, data:seq<u8>)
      | RETURNS(gas:nat, data:seq<u8>, ...) 
      | INVALID(Error) 
      | ...
```

Here, the `REVERTS` and `RETURNS` states include their `RETURNDATA` as
a sequence of bytes (i.e. `u8`) along with any `gas` returned to the
caller.

## Bytecode Semantics

There are (approx) 140 bytecode instructions supported in the Ethereum
Virtual Machine.  These include simple arithmetic operations, memory
reading/writing, storage reading/writing, contract calls, etc.  The
{{<wikip page="Semantics_(computer_science)">}}semantics{{</wikip>}}
(i.e. meaning) of every instruction is formalised in the DafnyEVM.

As a first example, here is our formalisation of the `ADD` instruction
(opcode `0x01`):

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

This states that there must be at least two operands on the stack,
otherwise execution stops with a stack underflow.  Furthermore, the
`lhs` (i.e. left-hand side) is at offset `0` from the top of the
stack, whilst the `rhs` is at offset `1`.  The two operands are then
added together modulo `TWO_256` to ensure the result fits into a
`u256`.  Thus, we can see from this that the EVM does not
automatically catch arithmetic overflow for us.

As a second example, consider the semantics given for the `MLOAD`
instruction (i.e. opcode `0x51`):

```dafny
function method MLoad(st: State): (st': State)
requires st.IsExecuting() {
   if st.Operands() >= 1
   then
      var loc := st.Peek(0) as nat;
      // Expand memory as necessary
      var nst := st.Expand(loc,32);
      // Read from expanded state
      nst.Pop().Push(nst.Read(loc)).Next()
   else
      State.INVALID(STACK_UNDERFLOW)
}
```

## Verification Example

## Conclusion


