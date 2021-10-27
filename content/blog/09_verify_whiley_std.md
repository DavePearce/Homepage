---
date: 2021-10-27
title: "Verifying the Whiley Standard Library"
draft: true
#twitter: ""
#reddit: ""
---

For sometime now, its been possible to use
[Boogie](https://github.com/boogie-org/boogie) /
[Z3](https://github.com/Z3Prover/z3) as a backend to verify Whiley
programs.  Initially, that was pretty sketchy but it's really starting
to ramp up now.  If you haven't heard
of it before, Boogie is an _Intermediate Verification Language (IVL)_
that provides a human-readable interface sitting on top of an [SMT
solver](https://en.wikipedia.org/wiki/Satisfiability_modulo_theories).
In this case, we use Z3 as the SMT solver --- but it is possible to
use others, such as [CVC4](https://cvc4.github.io/), etc.  Boogie was
initially developed at Microsoft, but is now maintained by a
consortium that appears to include Facebook and Amazon.

Boogie implements an extended form of Dijkstra's [Guarded Command
Language](https://en.wikipedia.org/wiki/Guarded_Command_Language).
For example, here's a simple Whiley program:

```Whiley
function abs(int x) -> (int r)
ensures r >= 0
ensures (r == x) || (r == -x):
   if x >= 0:
      return x
   else:
      return -x
```

The [Whiley2Boogie](https://github.com/Whiley/Whiley2Boogie)) backend
for Whiley translates the above program into Boogie.  Here's a
simplified version of how that looks:


```Boogie
procedure abs(x : int) returns (r : int)
ensures r >= 0;
ensures (r == x) || (r == -x);
{
   if(x >= 0) {
      r := x;
      return;
   } else {
      r := -x;
      return;
   }
}
```

## STD.wy

The Whiley standard library,
[STD.wy](https://github.com/Whiley/STD.wy), presents an interesting
target for verification, as it is currently the largest single body of
code written in Whiley.  Furthermore, my goal is to verify the
standard library and, from thereafter, ensure it is verified on every
commit.

## TODO


Talk about now that have Boogie trying to verify the standard library,
and what's in it.  Talk about verification issue, and `copy<>`.  Talk
about `unsafe`.
