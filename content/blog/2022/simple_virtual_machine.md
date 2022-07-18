---
date: 2022-06-28
title: "Formalising a Simple Virtual Machine"
draft: false
metaimg: "images/2022/SimpleVirtualMachine_Preview.png"
metatxt: "Formalising a simple virtual machine in Whiley."
# twitter: ""
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

In this post, I'm going to formalise a *[Simple Virtual Machine (SVM)](https://github.com/DavePearce/SimpleVirtualMachine.wy/)*
which has only a few instructions.  The goal is just to illustrate the
technique and to show that, using the formalisation, we can prove
interesting properties (e.g. that certain optimisations are safe).

The machine state includes a _stack_ of `u16` words and a _memory_
store of `u16` words.  In addition, we have a _program counter_ (`pc`)
and a _stack pointer_ (`sp`), along with a sequence of bytecodes.  To
illustrate, here are descriptions for a few example bytecodes:

{{<center>}}
|Bytecode | Description  | Stack | Memory | PC |
|:-------:|:-------------|:--|:-------|:-------|
| `NOP`   | _No-operation_ ||| `PC += 1` |
| `PUSH w`  | _Push (immediate) word_ | `.. ⟹   w ..` || `PC += 2` |
| `POP`   | _Pop word from stack_ | `.. w ⟹   ..` || `PC += 1` |
| `STORE k`| _Store word in memory_ | `.. w ⟹   ..` | `data[k] = w` | `PC += 2` |
| `LOAD k` | _Load word from memory_ | `.. ⟹   w ..` | `w = data[k]` | `PC += 2` |
| `ADD`   | _Add words_ | `.. v w  ⟹   u ..` || `PC += 1` |
||| **where** `u = (v+w) % 0x10000`{{<br>}} |||
| `SUB`   | _Subtract words_ | `.. v w  ⟹   u ..` || `PC += 1` |
||| **where** `u = (v-w) % 0x10000`{{<br>}} |||
| `MUL`   | _Multiply words_ | `.. v w  ⟹   u ..` || `PC += 1` |
||| **where** `u = (v*w) % 0x10000`{{<br>}} |||
| `DIV`   | _Divide words_ | `.. v w  ⟹   u ..` **if** `w != 0` || `PC += 1` |
||| **where** `u = v/w` |||
| `JMP k`   | _Unconditional Branch_ ||| `pc += 2+k` |
| `JZ k`   | _Conditional Branch_ | `v ⟹   ..` || `pc += 2` **if** `v!=0`|
||||| `pc += 2+k` **if** `v==0`|
{{</center>}}

