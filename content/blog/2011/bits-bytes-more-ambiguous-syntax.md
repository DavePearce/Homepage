---
date: 2011-07-04
title: "Bits, Bytes and More Ambiguous Syntax"
draft: false
---

Recently, I added a first-class `byte` type to Whiley.  Unlike Java, this is not interpreted as some kind of signed or unsigned `int`.  That's because I find that very confusing since a `byte` is really just a sequence of bits without any interpretation.  

Therefore, in Whiley, a `byte` is just that: a bit sequence on which you can perform the usual operations, such as bitwise AND/OR/XOR operations, as well as left and right shifts.  Here's a snipper which accepts a `byte` and converts it into an unsigned `int` assuming a little endian representation:

```whiley
int le2uint(byte b):
    r = 0
    base = 1
    while b != 0b:
        if (b & 00000001b) == 00000001b:
            r = r + base
        b = b >> 1
        base = base * 2
    return r
```

As usual, updating the compiler to support the `byte` type did not go according to plan.  That's because I ran into yet another ambiguity of syntax.  This time the ambiguity is surrounding the `|` operator, which represents bitwise OR and is also a delineator for set comprehensions.  Consider these three examples:

```whiley
x = b1 | b2    // bitwise OR
y = { a | a in as,  a > 0 }    // comprehension
z = { a | a in ys }    // hmmmm, set generator or comprehension?
```

In fact, it's fairly easy to disambiguate the last statement --- it must be a set comprehension.  That's because `a in ys` has `bool` type, and this cannot be part of a bitwise OR operation.  However, at the point it needs to make this decision, the compiler doesn't have access to type information.  This is because type propagation occurs after the source has been translated into the intermediate language (wyil).  But, there is no bytecode in the Wyil for a comprehension and, instead, it is constructed using `for` loops).

I believe it is possible to disambiguate bitwise OR from a set comprehension in the parser --- however, for the moment, I just resolve it by preferring set comprehensions over bitwise OR.  The impact of this is that the expression `{ a|b }` won't parse because it's not a valid set comprehension (but is a valid set generator).  Instead, you have to help the parser using braces.  So, `{ a|b }` becomes `{ (a|b) }`.