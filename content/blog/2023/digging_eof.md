---
date: 2023-10-16
title: "Digging into the EVM Object Format (EOF)"
draft: true
#metaimg: "images/2023/DafnyByMethod_Preview.png"
#metatxt: "An unusual feature of Dafny is that functions can be implemented _by_ methods."
#twitter: "https://twitter.com/whileydave/status/1673926723568832513"
#reddit: ""
---

The EVM Object Format (EOF) introduces a structured container format
for EVM bytecode.  The EOF proposal is spread over several EIPs (see
the ["Mega EOF
Endgame"](https://notes.ethereum.org/@ipsilon/mega-eof-specification)
for an overview of them all).  My goal here is to provide a high level
overview and, in particular, to clarify what problems it is trying to
solve.

The EOF proposal was recently presented at the [All Core Devs
Execution Layer meeting](https://www.youtube.com/watch?v=t25IIQWfCnY)
where its reception was somewhat luke warm (see the slides
[here](https://docs.google.com/presentation/d/10mxZK5hzLeaTLGAVo83qrPybk0Y8fUN5prAffGhKV6o/edit#slide=id.p)).
The following comments made during the meeting
captures the sentiment:

> **(1:18:55)** “I know the Ipsilon team and Danno have spent a lot of
> time working on this and it’s quite harsh to say that after all this
> time we might not be shipping it but I think it’s even worse to say,
> ‘Let’s see,’ and then in like we push it another two years and then
> we say, ‘Oh, we are not going to ship it after all.’ and so I think
> we should make a decision at Devconnect whether this is something
> that we want”

So, _what is the EOF and why is it important?_  That's what I want to
dig into here.  My summary would be:

> (**TL;DR**) The EOF container format offers a mechanism for managing
> _breaking changes_ to the EVM.

A big issue with the current proposal appears to be its size and
complexity.  I don't disagree with that but, keeping the above in
mind, we can at least understand what the proposal is trying to
achieve and why it is important.

## Overview

The Ethereum Virtual Machine (EVM) was specified as part of the
[Yellow Paper](https://ethereum.github.io/yellowpaper/paper.pdf) and,
more recently, through the [Execution Layer
Specification](https://github.com/ethereum/execution-specs).  The EVM
has evolved in many directions.  For example, new instructions have
been added (e.g. [`SHL`](https://eips.ethereum.org/EIPS/eip-145),
[`CREATE2`](https://eips.ethereum.org/EIPS/eip-1014),[`PUSH0`](https://eips.ethereum.org/EIPS/eip-3855),
etc), gas costs [have](https://eips.ethereum.org/EIPS/eip-2929)
[been](https://eips.ethereum.org/EIPS/eip-1108)
[tweaked](https://eips.ethereum.org/EIPS/eip-2565), a code size limit
has been [imposed](https://eips.ethereum.org/EIPS/eip-170),
instructions have been
[supplanted](https://eips.ethereum.org/EIPS/eip-4399) and even
(hopefully) [deprecated](https://eips.ethereum.org/EIPS/eip-4758).
So, allowing the EVM to evolve seems important for the future of
Ethereum! And yet, the nature of EVM bytecode makes this unnecessarily
difficult.

I'm going to give some examples to backup this claim that the current
nature of the EVM hinders its evolution.  These examples are not mine:
_they are actually part of the EOF proposal_.  I'm calling them
examples because that's what I think they are: _good examples where
evolving the EVM is currently difficult_.  I'm not specifically
advocating for any of them (that's for others to decide), but I am
arguing that they indicate a problem with the status quo.  There are
many other examples as well.  The challenges faced with deprecating
`SELFDESTRUCT` provide another
[example](https://ethereum-magicians.org/t/eip-4758-deactivate-selfdestruct/8710)
[here](https://ethereum-magicians.org/t/almost-self-destructing-selfdestruct-deactivate/11886).

### Example: Immediate Operands

Currently, _instructions with immediate operands cannot easily be
added to the EVM_.  Whilst the exact reasons for this are somewhat
involved (see Appendix below), the more important question is: _why do
we want instructions with immediate operands?_  Whilst this is hard to
pin down, two specific cases are illustrative here:

   * **(Static Control Flow)**.  EVM bytecode currently only supports
     _dynamic_ control flow.  Specifically, the `JUMP` and `JUMPI`
     instructions accept their branch targets as stack operands.
     Whilst determining a branch target is often easy (e.g. because it
     is loaded on the stack immediately beforehand using a `PUSH`) it
     can also be impossibly hard (e.g. because it was loaded out of
     storage using `SLOAD`).  As such, decompiling EVM bytecode is a
     [hard](https://dl.acm.org/doi/10.1145/3527321)
     [problem](https://doi.org/10.1109/SANER56733.2023.00011).  This
     complicates, for example, tools for operating on bytecode
     (e.g. for static verification) and even clients wishing to
     efficiently compile bytecode into machine code (where static
     branching is used).  The idea of introducing static control-flow
     to the EVM has been raised many times over the years (see
     e.g. [EIP-615](https://eips.ethereum.org/EIPS/eip-615),
     [EIP-2315](https://eips.ethereum.org/EIPS/eip-2315),
     [EIP-3779](https://eips.ethereum.org/EIPS/eip-3779),
     [EIP-4200](https://eips.ethereum.org/EIPS/eip-4200), etc).  _But,
     none of these proposals can work unless immediate operands are
     permitted_.
     
   * **(Stack Manipulation)**.  A similar issue lies with the stack,
     as instructions for manipulating the stack (e.g. `SWAP1`,
     `SWAP13`, `DUP16`, etc) can only reach sixteen items deep.  To
     support arbitrary numbers of parameters and local variables,
     compilers use complex maneuvers to shift items around, sometimes
     spilling them to memory.  This results in a discrepancy between
     the true cost of an operation, and its actual cost in gas
     (i.e. because some artificial maneuver was required which, on a
     physical CPU, could have been performed directly).  In 2017,
     [EIP-663](https://eips.ethereum.org/EIPS/eip-663) was created to
     address this issue by introducing the `DUPN` and `SWAPN`
     instructions.  The problem is that, without immediate operands,
     the `N` must be supplied as a stack operand.  But that, in turn,
     would mean losing the ability to statically analyse the stack.
     In some sense, it would be introducing for the stack the problem
     currently faced for control-flow.  _As such, EIP-663 has
     languished until finally being folded into the EOF proposal_.

This example is perhaps the most well-developed part of the EOF
proposal, and covers several of the proposed EIPs:
[EIP-663](https://eips.ethereum.org/EIPS/eip-663),
[EIP-4200](https://eips.ethereum.org/EIPS/eip-4200),
[EIP-4750](https://eips.ethereum.org/EIPS/eip-4750),
[EIP-5450](https://eips.ethereum.org/EIPS/eip-5450), and
[EIP-6206](https://eips.ethereum.org/EIPS/eip-6206).

### Example: Gas Costs

On several occasions the gas cost of an existing operation has been
tweaked for some reason.  Some examples include:

  * [EIP-2929](https://eips.ethereum.org/EIPS/eip-2929).
  * [EIP-1108](https://eips.ethereum.org/EIPS/eip-1108).
  * [EIP-2565](https://eips.ethereum.org/EIPS/eip-2565).

  * Want to be able to change gas schedule whilst minimising impact on
    existing contracts.
  * Hence, instructions which explicitly refer to gas (e.g. `GAS`,
    `CALL`) need to be removed.

### Example: Code Inspection

  * Ambitious
  * Want to be able to programmatically upgrade existing contracts
    (e.g. to replace all occurences of `PUSH1 0x0` with `PUSH0`).
  * Need to remove instructions which can "see" code.  
  * Most instructions which can "see" code do so because they see it
    as data (e.g. initcode to be deployed using `CREATE`).

## Evolution

Thus, we can start to see the EOF proposal for what it is: a [core
proposal](https://eips.ethereum.org/EIPS/eip-3540) (EOF container
format) along with (for various reasons) a bunch of examples baked
[in](https://eips.ethereum.org/EIPS/eip-3670)
[as](https://eips.ethereum.org/EIPS/eip-4200)
[additional](https://eips.ethereum.org/EIPS/eip-4750)
[proposals](https://eips.ethereum.org/EIPS/eip-5450).  

* `0xEF` and scan.  Separate data from code.
* Incentivisation

## Conclusion

In my opinion, the real benefit of EOF comes from versioning: we can
 have multiple bytecode versions "in flight" at the same time.  There
 are concerns this could lead to a glut of different versions on
 chain.  But, the fact is, we are already living in this world.
 Whenever a change is made which could break existing code
 (e.g. changing gas costs, deprecating instructions, imposing new
 limits, etc), we effectively create a new version of bytecode.  The
 only difference is how we manage it: either in a completely ad-hoc
 way (as is done now); or, in a more structured and identifiable
 fashion (as with EOF).



   * Perhaps the proposal could be broken down into smaller
     independent pieces (though this was tried already).

   * EOF v0.

   * What about existing legacy?
   
## Appendix --- Immediate Operands

As discussed above, we cannot easily add instructions with immediate
operands to the EVM.  The purpose here is just to clarify what the
problem is.  The relevant points for our discussion are:

  1. **Bytecode is Unstructured**.  An EVM bytecode program is just a
     collection of bytes with (almost) no other structured imposed.
     Execution begins with the first instruction at offset `0`.  Some
     bytes represent instructions, some bytes can represent data and
     some bytes can simply be "dead code".  Determining whether or not
     a given byte is part of an instruction, is unused or represents
     data is not at all straightforward. The solidity compiler, for
     example, [appends metadata to the end of a contract's
     bytecode](https://docs.soliditylang.org/en/latest/metadata.html).
     
  2. **Mostly No Immediates**.  Unlike many other bytecode formats
     (e.g. [JVM
     Bytecode](https://docs.oracle.com/javase/specs/jvms/se7/html/)),
     EVM instructions generally do not take immediate operands and,
     instead, operands are supplied on the stack.  The only exception
     here are the `PUSHXX` instructions with which one can put
     constants on the stack.
     
  3. **Jump Destinations**.  Every branch in an EVM bytecode program
     must land at a `JUMPDEST` instruction (opcode `0x5b`), otherwise
     a runtime exception is raised.  Furthermore, branches must land
     on instruction boundaries (i.e. _not on bytes within the
     immediate operand of another instruction_).

Let's consider a simple (but currently valid) sequence of EVM
bytecode: `0x600456e05b00`.  This corresponds to the following
assembly:

```
   push1 lab
   jump
   db 0xe0
lab:
   jumpdest
   stop
```

Observe that the data byte `0xe0` is mixed in with other instructions,
which is permitted under the current EVM specification.  The EVM
simply [views this as an `INVALID`
instruction](https://www.evm.codes/playground?fork=shanghai&unit=Wei&codeType=Bytecode&code='600456e05b00'_)
_which has no operands_.  Specifically, this is defined in Section
9.4.3 ("Jump Destination Validity") of the [Yellow
Paper](https://ethereum.github.io/yellowpaper/paper.pdf):

{{<img class="text-center" src="/images/2023/DiggingEof_yp943.png" width="454px" alt="Illustrating Section 9.4.3 of the Yellow Paper which clarifies jump destination validity.">}}

Now, let us consider EIP-4200 which adds a new instruction `RJUMP`
which takes a two-byte immediate operand.  Funnily enough, the opcode
of `RJUMP` is `0xe0`.  _So, what's the problem?_ Well, our bytecode
sequence now looks like this:

```
   push 0x4
   jump
   rjump 0x5b00
```

Observe how the `JUMPDEST` and `STOP` bytecodes are now bytes within
the immediate operand of the `RJUMP` instruction!  Furthermore, its
not clear how to resolve this.  We could try to make an exception for
`RJUMP` and allow legacy branches into its immediate opcode bytes
(i.e. so the above retains its original meaning).  But, this makes
disassembling bytecode contracts much harder!  Furthermore, we can now
write bytecode programs which do this on purpose.  At that point, a
given byte can be interpreted as a different instruction depending on
the `PC` of the executing EVM.  _That is not ideal!_

A final point worth noting is that we can still introduce instructions
such as `RJUMP` _when we know they don't actually break anything_.
Essentially, we can analyse mainnet to check whether any bad
situations (such as above) actually exist.  If not, then we're good to
go!  Or, if they do exist, we can repeat the analysis for a different
opcode value (e.g. `0xe1` in this case).  This approach works and was,
for example, [used in determining the `0xEF` marker
opcode](https://eips.ethereum.org/EIPS/eip-3541) used to indicate an
EOF contract.  However, as more contracts are deployed and as more
instructions added to the EVM, it will get harder and harder to
successfully apply this technique.
