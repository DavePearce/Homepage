---
date: 2014-06-20
title: "Understanding Ghost Variables in Software Verification"
draft: false
---

Verification tools often support the use of *ghost variables* to help in the verification process. A ghost variable is not needed for the program to execute, and will not be compiled into [object code](http://en.wikipedia.org/wiki/Object_code). Typically, they are declared with a `ghost` modifier or similar, but are otherwise identical to normal program variables (i.e. they have a type, support the usual operators, etc). The purpose of ghost variables provide a way for the programmer to guide the verifier in checking a program is correct. The [Java Modelling Language](http://openjml.org/), [Spec#](http://research.microsoft.com/en-us/projects/specsharp/), [Dafny](http://research.microsoft.com/en-us/projects/dafny/), and [Frama-C](http://frama-c.com/) all support ghost variables in some manner (see e.g. [this tutorial](https://www.youtube.com/watch?v=P2durYFsJSA) on using them in Dafny).

In the Whiley programming language there is currently no support for ghost variables.  However, it turns out that you can still achieve the same effect using just local variables.  In the future, we may extend Whiley to support an explicit `ghost` modifier.
## Example
To illustrate what ghost variables are, consider verifying the following simple function:

```whiley
function add([int] v1, int c) => ([int] v2)
// Return must have same length as parameters
ensures |v2| == |v1|:
   //
   int i = 0
   //
   while i < |v1| where i >= 0:
      v1[i] = v1[i] + c
      i = i + 1
   //
   return v1
```

This function accepts a list of integers and adds a constant to each. The Whiley compiler will not verify this function and complains that the *postconditon is not satisfied*. This is because the loop invariant says nothing about the size of variable `v1`, and so the compiler knows nothing after the loop about it (e.g. that it was unchanged by the loop).

To verify the above function the loop invariant needs to state *the size of v1 is not modified by the loop*.  Unfortunately, there is no explicit syntax for expressing loop variants in Whiley.  Instead, we can simulate a *ghost variable* to achieve the same effect:

```whiley
function add([int] v1, int c) => ([int] v2)
// Return must have same length as parameters
ensures |v2| == |v1|:
   //
   int i = 0
   int tmp = |v1| // ghost variable
   //
   while i < |v1| where i >= 0 && |v1| == tmp:
      v1[i] = v1[i] + c
      i = i + 1
   //
   return v1
```

The Whiley compiler will now verify this function is correct. The local variable `tmp` is acting as a ghost variable here.  This is because it was introduced just to ensure the function passes verification. The variable `tmp` simply saves the original length of `v1` and this allows us to state in the loop invariant that the size of `v1` is unchanged by the loop.

In the above example, the variable `tmp` is just an ordinary local variable. This is because Whiley has no explicit syntax for ghost variables. One simple idea is to introduce the `ghost` modifier to help signify a ghost variable. For example:

```whiley
   ...
   ghost int tmp = |v1| // ghost variable
   ...
```

This modifier would help clarity and would also signal to the compiler that this variable can be ignore in the generated executable file.
## Conclusion
Ghost variables are often used to help verify that a function meets its specification. Although Whiley has no explicit syntax for ghost variables, a similar can be achieved through the use of local variables.
