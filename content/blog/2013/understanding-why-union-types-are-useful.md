---
date: 2013-07-31
title: "Understanding why Union Types are useful"
draft: false
---

The Whiley programming language uses [union types](https://en.wikipedia.org/wiki/Union_type) as a way of combining types together.  Here's a simple example to illustrate:

```whiley
function indexOf(string str, char c) => null|int:
    for i in 0..|str|:
       if str[i] == c:
          return i // found a match
    // didn't find a match
    return null
```

Here, the type `null|int` is a *union type* --- i.e. the union of types `null` and `int`. This means a variable of type `null|int` can hold any valid `int` or `null`. In this example, I'm using `null` to signal that the character `c` was not found in the string `str`. More formally, we can imagine types as sets where `int` is the set of all integers, and `null` is the singleton set containing the special value `null`. Then the union type `null|int` can be thought of as the *set union* of `int` and `null`.

This type `null|int` may seem familiar. In fact, it's roughly equivalent to the Java type `java.lang.Integer`. As you may know, reference types in Java may hold the `null` as well as a valid object reference. A critical mistake in the design of Java (in my opinion) was to allow potentially `null` references to be dereferenced and, hence, the possibility of `NullPointerException`s. The design of Whiley contrasts with this, where union types provide an elegant solution. Specifically, a variable of type `null|int`*cannot be treated as an int* (i.e. you cannot perform arithmetic on it, etc). Instead, we must first check whether it actually is an `int` (or not) using a runtime type test. The following illustrates:

```whiley
function replaceFirst(string str, char old, char repl) => string:
    idx = indexOf(str,old)
    if idx is int:
        str[idx] = repl
    // return potentially updated string
    return str
```

Here, the `replaceFirst()` function is replaces the first occurrence of a given character with an alternative. It uses `indexOf()` to find the first occurrence, and assigns the result to `idx` on Line 2. At this point, we cannot immediately perform the assignment `str[idx]=repl` (and, if we attempted this the compiler would give an error). This is because, immediately after Line 2, the variable `idx` has type `int|null`. However, on the true branch of the type test `idx is int`, the compiler automatically retypes `idx` to have type `int` and, hence, the assignment on Line 4 is safe.
## Inheritance versus Union Types
Object-oriented languages use inheritance of classes (or interfaces) as a way to construct data types with commonalities.  In statically typed languages (like Java or C#), a common complaint is that this leads to "rigid" class hierarchies which (perhaps surprisingly) restrict how classes can be reused.  To understand this better, let's consider a concrete example in Java:

```java
abstract class Bytecode {
   ...
   abstract int stackDiff();
   ...
}

interface Branch { String target(); }

class Store extends Bytecode { ... }

class Goto extends Bytecode implements Branch { ... }
```

This is a simplification of a library I created for generating Java Bytecode in the Whiley compiler. Obviously, I've stripped out a lot of stuff for simplicity. However, we can see that every `Bytecode` has the method `stackDiff()` in common. Furthermore, I also wish to group other bytecodes together. For example, branching bytecodes (e.g. `goto`, `ifeq`, etc) have their destination target (which is a label) in common. To implement this, I've use a number of supplementary interfaces (e.g. `Branch` above). This is pretty flexible and allows me to describe lots of different groupings of the bytecodes.

Anyone who knows much about Java Bytecode will know that there are a large number of different bytecode groupings which are useful. For example, we might want to group bytecodes together that occupy a single byte in a `class` file (e.g. `ineg`, `pop`, `dup`, `return`, etc). Or, we might want to group together bytecodes which operate on `int` values (e.g. `iadd`, `ificmp_eq`, `ireturn`, etc). In fact, there are so many possible groupings that it is essentially impossible for the library designer (me, in this case) to implement them all up front as separate interfaces. The problem is, once we've defined a given class (e.g. `Goto` above), we're stuck with those interfaces it does (or doesn't) implement. In other words, *we can't group our bytecodes differently after the fact*.

Now, union types provide an alternative approach which is both simple to understand and more flexible. Let's rewrite the above in Whiley:

```whiley
type Store is {
  int stackDiff(),
  ...
}

type Goto is {
  int stackDiff(),
  string target(),
  ...
}

type Bytecode is Store | Goto | ...
type Branch is Goto | ...
```

As we can see, grouping of bytecodes can be performed using union types. Furthermore, any union of records exposes fields common across all members. Therefore, if all bytecodes provide the `stackDiff()` method, then we can call this method whenever we have a variable of `Bytecode` type. The key here is that *the groupings are independent of the individual components*. Thus, we can define other groupings after the fact whenever we wish (e.g. in client code, etc).
## More on Groupings
My use of union types above is not quite identical to the original Java code. That's because the `Bytecode` type is fixed at its definition and cannot be extended. In contrast, the Java `Bytecode` class can be arbitrarily extended with new bytecodes. However, we can get a similar effect in Whiley by using another feature called *structural typing*. For example, we could define `Bytecode` as `{ int stackDiff(), ... }`. Here, the `...` is significant and indicates an *open record* (i.e. one which may have other fields not listed). Then, any record type which has the method `int stackDiff()` is automatically a subtype of `Bytecode`.

We can think of union types as providing *closed* (i.e. fixed) groupings of types, whilst structural subtyping provides *open* (i.e. extensible) groupings of types.  Taken together, these provide an interesting alternative mechanism for defining data types (compared with classical inheritance).  Closed groupings are used in situations where you know exactly what types you have, and e.g. you want the compiler to check you've covered all cases.  In contrast, open groupings are used when you want to provide an extension point for client code.  This provides a neat separation of the two different use cases, compared with e.g. Java where you only have inheritance to use.

Anyhow, that's all for now!
