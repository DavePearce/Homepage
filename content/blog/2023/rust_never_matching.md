---
date: 2023-04-16
title: "Pattern Matching in Rust's Neverland"
draft: false
metaimg: "images/2023/Rust_Neverland_Preview.png"
metatxt: "Pattern matching with the never type offers exciting possibilities!"
#twitter: ""
# reddit: "https://www.reddit.com/r/rust/comments/uigljf/puzzling_strong_updates_in_rust/"
---

Recently, I've been working on a tool for manipulating EVM bytecode
and assembly language.  I wanted describe low level bytecode and
assembly language using the same datatype.  At the same time, I wanted
some instructions to be allowed only in assembly language but not
bytecode (e.g. labels only make sense at the assembly language level).
I was hoping to encode this _precisely_ in stable Rust, but couldn't
make it work.  However, turns out you can do this on nightly (where
[RFC1872](https://github.com/rust-lang/rust/issues/51085) is merged).
That is actually pretty neat, and I want to explain why.

## Background

A very simplified version of my bytecode system is:

```Rust
enum Insn<T> {
    PUSH(usize),
    LABEL(T),
    JUMP(T),
    RET
}

type Bytecode = Insn<usize>;
type Assembly = Insn<String>;
```

This is a subset of the instructions I needed, but its enough to
illustrate what I was trying to achieve.  As indicated, we instantiate
`Insn<T>` in one of two ways:

   1. `Bytecode`.  This describes concrete bytecode
      instructions where exact jump destinations (expressed as byte
      offsets) are known.
   2. `Assembly`.  This describes assembly language instructions which
      use labels to describe control-flow.

Thus, we can have a simple function which "assembles" assembly
language instructions into concrete instructions:

```Rust
fn assemble(&[Bytecode]) -> Vec<Assembly>
```

(**Note**: To make this work properly, we'd want some error handling
but I'm ignoring that here).  Similarly, we can have a simple function
which "disassembles" concrete instructions back into assembly language
instructions:

```Rust
fn diassemble(&[Bytecode]) -> Vec<Assembly>
```

The interesting thing about these two functions is that some
instructions don't make sense, depending on the context.  For example,
labels are not concrete instructions as, in effect, they are
artificial instructions used only for assembly language.  So, we
might write something like this:

```Rust
fn diassemble_insn(insn: Bytecode) -> Assembly { 
  match insn {
    Bytecode::PUSH(imm) => { 
      Assembly::PUSH(imm) 
    }          
    Bytecode::JUMP(offset) => { todo!() }
    Bytecode::RET => { 
      Assembly::RET 
    }     
    Bytecode::LABEL(_) => { unreachable!(); }
  }
}
```

This is fine as it goes, but it's a shame we have to use
`unreachable!()` to signal the `LABEL` case should not occur.  So, I
was wondering: _can we use Rust's type system to encode these
constraints?_ More specifically, so that `Insn::Label(usize)`: (1) cannot
be instantiated; and (2) cannot be matched against.  The short answer is:
yes!

## Stable Version

My first approach was to exploit types which cannot be instantiated (so-called [empty types](https://doc.rust-lang.org/nomicon/exotic-sizes.html#empty-types)).  It looks something like this:

```Rust
enum Insn<T,L> {
    PUSH(usize),
    LABEL(L),
    JUMP(T),
    RET
}

enum Void {}
type Bytecode = Insn<usize,Void>;
type Assembly = Insn<String,String>;
```

This prevents instances of `Bytecode::LABEL` from being created, since
we cannot create an instance of type `Void`.  That's great!
Unfortunately, I was disappointed the following still didn't compile:

```Rust
fn diassemble_insn(insn: Bytecode) -> Assembly { 
  match insn {
    Bytecode::PUSH(imm) => { 
       Assembly::PUSH(imm) 
    }          
    Bytecode::JUMP(offset) => { todo!() }
    Bytecode::RET => { 
       Assembly::RET 
    }
  }
}
```

Here, I've dropped the case for `Bytecode::LABEL` since we know this
cannot arise any more.  Unfortunately, the Rust compiler still
requires you to cover the case for `Bytecode::LABEL`.  So, I
[tweeted](https://twitter.com/whileydave/status/1645976021630595073)
out my disappointment.

## Nightly Version

Thanks to [Varun](https://twitter.com/typesanitizer) who
[responded](https://twitter.com/typesanitizer/status/1645986739193012226)
by pointing me to
[RFC1872](https://github.com/rust-lang/rust/issues/51085).  This is
exactly what we needed to fix the above, though it is not yet in
stable.  In essence instead our own `Void` type, we use Rust's new
so-called [never type](https://github.com/rust-lang/rust/issues/35121)
(`!`) which the compiler knows is uninhabited.  Here's the final
version:

```Rust
#![feature(never_type)]
#![feature(exhaustive_patterns)]

enum Insn<T,L> {
    PUSH(usize),
    LABEL(L),
    JUMP(T),
    RET
}

type Bytecode = Insn<usize,!>;
type Assembly = Insn<String,String>;
```

Here, instead of using a custom `Void` type in defining `Bytecode`, we
can now use `!` directly as a type.  With this definition our version
of `disassemble_insn()` above which omits the case for
`Bytecode::LABEL` now compiles.  Furthermore, Rust emits a warning
(`unreachable pattern`) if a case for `Bytecode::LABEL` is included!

## Conclusion

The never type `!` looks to be a very handy extension to Rust, and I'm
looking forward to it landing in the stable branch.  When combined
with the exhaustive pattern matching extension in
[RFC1872](https://github.com/rust-lang/rust/issues/51085), it really
is very powerful.
