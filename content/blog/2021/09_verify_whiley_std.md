---
date: 2021-10-27
title: "Verifying the Whiley Standard Library"
draft: false
metaimg: "images/2021/Verifying_Whiley_Std.png"
metatxt: "Using Boogie / Z3 it is possible verify a large number of Whiley programs."
#twitter: ""
#reddit: ""
---

For sometime now, its been possible to use
[Boogie](https://github.com/boogie-org/boogie) /
[Z3](https://github.com/Z3Prover/z3) as a backend for verifying Whiley
programs.  Initially that was pretty sketchy, but it's really starting
to ramp up now.  If you haven't heard of it before, Boogie is an
_Intermediate Verification Language (IVL)_ that provides a
human-readable interface sitting on top of an [SMT
solver](https://en.wikipedia.org/wiki/Satisfiability_modulo_theories).
In this case, we use Z3 as the SMT solver --- but it is possible to
use others, such as [CVC4](https://cvc4.github.io/), etc.  Boogie was
initially developed at Microsoft, but is now maintained by a
consortium that appears to include Facebook and Amazon, amongst
others.

For example, here's a simple Whiley program:

```Rust
function abs(int x) -> (int r)
ensures r >= 0
ensures (r == x) || (r == -x):
   if x >= 0:
      return x
   else:
      return -x
```

The [Whiley2Boogie](https://github.com/Whiley/Whiley2Boogie) backend
 is responsible for translating the above program into Boogie.  Here's
 a simplified version of how that looks:


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

Here we can see there are a few differences from the Whiley code but,
in this example at least, the two look quite similar.  In fact, Boogie
implements a form of Dijkstra's [Guarded Command
Language](https://en.wikipedia.org/wiki/Guarded_Command_Language)
which has been extended with syntax to bring it closer to a
programming language.  However, in some cases, it can end up looking
quite different.


## STD.wy

The Whiley standard library,
[STD.wy](https://github.com/Whiley/STD.wy), presents an interesting
target for verification, as it is currently the largest single body of
code written in Whiley.  My goal is to verify the standard library
and, from thereafter, ensure it is verified on every commit (using a
[GitHub Action](https://github.com/Whiley/WhileyBuildAction)).  The
library is still pretty small, but contains some of the things you
would expect, such as collections (e.g. `std::vector`,
`std::hash_map`), ASCII support (`std::ascii`), math functions
(`std::math`) and various array manipulation functions (`std::array`).
Yes, it is very much a work in progress ...  _and now is the time to get
verification ingrained as part of the build process_.

As an example, here's a function from `std::array`:

```Rust
// find first index after a given start point in list which matches character.
// If no match, then return null.
function first_index_of<T>(T[] items, T item, uint start) -> (uint|null index)
// Starting point cannot be beyond array
requires start <= |items|
// If int returned, element at this position matches item
ensures index is uint ==> items[index] == item
// If int returned, element at this position is first match
ensures index is uint ==> !contains(items,item,start,index)
// If null returned, no element in items matches item
ensures index is null ==> !contains(items,item,start,|items|):
    //
    for i in start .. |items|
    // No element seen so far matches item
    where !contains(items,item,start,i):
        if items[i] == item:
            return (uint) i
    //
    return null
```

This searches forward in `items` from a given `start` index and
returns either the first index matching `item` or (if none exists)
returns `null`.  The specification is reasonably involved, but
essentially says this in logical form.  To keep things a bit more
intuitive we've used `contains()` instead of raw quantifiers.  Here,
`constrains()` is a `property` defined in `std::array` as follows:

```Rust
property contains<T>(T[] lhs, T item, int start, int end)
// Some index in given range contains item
where some { i in start..end | lhs[i] == item }
```

The module `std::array` contains several predefined properties like
this which are helpful in specifying array manipulating functions.
The great thing about `first_index_of()` above is that we can now
[statically
verify](https://en.wikipedia.org/wiki/Software_verification) that its
implementation meets its specification.  No need to write lots of
extensive tests checking all the edge cases!! This also means
`first_index_of()` is guaranteed not to perform an out-of-bounds
access or exhibit other undefined behaviour.  That is something really
quite powerful, and [Boogie](https://github.com/boogie-org/boogie) /
[Z3](https://github.com/Z3Prover/z3) is key to making it work.

## Challenges

At this point, the goal was fairly straightforward: _go through all
the library functions adding specifications so they will now
statically verify_.  This was quite a laborious task since, initially,
large chunks of the library had not been specified.  Having largely
completed that now, we can proceed incrementally by requiring that
_all new code must be fully specified and pass verification_.

Still, there are some challenges.  Most notably, we have at least
one function which currently cannot be verified (due to limitations
with Boogie / Z3).  This is the following function:

```Rust
unsafe function copy<T>(T[] src, uint srcStart, T[] dest, uint destStart, uint length) -> (T[] result)
// Source array must contain enough elements to be copied
requires (srcStart + length) <= |src|
// Destination array must have enough space for copied elements
requires (destStart + length) <= |dest|
// Result is same size as dest
ensures |result| == |dest|
// All elements before copied region are same
ensures equals(dest,0,result,0,destStart)
// All elements in copied region match src
ensures equals(src,srcStart,result,destStart,length)
// All elements above copied region are same
ensures all { i in (destStart+length) .. |dest| | dest[i] == result[i] }:
    T[] _dest = dest // ghost
    //
    for i in 0 .. length
    // Size of dest unchanged
    where |dest| == |_dest|
    // Everything below destStart unchanged
    where equals(_dest,0,dest,0,destStart)
    // Everything copied so far is equal
    where equals(src, srcStart, dest, destStart, i)
    // Everything above j is unchanged
    where equals(_dest,dest,i+destStart,|dest|):
        dest[i+destStart] = src[i+srcStart]
    //
    return dest
```

This is roughly equivalent to `System.arraycopy()` in Java, and its
not very complicated.  Unfortunately, Boogie / Z3 cannot verify this
without additional help.  Whilst this is potentially something we can
fix in the [Whiley2Boogie](https://github.com/Whiley/Whiley2Boogie)
backend, for now we simply mark the `function` as `unsafe`.  This
means it will be ignored during verification.  However, its
specification is still used when verifying other functions not marked
`unsafe`.  Whilst this is not ideal, it is a pragmatic compromise for
now.

## Conclusion

Using Boogie / Z3 though the
[Whiley2Boogie](https://github.com/Whiley/Whiley2Boogie) backend has
significantly improved our ability to verify non-trivial Whiley
programs.  Work is ongoing here, and you can find a number of
[interesting
benchmarks](https://github.com/Whiley/WyBench/tree/main/src) we are
currently working through.