This provides a fairly typical description for a bytecode machine
(e.g. similar in style to that for the
[JVM](https://docs.oracle.com/javase/specs/jvms/se11/html/jvms-6.html#jvms-6.5)).
Each bytecode typically has an effect on the stack, and may also
effect the memory.  The effect on the stack is described as a rewrite
popping zero or more elements on the stack, and pushing zero or more
elements on the stack.  For example, the effect `.. w ⟹ ..` for `POP`
states that, for the bytecode to execute, there must be at least one
word `w` on the stack and, afterwards, that word is removed (but
everything else remains the same).  Likewise, the memory effect for
`STORE` is that the word `w` is written to address `k` in the `data`
memory.  Finally, for completeness, note that (like most programming
languages) integer division is {{<wikip
page="Euclidean_division">}}non-Euclidean{{</wikip>}} (i.e. rounds
_towards_ zero).

Whilst the above description is fairly clear and easy to follow, there
are still some questions.  For example, what happens if an address `k`
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
type SVM is {
   u16 pc,
   u16 sp,
   u16[] data,
   u16[] stack,
   u8[] code
}
where sp <= |stack| && |stack| < 0xFFFF 
where |code| <= 0xFF00
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
property isHalted(SVM st) -> (bool r):
  return st.pc >= |st.code|
    
property exitCode(SVM st) -> (u8 r)
requires isHalted(st):
  return |st.code| - st.pc
```

Basically, whenever the `pc` is passed the end of the `code` size,
then we consider the machine to be "halted".  Once it reaches this
state, then execution is finished.  Furthermore, we'll exploit the
final `pc` value to determine an exit code, such that `exitCode(vm) ==
0` indicates execution completed normally (more on this later).

### Bytecodes

We now need to formalise the *semantics* of each bytecode.  That is,
specify how each bytecode should execute.  A simple example is the following:

```whiley
property evalPOP(SVM st) -> (SVM nst):
  if st.sp < 1:
    return halt(st, STACK_OVERFLOW)
  else:
    return pop(st)
```

This specifies that evaluating a `POP` bytecode requires at least one
element on the stack, otherwise the machine halts (with exit code
`STACK_OVERFLOW`).  If there is one element, then its popped off.  This
uses two helpers `halt` and `pop` defined as follows:

```whiley
property pop(SVM st) -> SVM
requires st.sp > 0:
   return st{sp:=st.sp-1}

property halt(SVM st) -> SVM:
   return st{pc:=|st.code|}
```

Whilst these could have been written inline, I find it helpful to give
them more descriptive names.  I typically refer to these low-level
building blocks as _microcodes_ and, as for a physical machine, we'll
see these are reused a lot in defining the semantics of our bytecodes.
Observe the precondition for `pop()` is that the stack cannot be
empty.  Also, `st{sp:=st.sp-1}` returns `st` with field `sp` updated
to `sp-1` and all others unchanged.

Another example to illustrate is the following:

```whiley
property evalADD(SVM st) -> (SVM nst):
  if st.sp < 2:
    return halt(st, STACK_UNDERFLOW)
  else:
    // Read operands
    u16 r = peek(st,1)
    u16 l = peek(st,2)
    u16 v = (l + r) % 0x10000
    // done
    return push(pop(pop(st)),v)
```

Here, the right-hand side `r` is taken from the first (i.e. topmost)
stack item, whilst the left-hand side `l` is from the second stack
item.  Furthermore, the addition itself must be modulo `0x10000`
(i.e. `65536`) in order that the value assigned to `v` remains within
bounds.  Indeed if the modulo operation was left out, then the
verifier would highlight a potential integer overflow (and this is
where tools like Whiley shine).  Again, our definition above uses some
more microcodes:

```whiley
property push(SVM st, u16 k) -> SVM
requires st.sp < |st.stack|:
  SVM nst = st{stack:=st.stack[st.sp:=k]}
  return nst{sp:=st.sp+1}

property peek(SVM st, int n) -> u16
requires 0 < n && n <= st.sp:
  return st.stack[st.sp - n]
```

This time, the precondition for `push()` requires that _the stack cannot be full_.  Likewise, for `peek()`, we must have enough items on the stack to cover the one we're after.

Finally, whilst I've only illustrated the semantics for a few example
bytecodes above you can see them all in [the repo](https://github.com/DavePearce/SimpleVirtualMachine.wy/blob/main/src/svm.whiley).

### Execution

The next piece of the jigsaw is to bring the semantics of all the
bytecodes together into one method which is responsible for executing
the next bytecode.  For this, we have `eval()`:

```whiley
// Execute a "single step" of the machine.
property eval(SVM st) -> (SVM res)
requires !isHalted(st):
  u8 opcode = st.code[st.pc]
  // increment pc
  SVM nst = st{pc:=st.pc+1}
  // Decode opcode
  if opcode == NOP:
    return evalNOP(nst)
  ...
  else if opcode == POP:
    return evalPOP(nst)
  ...
  else:
    // Force machine to halt
    return halt(nst)
```

There are a few things to note about this: **Firstly**, it is rather
ugly having this large sequence of `if` / `else if` statements.  It
would be better if we could at least use a `switch` statement.
Unfortunately, at the moment, Whiley does not support this (though in
the future it should).  **Secondly**, the machine is set to `halt()`
whenever an unknown instruction is encountered.  **Finally**, a
precondition for `eval()` is that the machine as not already halted.
Whilst there are different ways we could have done this, I've just
chosen something simple here.

## Something Useful!

Right, having now specified our simple virtual machine the question
is: *what can we do with it?*  Well, the first and most obvious thing
would be to generate code from it to give us a reference
implementation.  But, there are some other things we can do as well.
For example, we can prove *safety properties* of bytecode sequences
(e.g. that they don't unexpectedly halt), or that certain *compiler
optimisations* are sound, etc. 

### Example 1

As a first example to illustrate, consider the following:

```whiley
method test_add_01():
  SVM m1 = execute(
     [LDC,2,LDC,1,ADD], // code
     [], // data
     1024) // stack size
  // Check expected output.
  assert exitCode(m1) == OK
  assert peek(m1,1) == 3
```

This method is statically verified by Whiley.  However its pretty
simplistic since (in this case) it does not depend on any unknown
(i.e. symbolic values).  So, there isn't any benefit to static
verification over just executing the test.

We can make our example more interesting by introducing an _unknown_
value as follows:

```whiley
method test_add_02(u16 x):
  SVM m1 = execute(
    [LOAD,0,LDC,1,ADD],
    [x],
    1024)
  // Check expected output.
  assert exitCode(m1) == OK
  assert peek(m1,1) > x
```

Here, `x` is an _arbitrary_ value and, essentially, we are computing
`x + 1`.  However, attempting to statically verify this with Whiley
produces an error:

```
main.whiley:5:assertion may not hold
  assert peek(m1,1) > x
         ^^^^^^^^^^^^^^
```

The problem is that the `ADD` may have overflowed and wrapped around
to `0`.  That's right!  Our assertion doesn't hold for those
instructions.  We can test this hypothesis by restricting our unknown
value as follows:

```whiley
method test_add_02(u16 x):
requires x < 65535:
   ...
```

This essentially prevents the overflow case and, with that, the
assertion will now statically verify.

### Example 2

As another example, lets consider the case for division.  

```whiley
method test_div_01(u16 x, u16 y):
  SVM m1 = execute(
     [LOAD,0,LOAD,1,DIV],
     [x,y],
     1024)
  // Check expected output.
  assert y == 0 || exitCode(m1) == OK
```

This statically verifies only because our final assertion is quite
weak.  Let's assume we wanted a code sequence which would execute
division safely for all values of `x` and `y`.  Clearly, the above is
not there yet.  We need a check against `y` to handle when `y == 0`.
Here's one possible solution:

```whiley
method test_div_02(u16 x, u16 y):
  SVM m1 = execute(
    [LOAD,0,LOAD,1,JZ,3,LOAD,1,DIV],
    [x,y],
    1024)
  // Check expected output.
  assert exitCode(m1) == OK
```

This introduces the conditional check using the conditional branch
instruction, `JZ`.  To keep the example short, the above just returns
`x` when `y == 0` (but in principle it can do whatever we want).  The
above now statically verifies with Whiley.  _Just think about that for
a second_.  It means we've **proved** (for all input values `x` and
`y`) that the above code sequence _always executes to completion_ on
our simple virtual machine!

## Conclusion

We've formalised a very [simple virtual
machine](https://github.com/DavePearce/SimpleVirtualMachine.wy/blob/main/src/svm.whiley)
in Whiley, and then proved some useful properties about (albeit small)
bytecode sequences.  In fact, there's a lot more we could do with this
tool, such as proving optimisations preserve a program's meaning, etc.
But, we can save those discussions for another day!

## References

   * **KEVM: A Complete Semantics of the Ethereum Virtual Machine**,
     Everett Hildenbrandt *et al.*. In *Proc
     CSF*, 2018. [(PDF)](http://t-news.cn/Floc2018/FLoC2018-pages/proceedings_paper_513.pdf)
     
   * **Defining the Ethereum Virtual Machine for Interactive Theorem
     Provers**, Y. Hirai.  In *Proc WTSC*, 2017. [(PDF)](https://yoichihirai.com/malta-paper.pdf)
     
     
