---
date: 2011-12-13
title: "Efficient Value Semantics for Whiley"
draft: true
---

The latest release of the Whiley compiler (v0.3.12) includes an optimisation for passing compound structures (e.g. lists, sets and records) by value.  This is really important because all compound structures in Whiley have *value semantics*, meaning they are always passed by value.  In fact, Whiley does not support references or pointers as found in other languages (e.g. Java, C, etc).  This means Whiley is really more of a [functional programming language](http://wikipedia.org/wiki/Functional_programming) than an [imperative language](http://wikipedia.org/wiki/Imperative_programming).  As we'll see, compared to other functional languages, Whiley is a little unusual as it supports *imperative updates* to compound structures.

## An Example

*So, what does this all mean?* Well, let's have a look at an examples:

```whiley
[real] normalise([real] data, real max):
    for i in 0..|data|:
        data[i] = data[i] / max
    return data

void ::main(System sys):
    original = [1.2,2.3,4.5]
    normalised = normalise(original,0.5)
    sys.out.println(original)
    sys.out.println(normalised)
```

This function updates a list of real values in-place using a typical (imperative) list assignment of the form `data[i] = ...`.  In Java, where variables of array type are references to array objects, this would simultaneously update the callers version of this array as well.  In Whiley, however, this is not the case.  The list assignment inside `normalise()` does not modify the `original` list in `main()`.  This is because the list is *passed-by-value* in the true sense of the word.  You can think of this as meaning that *the whole list is copied when the normalise function is called* to prevent any interference between caller and callee.

## (In)efficiency?

*Ok, but that all that copying must be crazily inefficient! *Well, not so.  This is because, whilst the semantics of Whiley dictate pass-by-value for compound structures, the underlying implementation actually passes them by reference.  Furthermore, it clones them lazily with reference counting being employed to reduce the situations where a clone is actually required.  Before explaining how the reference counting works, let's look at some actual data first:

omitted tag "table"

*What is this data showing us?* Well, "# Clones" shows the number of clones actually performed versus the number that a naive implementation of value semantics would have performed.   The naive version clones whenever an argument is passed or returned from a function, whenever a list element is assigned, etc.  It represents an upper bound on the number of clones required.  So, for example, looking at the Gunzip benchmark we see that only 873 clones were required out of a maximum of 140561.  This indicates that, in the vast majority of cases, value semantics does not actually result in many extra clones.

*But the results for Jasm look quite bad?* Well, yes.  But, actually, they're not too bad if we consider the cause.  Essentially, the Jasm benchmark consists of an inner loop for decoding bytecodes.  Roughly speaking, it looks like this:

```whiley
([Bytecode],int) decode([byte] data, int pos):
    opcode = Byte.toUnsignedInt(data[pos])
    kind,fmt,type = decodeTable[opcode]
    switch fmt:
        case FMT_EMPTY,
            ....
        case FMT_I8:
            idx = Byte.toUnsignedInt(data[pos+1..pos+2])
            return {offset: pos-14, op: opcode},pos+2
        case FMT_I16:
            idx = Byte.toUnsignedInt(data[pos+3..pos+1])
            return {offset: pos-14, op: opcode},pos+3
        case FMT_VARIDX:
            index = Byte.toUnsignedInt(data[pos+1..pos+2])
            return VarIndex(pos-14, opcode, index),pos+2
        ...
```

What we see is that the loop first reads the opcode byte and then, based on this, reads zero or more additional bytes  (e.g. `data[pos+1..pos+2]`).  This sublist operation is the root cause of almost every  clone reported in the above table for Jasm.  In fact, it's not a clone of the entire `data` list; rather, it just copies those few bytes being read into a new list --- meaning it's not really very expensive.

## Implementation

*So, how does the reference counting work?* One of the key optimisations is knowledge of when a variable is no longer *live*.  For example:

```whiley
[int] f([int] xs):
   ys = xs // ref count not increased
   ys[0] = 1
   return ys
```

Here, the variable `xs` is dead after the assignment to `ys`.  Therefore, we don't need to increase the reference count of the object it refers to as, effectively, ownership is transferred from `xs` to `ys`.  If the reference count of `xs` on entry was 1, then it will still be 1 at the assignment `ys[0] = 1` and, hence, the assignment can be performed in place.  Now, let's consider a more detailed example:

```whiley
  [int] f([int] z):
      z[0] = 1 // Object #2 created
      return z

  [int] g():
      x = [1,2,3] // Object #1 created
      y = f(x)
      return x + y
```

On Line 6,  Object #1 (which represents the constructed list) has an initial reference count of one.  This does not change when it is assigned to `x`.  Its reference count is then increased by one on Line 7, as `x` is used in the invocation expression and *remains live afterward*.  On entry to `f()`, parameter `z` refers to Object #1, which now has reference count two. Therefore, the list assignment on Line 2 creates an entirely new object before updating it.  It also decrements the reference count of Object #1.  On Line 8, `x` still refers to Object #1 (now with reference count one) and, hence, the append is performed in place without cloning.
## Conclusion
Reference counting is a critical optimisation to improving the performance of Whiley programs.  The latest version of the compiler (finally) supports this, although there is still room for considerable improvement.  In fact, a good starting point is the following paper:
   * Staged Static Techniques to Efficiently Implement Array Copy Semantics in a MATLAB JIT Compiler, N. Lameed and L. Hendren.  In *Proceedings of the Conference on Compiler Construction*, 2011. [[PDF](http://www.sable.mcgill.ca/mclab/mcvm/mcvmcc2011.pdf)]


Anyway, that's all folks!