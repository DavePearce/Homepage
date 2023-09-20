---
date: 2023-09-16
title: "Fast Functions in Dafny"
draft: true
#metaimg: "images/2023/DafnyAbove_Preview.png"
#metatxt: "Dafny is a programming language which often genuinely amazes me."
#twitter: "https://twitter.com/whileydave/status/1673926723568832513"
#reddit: ""
---

In developing an [EVM in
Dafny](https://github.com/ConsenSys/evm-dafny), a key requirement is
that it can be tested against the [official EVM test
suite](https://github.com/ethereum/tests).  This means the code needs
to be at least reasonably efficient, otherwise completing a test run
becomes impractical.  At the same time, our EVM is intended to serve
as a specification which can be used to verify bytecode contracts (see
e.g. [here](https://github.com/Consensys/WrappedEther.dfy)).  This
means we use Dafny `function`s to implement our EVM, rather than
`method`s.  For various reasons, a `function` in Dafny is fairly
restricted and cannot, for example, use imperative loops but instead
must use recursion.  Dafny will attempt to exploit [tail
recursion](https://en.wikipedia.org/wiki/Tail_call) to improve
performance, but this is not always possible.  Thus, we have a
problem: we need to use `function`s for verification, but these are
inherently less efficient than `method`s.

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
    r.add(r);
  }
}
```

Here, Dafny optimises away the recursive call using a `while` loop ---
thereby making it (relatively) efficient.  Now, let us consider the
analoguous operation which converts a sequence of bytes back into an
arbitrary-sized unsigned integer:

```dafny
function FromBytes(bytes:seq<u8>) : (r:nat) {
  if |bytes| == 0 then 0
  else
    var last := |bytes| - 1;
    var byte := bytes[last] as nat;
    var msw := FromBytes(bytes[..last]);
    (msw * 256) + byte
}
```

In this case, Dafny does not recognise this as tail recursive and,
hence, generates (very roughly speaking) the following Java:

```java
BigInteger FromBytes(bytes: List<Byte>) {
  if bytes.length() == 0 {
    return BigInteger.ZERO;
  } else {
    int last = bytes.length() - 1;
    byte byte = bytes.get(last);
    BigInteger msw = FromBytes(bytes.subList(0,last));
    msw = msw.mul(_256).add(BigInteger.valueOf(byte & 0xff));
    return msw;
  }
}
```

Unfortunately, on the official Ethereum test suite this implementation
raises a `StackOverflowException` on certain tests (specifically, in
certain [precompiled contracts](https://www.evm.codes/precompiled),
very large integers are generated from long byte sequences).

## Function `by method`



