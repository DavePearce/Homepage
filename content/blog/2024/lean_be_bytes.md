---
date: 2024-05-04
title: "A Proof in Lean"
draft: true
#metaimg: "images/2023/DafnyEVM_Preview.png"
#metatxt: "Formalising the EVM in Dafny allows us prove properties over bytecode sequences."
#twitter: ""
# reddit: "https://www.reddit.com/r/rust/comments/uigljf/puzzling_strong_updates_in_rust/"
---

I have a function for converting a list of bytes into a natural number:

```lean
def from_bytes_be(bytes:List UInt8) : Nat :=
  match bytes with
  | List.nil => 0
  | b::bs =>
      let n := bs.length
      (b.toNat * (256^n)) + (from_bytes_be bs)
```

Here, `b::bs` indicates a list whose first element is the byte `b` and
there are zero or more remaining elements `bs`.

What I need to show is that, if I give this function an array of at
most `n` bytes then I will get back a number `k` where `k < 2ⁿ`.  For
example, if I give it two bytes `b1` and `b2` then it gives back a
number `k < 65536`.

I need this property to show that the number generated from an array
of at most `32` bytes (coming from a `pushX` bytecode) will fit into a
`u256`.

```lean
def from_bytes_be_bound(bytes:List UInt8) : (from_bytes_be bytes) < 256^bytes.length :=
by
  match bytes with
  | [] => simp_arith
  | b::bs =>
      apply pow256_shr
      apply (from_bytes_be_bound bs)
```

The return value from the Lemma is indicated after the `:`.  In this
case, we can think of it as a postcondition which states the property
I wanted.

The proof uses induction on `bytes` with a case analysis:

   * (`bytes == []`)
   
   * (`bytes == b::bs`)
