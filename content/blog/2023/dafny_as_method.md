---
date: 2023-09-16
title: "Efficient Functions in Dafny"
draft: false
metaimg: "images/2023/DafnyByMethod_Preview.png"
metatxt: "An unusual feature of Dafny is that functions can be implemented _by_ methods."
#twitter: "https://twitter.com/whileydave/status/1673926723568832513"
#reddit: ""
---

In developing an [EVM in
Dafny](https://github.com/ConsenSys/evm-dafny), a key requirement is
to test it against the [official EVM test
suite](https://github.com/ethereum/tests).  This means it needs to be
at least reasonably efficient, otherwise completing a test run becomes
impractical.  At the same time, our DafnyEVM is intended to serve as a
specification for which can be used to verify bytecode contracts (see
e.g. [here](https://github.com/Consensys/WrappedEther.dfy)).  This
means we use Dafny `function`s to implement our EVM, rather than
`method`s.  For various reasons, a `function` in Dafny is fairly
restricted and, for example, must use recursion instead of
imperative-style loops.  Dafny will exploit [tail
recursion](https://en.wikipedia.org/wiki/Tail_call) to improve
performance, but this is not always possible.  Thus, we have a
problem: we need to use `function`s for verification, but these are
(sometimes) less efficient than `method`s.

## Example

To illustrate, consider the following method taken from our DafnyEVM:

```dafny
function ToBytes(v:nat) : (r:seq<u8>)
ensures |r| > 0 {
  // Extract the byte
  var b : u8 := (v % 256) as u8;
  // Determine what's left
  var w : nat := v/256;
  if w == 0 then [b]
  else
    ToBytes(w) + [b]
}
```

This converts an arbitrary-sized unsigned integer into a sequence of
one or more bytes (in big endian form).  When translating this into
Java, Dafny regards it as tail recursive and generates (very roughly
speaking) the following code:

```java
List<Byte> ToBytes(v:BigInteger) {
  ArrayList<Byte> r = new ArrayList<>();
  while (true) {
    byte b = v.mod(_256).byteValue();
    BigInteger w = v.div(_256);
    if(w.signum() == 0) {
      return r;
    }
    r.add(b);
} }
```

Dafny optimises the recursive call into a `while` loop --- thereby
making it (relatively) efficient.  Consider now the analoguous
operation which converts a sequence of bytes back into an
arbitrary-sized unsigned integer:

```dafny
function FromBytes(bytes:seq<u8>) : nat {
  if |bytes| == 0 then 0
  else
    var last := |bytes| - 1;
    var byte := bytes[last] as nat;
    var msw := FromBytes(bytes[..last]);
    (msw * 256) + byte
}
```

Dafny does not recognise this as tail recursive and, hence, generates
(very roughly speaking) the following Java:

```java
BigInteger FromBytes(bytes: List<Byte>) {
  if bytes.length() == 0 {
    return BigInteger.ZERO;
  } else {
    int last = bytes.length() - 1;
    byte byte = bytes.get(last);
    bytes = bytes.subList(0,last);
    BigInteger msw = FromBytes(bytes);
    msw = msw.mul(_256);
    msw = msw.add(BigInteger.valueOf(byte & 0xff));
    return msw;
} }
```

Unfortunately, on the official Ethereum test suite this implementation
raises a `StackOverflowException` on some tests.  Specifically, on
tests for certain [precompiled
contracts](https://www.evm.codes/precompiled) which generate _very_
large integers from long byte sequences.

## Function `by method`

Dafny supports a little-known feature (`by method`) which allows a
`function` to be implemented using a `method`.  This means the
specification can still be given in the functional (i.e. recursive)
style whilst the implementation is given in an imperative style
(i.e. with loops).

To better understand this, consider the following example:

```dafny
function sum(items: seq<nat>) : (r:nat) {
  if |items| == 0 then 0
  else items[0] + sum(items[1..])
}
```

This recursively computes the sum of a sequence of unsigned integers
(`nat`s).  Using `by method`, we can add an imperative implementation
like so:

```
function sum(items: seq<nat>) : (r:nat) {
  if |items| == 0 then 0
  else items[0] + sum(items[1..])
} by method {
  r := 0;
  var i : nat := |items|;
  while i > 0
  invariant r == sum(items[i..]) {
    i := i - 1;
    r := r + items[i];
} }
```

This uses a `while` loop to implement the functional specification.
The key point is that _Dafny automatically proves the imperative
version correctly implements the functional specification_.  Whilst
that is pretty amazing, Dafny does need help to do this: firstly, an
`invariant` was needed to help Dafny match the loop and the recursive
specification at each step; secondly, to make this work, the loop must
traverse _backwards_ through the sequence (which is perhaps not that
intuitive).  However, having done this, we now know `sum()` will not
exhaust the call stack at runtime on large inputs!

We can now see the final version of our `FromBytes()` function from before:

```
function FromBytes(bytes:seq<u8>) : (r:nat) {
  if |bytes| == 0 then 0
  else
    var last := |bytes| - 1;
    var byte := bytes[last] as nat;
    var msw := FromBytes(bytes[..last]);
    (msw * 256) + byte
} by method {
  r := 0;
  for i := 0 to |bytes|
  invariant r == FromBytes(bytes[..i]) {
    var ith := bytes[i] as nat;
    r := (r * 256) + ith;
    LemmaFromBytes(bytes,i);
  }
  // Dafny needs help here :)
  assert bytes[..|bytes|] == bytes;
  // Done
  return r;
}
```

The `by method` implementation is similar to what we did for the
`sum()` example, though Dafny requires slightly more hand-holding to
show the `while` loop is equivalent to the recursive specification.
In particular, a lemma `LemmaFromBytes()` is needed to help
reestablish the loop invariant (not shown).  Regardless, the net
effect is the same --- `FromBytes()` no longer exhausts the call stack
on the official EVM test suite.  That is a great result!

## Conclusion

In languages like Dafny, a dichotomy exists between _functional
specifications_ and _imperative implementations_: Specifications must
be functional so the underlying theorem prover can safely reason about
them; on the other hand, algorithms are often more efficient when
implemented using loops.  The ability to seamlessly cross the divide
between these two extremes is a unique feature of Dafny which (when
the need arises) is compelling.
