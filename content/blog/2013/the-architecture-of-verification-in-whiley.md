---
date: 2013-06-26
title: "The Architecture of Verification in Whiley"
draft: false
---

As the Whiley compiler continues to evolve, certain aspects of its architecture are really starting to mature.  One of the more recent pieces to take shape is the *verification pipeline*.  This is the process by which a Whiley file is converted into a series of verification conditions, which are then checked by the [automated theorem prover](http://en.wikipedia.org/wiki/Automated_theorem_proving).

A high level overview of the architecture is the following:

{{<img class="text-center" width="50%" src="/images/2013/Whiley-Verification-Architecture.jpg">}}

Here, we see some of the main components in the Whiley compiler:
   * **The Whiley Compiler (WyC)** --- The compiler front-end, responsible for parsing and type checking whiley `source` files and generating (binary) `wyil` files.

   * **The Whiley Intermediate Language (WyIL)** --- A register-based intermediate language, similar in some ways to JVM bytecode.  This essentially provides a simplified form of a `whiley` source file which is more manageable, and where many intricate aspects are already completed (e.g. *name resolution*, *typing*, *desugaring*, etc).

   * **The Whiley Constraint Solver (WyCS)** --- The automated theorem prover responsible for checking whether a given formula in first-order logic is *satisfiable* or not.  These *verification conditions* are represented as (binary) `wycs` files.  A human-readable file format is provided in the form of `wyal` files, which can be "compiled" to `wycs` files independently.


In this post, I'm just going to focus on the flow from `whiley` source files to the `wycs` files which are passed to the theorem prover.  One of the really nice aspects of the compiler architecture is that we can see these files in a human-readable form at each step.
## Compiling Whiley to WyIL
The first step of the process is to translate `whiley` source files into binary `wyil` files, which are analogous to JVM `class` files.  Here's our simple Whiley function which we're going to compile and verify:

```whiley
int abs(int x) ensures $ >= 0:
    if x >= 0:
        return x
    else:
        return -x
```

This defines the *absolute function* which returns the positive image of any integer value. The `ensures` clause defines a postcondition which states that the value returned (given by `$`) is non-negative.  If we compile this function into the WyIL form, we'll get something like this:

```whiley
int abs(int):
ensures:
    const %3 = 0 : int
    assertge %0, %3 "postcondition not satisfied" : int
body:
    const %2 = 0 : int
    iflt %0, %2 goto label0 : int
    return %0 : int
.label0
    neg %5 = %0 : int
    return %5 : int
```

As we can see, the Whiley Intermediate Language is a register-based bytecode with unstructured control-flow. Here, registers are prefixed with `%` (e.g. `%3`); the `const` bytecode loads a constant value into a register; the `iflt` bytecode branches to a label if its first operand is less than its second; the `neg` bytecode negates its operand and assigns to a given register; finally, the `return` bytecode returns its operand.

There are several advantages of the WyIL form over Whiley source code: firstly, all names have been resolved using their context (i.e. combination of enclosing function and imports); secondly, all types have been propagated through expressions (and checked); thirdly, the number of constructs at the WyIL level is far fewer than at the source level (e.g. conditional branches and goto bytecodes can encode a wide range of source-level control-flow constructs).  This latter point is particularly important, as it helps decouple the language syntax from the compiler back-ends and other optimisation passes (i.e. changes to the syntax do not usually require changes to the WyIL).
## Compiling WyIL to WyAL
The *Whiley Assertion Language (WyAL)* provides a useful interface between the WyIL bytecode programs and the automated theorem prover (WyCS).  The `wyal` source files provide a human-readable dialect of first-order logic with various additional operations (e.g. for arithmetic, sets, lists, etc).  As an example, consider the following WyAL code:

```whiley
assert:
    forall(int x):
        if:
            x > 0
        then:
            x >= 0
```

Here, we see a very simple logical constraint which asserts that `x > 0` implies `x >= 0` (which we know is true). Putting this assertion through the constraint solver will not produce any assertion errors (indicating the theorem prover believes the assertion holds). The WyAL language bears some resemblances to the input languages for [Z3](http://z3.codeplex.com/) (i.e. [SMT-LIB](http://www.smt-lib.org/)) and [Yices](http://yices.csl.sri.com/).

To verify a Whiley source file is correct, it is first translated into a WyIL file, then into a WyAL file which, finally, is verified as correct (or not) by the WyCS theorem prover.  Each Whiley function can generate multiple assertions (a.k.a *verification conditions*) in the generated WyAL file.  For our running `abs(int)` example two assertions are generated: one for the execution path going down the `true` branch; and the other going down the `false` branch.  In fact, our simple WyAL assertion above represents the verification condition for the `true` branch of our running example.
## Conclusion
Hopefully this article has given a little insight into the mechanism for verifying a Whiley source file is correct.  Of course, a lot of questions are left unanswered and, unfortunately, many aspects of the mechanism remain undocumented (for now).  For example, the WyAL language is completely undocumented at this stage.  However, over the coming weeks, months and years I hope to document more of the compiler and how it works.
