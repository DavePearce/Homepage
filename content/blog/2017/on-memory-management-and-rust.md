---
date: 2017-02-15
title: "On Memory Management and Rust"
draft: false
---

Rust is definitely one of the more interesting new programming language I've come across recently. Memory management is definitely Rust's "thing", and the language wants to have its cake and eat it (so to speak). That is, Rust wants *safe* memory management *without* garbage collection. This is no easy task, and Rust delivers a modern solution which (it seems to me) is heavily inspired by smart pointers in C++.

In this post, I'm going talk about how Rust addresses the memory management problem. But, I'm also want to think about other ways we could solve this problem and, in particular, talk about a rather obscure programming language called [Cyclone](https://en.wikipedia.org/wiki/Cyclone_(programming_language)) (which takes a somewhat different approach to solving the same problem).

## Background
The question of how to safely manage memory has been an ongoing challenge for programming language designers.  There are essentially just two approaches in wide spread use:  on the one hand, we have languages with *manual* memory management (e.g. C/C++) and, on the other, we have languages with *[garbage collection](https://en.wikipedia.org/wiki/Garbage_collection_(computer_science))* (e.g. Java/C#/Haskell/Erlang).  By now, the issues with manual memory management are well known and, for example, are (still) a major source of software vulnerabilities.  Garbage collection, in contrast, has proved to be an excellent solution, and significant research has been directed into efficient algorithms.  In some sense, garbage collection has been a real success story for computer science.

Systems programming languages avoid garbage collection for various reasons (e.g. to run on bare metal).  The problem is there's no middle ground between manual memory management and garbage collection.  *Actually, that's not completely true!*  C++ has forged an interesting approach over the years through  [*smart pointers*](https://en.wikipedia.org/wiki/Smart_pointer).  The advent of [C++11](https://en.wikipedia.org/wiki/C%2B%2B11) brought a bunch of great language updates, especially (from our perspective) support for *move semantics*.  This allowed smart pointer to evolve and become more sophisticated, leading to `auto_ptr` being retired and replaced with `unique_ptr`.  Overall, smart pointers were (it seems to me) a big influence on the design of Rust...
### Rust
In essence, Rust bakes smart pointers into the language and provides proper support for checking them through the so-called *borrow checker*.  It's easy to get confused with all the Rust "lingo" but (for me at least) viewing it through the C++ lens helped a lot.  Here's an example in Rust:
```Rust
fn dup(x: Vec<i32>) -> (Vec<i32>,Vec<i32>) { 
    let y = x;
    return (x, y); 
}
```
This program [does not compile](https://play.rust-lang.org/?gist=b813c1ec0196f5a17af85f0bc3b86690&version=stable&backtrace=0), and produces the (somewhat) useful error message:
```
error[E0382]: use of moved value: `x`
  |
2 |     let y = x;
  |         - value moved here
3 |     return (x, y); 
  |             ^ value used here after move
```
(actually, the error message is pretty good *once* you get the hang of borrowing in Rust)

We can view this through the C++11 lens as something like this:
```cpp
#include <utility>
#include <memory>
#include <vector>

typedef std::unique_ptr<std::vector<int> > Vec;

std::pair<Vec,Vec> dup(Vec x) {
  Vec y = std::move(x);
  return make_pair(std::move(x),std::move(y));
}
```
This is actually a pretty fair representation of the original Rust program. But, there's one important difference: *it compiles!* According to the standard, variable `x` is in a valid but *unspecified* state after the move (see e.g. [here](http://stackoverflow.com/questions/20850196/what-lasts-after-using-stdmove-c11) and [here](http://stackoverflow.com/questions/9168823/reusing-a-moved-container)).

At this point, I'm not going say much more about Rust.  Don't take this to mean there isn't anything else to say *as that's definitely not true!* And, my simple way of looking at Rust through a "C++ lens" is just that --- a simplification.  Nevertheless, it provides a useful way to think about Rust, and to compare with other similar approaches ...

## Cyclone
The [Cyclone language](https://en.wikipedia.org/wiki/Cyclone_(programming_language)) was an attempt to add memory safety to C. In a way, many of its objectives were similar to those of Rust. In fact, Cyclone is highlighted as [one of the influences](https://doc.rust-lang.org/reference.html#appendix-influences) on Rust.

Cyclone adopts the [region-based approach to memory management](https://en.wikipedia.org/wiki/Region-based_memory_management). In this approach, data is allocated into *regions* and references are annotated to identify the region in which the data they refer to resides. The following is the canonical illustration which reports a compile-time error:
```c
int f() {
  int x = 0;
  int *@region(`f) y = &x;
  L:{
      int a = 0;
      y = &a;
   }
   return *y;
}
```
Here, variable `y` is a pointer into the region automatically associated with function `f()`, whilst variable `a` is declared in region `L`. Thus, the assignment `y=&a` is not permitted as, if allowed, it would lead to a dangling pointer.

For Rustaceans, the above example should be somewhat familiar. Simplistically, we can say that regions are like lifetimes in Rust. Certainly, they exhibit many similar properties. But, they are also subtly different:
   * **Regions do not impose ownership**.  There is no problem with multiple mutable pointers to the same heap location, provided both pointers are appropriately typed.  Amongst other things, this means cyclic structures within a region are permitted.  However, it also means this mechanism cannot be used for managing shared resources (in fact, Cyclone has a separate notion of `unique` pointer for this).

   * **All pointers are associated with regions**.  This may seem an odd thing to say.  But, in Rust, this is not actually the case.  That is, in Rust, all *borrowed references* are associated with a lifetime (either explicitly or implicitly).  However, owned references are not.  This means we can return a `Vec` from a function and it can migrate throughout our program as we see fit.  In Cyclone, however, heap objects are tied to a specific region and *cannot outlive that region*.



My goal here is not to suggest that one approach is better than the other.  Only to highlight the interesting differences.  It seems to me that Cyclone's approach offers more flexibility in terms of the structures it can represent but, at the same time, is perhaps more "relaxed" about deallocation of memory (i.e. because you must wait for an entire region to be deallocated).  Rust is quite strict, on the otherhand, but as a result can be more aggressive in deallocating memory.  Rust also perhaps hides users from memory management to some extent, as such details are often hidden behind abstract data types (like `Vec`).