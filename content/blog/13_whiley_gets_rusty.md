---
date: 2022-05-31
title: "Whiley gets Rusty!"
draft: false
#twitter: ""
#reddit: ""
---

I've been learning [Rust](https://www.rust-lang.org/) for a while now
but, at the same time, trying to continue developing
[Whiley](https://whiley.org).  Since Whiley was written entirely in
Java, these activities were mutually exclusive and it was frustrating
trying to balance things!  One day I'd had enough ... so I decided to
rewrite part of the Whiley compiler in Rust.  This is not as dramatic
as it sounds, and I'm not planning to rewrite the whole compiler in
Rust (yet).  That would be a lot of work.  However, bringing these
things together means I can learn Rust and work on Whiley at the same
time.  _Its actually working out pretty well!_

### Distribution

An unexpected benefit of working with Rust is that it makes
_distributing_ Whiley quite easy.  Installing Whiley through Cargo is
really simple:

```
cargo install whiley
```

Assuming you have Java installed, you can just run `wy --help` and see
the options for building Whiley programs.  For example, we can
initialise and then build a new project like so:

```
> wy init
> wy build
> wy run
Hello World!
```

_This is pretty neat!_

### Architecture

An interesting question is how the compiler blends Rust with Java.
The compiler has always been structured as a build tool with various
plugins implementing core functionality.  For example, the
[WhileyCompiler](https://github.com/Whiley/WhileyCompiler) is actually
a plugin responsible for compiling Whiley into its intermediate
language, whilst
[Whiley2JavaScript](https://github.com/Whiley/Whiley2JavaScript) is
another plugin responsible for translating the intermediate language
into JavaScript, etc.  In principle, we can add plugins to provide
more functionality (e.g. compiling to
[Wasm](https://en.wikipedia.org/wiki/WebAssembly) or [EVM
bytecode](https://en.wikipedia.org/wiki/Ethereum#Virtual_machine)).
This is how it looks:

{{<img class="text-center" src="/images/2022/WhileyCompilerOverview.png" height="300px" alt="Architectural diagram of Whiley compiler.">}}

What we see in the diagram is that the
[WhileyBuildTool](https://github.com/Whiley/WhileyBuildTool)
orchestrates the plugins, and _it is this component that I have
rewritten in Rust_.  Roughly speaking, the build tool downloads the
`jar` files representing the plugins from Maven central as needed and
fires up a JVM instance to run them.  Its pretty simple, and it works
surprisingly well!

Another interesting opportunity here is that I can incrementally
rewrite the compiler in Rust by rewriting the plugins one by one.
Whilst none of the plugins is written in Rust yet, I plan to implement
a simple one in the near future as a proof-of-concept.
