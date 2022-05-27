---
date: 2022-05-27
title: "Type Checking in Whiley goes Both Ways!"
draft: true
#metaimg: "images/2022/AuctionContract_Preview.png"
#metatxt: "Verifying a smart contract in Whiley finds lots of problems."
#twitter: ""
# reddit: "https://www.reddit.com/r/rust/comments/uigljf/puzzling_strong_updates_in_rust/"
---

Type checking in Whiley is a curious thing as it goes both _forwards_
and _backwards_.  This is sometimes referred to as bidirectional type
checking (see [here](https://arxiv.org/abs/1908.05839) and
[here](https://ncatlab.org/nlab/show/bidirectional+typechecking)).
This is actually really useful in Whiley, given its unusual feature
set.

## Backwards Typing

Type checkers normally work in what I refer to as the "backwards"
direction.  That is, we start from leaves of the [abstract syntax
tree](https://en.wikipedia.org/wiki/Abstract_syntax_tree) and work
upwards.  So, typing a statement like `xs[i] = ys[i] + 1` when `xs`
and `ys` both have type `int[]` might look something like this:

{{<img class="text-center" src="/images/2022/BidirectionalTypeChecking.png" width="40%" alt="Illustrating the layout of an array in Java.">}}

They key here is that everything has to "line up" properly, otherwise
we have a type error.

### Limitations

As a general approach, backwards typing works pretty well in most
cases.  But, there are a number of problems (many of which are
specific to Whiley):

   * *(Sizing)*.  Variables of type `int` in Whiley can hold arbitrary
      sized integers and, because of this, backwards typing can lead
      to inefficiency.  Consider this:
      
      ```Whiley
      u8 x = 124
      ```
      
      Under the backwards typing scheme, the constant `124` is given
      type `int`.  That means, under-the-hood when the constant is
      created, we'll allocate space for an arbitrary sized integer and
      give it the value `124`.  Then, we'll immediately coerce from
      that to `u8` causing a deallocation.  It would be much better if
      we automatically determined the type of `124` was `u8`, and not
      `int`.  To resolve this, many languages provide notation for
      specifying the type of a constant (e.g. `124u8` in Rust).

   * *(Overloading)*.  Whiley, like many languages, provides the
      ability to
      [overload](https://en.wikipedia.org/wiki/Function_overloading)
      functions.  Backwards typing supports this on _parameter types_
      but not on _return types_.  Whilst it may seem esoteric, the
      ability to overload on return types can be useful.  For example,
      you might want to do something analoguous to the [default
      trait](https://doc.rust-lang.org/std/default/trait.Default.html)
      in Rust:
      
      ```Whiley
      function default() -> int[]:
         return []

      function default() -> int:
         return 0
      ```

      This is valid Whiley and the compiler will automatically
      select the right method based on context.

   * *(Templates)*.  Example with unused template?  Or requires
      complex subtyping based on an invariant?

   * *(Runtime Type Checking)*.

## Forwards Typing

## Notes

   * Type invariants.  It could be inefficient to check them at runtime.
   * Corecions.  Low down or high up?
   ```Whiley
   nat[] ys = ...
   nat[] xs = ys ++ [1,2,3]
   ```
   * Templates.