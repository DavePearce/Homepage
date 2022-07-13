---
date: 2016-08-03
title: "Flow Typing with Constrained Types"
draft: false
---

{{<wikip page="Flow-sensitive_typing">}}Flow-sensitive typing{{</wikip>}} (a.k.a. "Flow Typing") is definitely getting more popular these days. {{<wikip page="Ceylon_(programming_language)">}}Ceylon{{</wikip>}}, {{<wikip page="Kotlin_(programming_language)">}}Kotlin{{</wikip>}}, {{<wikip page="TypeScript">}}TypeScript{{</wikip>}}, {{<wikip page="Racket_(programming_language)">}}Racket{{</wikip>}}, {{<wikip page="Whiley_(programming_language)">}}Whiley{{</wikip>}} all support flow typing in some form. Then, of course, there's [Facebook Flow](https://flow.org) and the list goes on!

Recently, I've made some fairly major updates to the internals of the Whiley compiler (basically, redesigning the intermediate language). In doing so, I came across an interesting problem which I wanted to get down on paper. The problem arises when flow typing meets constrained types (a.k.a. types with invariants). *What does a constrained type look like?* Here's an example in Whiley:

```whiley
type nat is (int x) where x >= 0
```

This defines a type `nat` which contain all integer values `x`, where `x >= 0`. Constrained types are quite powerful and Whiley attempts to seamlessly integrate them with flow typing. Here's a simple program to illustrate:

```whiley
function abs(int x) -> (nat r):
    if x >= 0:
        return x
    else:
        return -x
```

The type of `x` is initially `int`. On the true branch, we know `x >= 0` and (roughly speaking) the compiler automatically promotes `x` to type `nat` as needed.
## The Problem
An interesting challenge arises within the compiler when reasoning about constrained types and flow typing. To understand, we need to consider how flow typing works in general. The following gives a rough outline:

```whiley
S x = ...

if x is T:
   ...
else:
   ...
```

Here, `S` and `T` are some arbitrary types where `T` is a subtype of `S`. The compiler *retypes* variable `x` on the true branch to have type `S & T` and, on the false branch, to type `S & !T` (which you can think of as `S - T`). To make it concrete, consider the case for `int|null` and `int`:

```whiley
int|null x = ...

if x is int:
   ...
else:
   ...
```

On the true branch, `x` has type `(int|null)&int` (which reduces to `int`) and, on the false branch, it has type `(int|null)&!int` (which reduces to `null`).

The basic plan outlined above works pretty well, but things get interesting with constrained types. For example, let's use `nat` for the type test instead of `int` above:

```whiley
int|null x = ...

if x is nat:
   ...
else:
   ...
```

The type of `x` on the true branch is `(int|null)&nat`, *but what does this reduce to?* A simple idea is to replace `nat` with its *underlying type* (i.e. `int`). We know this works as it's exactly what we had before. *But, what about the false branch?* Reducing `(int|null)&!nat` in this way gives us `null` as before which, unfortunately, is wrong. The problem is that, on the false branch, `x` can still hold values of `int` type (i.e. *negative* values).

## Mitigating Factors
The Whiley compiler already reasons correctly about flow typing in the presence of arbitrary conditionals. For example, consider this variant on our example from before:

```whiley
int|null x = ...

if x is int && x >= 0:
   ...
else:
   ...
```

In this case, the Whiley compiler will correctly conclude that `x` has type `int|null` on the false branch. *Then why not just expand constrained types like this and be done?* That's a good question. The answer is that, if we expand types in this way, *we lose nominal information about them*. For example, we'd lose the connection between `x` and type `nat` above, as `x`'s type on the either branch would be in terms of `int` and `null` only.

*So, do we really need this nominal information?* The answer is, technically speaking, no we don't.  Expanding types in this way is how the Whiley compiler currently works. But, nominal information helps with providing good error messages and, turns out, that's important!

## The Solution?
My proposed solution stems from ideas currently being used in the Whiley compiler, namely the concept of *maximal* and *minimal* consumption of types. The idea is that the maximal consumption of a type is the largest set of values it could consume. For type `nat`, the maximal consumption is `int`. The minimal consumption is the exact oppostite --- the smallest set of values it must consume. For type `nat`, this is `void` because `nat` does not consume all possible integers. Note that the minimal consumption is not always `void`. For example, the minimal consumption for `null|nat` is `null` because `null` values are *always* consumed.

This probably seems a little confusing right now, but it will start to make sense! The key idea behind my solution is the introduction of two new operators over types, namely `⌈T⌉` (ceiling) and `⌊T⌋` (floor) for representing maximal and minimal consumption for a type `T`. With these, we can now correctly type our program from before *without losing nominal information*:

```whiley
int|null x = ...

if x is nat:
   ...
else:
   ...
```

On the true branch, `x` is given the type `(int|null) & ⌈nat⌉`, whilst on the false branch it's given the type `(int|null) & !⌊nat⌋`. Here, the underlying type for `(int|null) & ⌈nat⌉` is `int`, whilst for `(int|null) & !⌊nat⌋` it's `int|null`.

The point of these new operators is that they allow us to delay calculating the underlying type for `x` *until we need it*. In other words, they allow us to retain nominal information for as long as possible.

## Observations

These two operators are interesting and it turns out there are few observations we can make about them:

   * For any **primitive type** `T`, we have that `T` is equivalent to both `⌊T⌋` and `⌈T⌉`.

   * For any **negation type** `!T`, we have that `⌊!T⌋` is equivalent to  `!⌈T⌉` and `⌈!T⌉` is equivalent to  `!⌊T⌋`.

   * For any **union type** `T1 || T2`, we have that `⌊T1 || T2⌋` is equivalent to `⌊T1⌋ || ⌊T2⌋`, whilst `⌈T1 || T2⌉` is equivalent to `⌈T1⌉ || ⌈T2⌉`.

   * For any **intersection type** `T1 && T2`, we have that `⌊T1 && T2⌋` is equivalent to `⌊T1⌋ && ⌊T2⌋`, whilst `⌈T1 && T2⌉` is equivalent to `⌈T1⌉ || ⌈T2⌉`.

   * For any **nominal type** `N` declared as `T where e`, we have that `⌊N⌋` is equivalent to `void` and `⌈T⌉` is equivalent to `T`.


## Conclusion

Using these two new operators provides a simple way to reason about flow typing over constrained types.  The next job for me is to implement this within the Whiley compiler!

## References

Here's an interesting paper on constrained types:

   * **Constrained types for object-oriented languages**, Nathaniel Nystrom, Vijay Saraswat, Jens Palsberg, Christian Grothoff. In *Proceedings of OOPSLA*, 2008. ([LINK](http://dl.acm.org/citation.cfm?id=1449800))
