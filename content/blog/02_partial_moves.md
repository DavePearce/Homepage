---
date: 2020-11-30
title: "Understanding Partial Moves in Rust"
draft: true
#metaimg: "images/2020/semver.png"
#metatxt: "Semantic versioning is a low fidelity communication channel.  However, tooling could be used to improve this situation, such as through static analysis"
#twitterimgalt: "Image showing example dependencies with versions in a build file."
twittersite: "@whileydave"
---

Recently I've been digging into [Rust](https://www.rust-lang.org/) and, whilst it's a great language on many fronts, I do find lots of hidden complexity.  One example which doesn't get much attention is _partial moves_.  So, I thought, _why not write an introduction?_

### Ownership (Briefly)

I'm not going to cover all the details of [ownership and borrowing](https://doc.rust-lang.org/book/ch04-00-understanding-ownership.html) in Rust here.  Still, we need some background for partial moves to make sense. So, here is a _box and arrow_ (i.e. simplified) perspective of ownership in Rust!

In an imperative world without ownership (think Java, C/C++) we are generally allowed references to (e.g. heap) data without any restrictions around aliasing.  We can have two references pointing to the same data, references which point to each other, and so on:

{{<img class="text-center" src="/images/2020/PartialMoves_Aliasing.png" height="150px" alt="Illustrating different examples of aliasing between references.">}}

In a world with ownership (i.e. Rust) a reference can now _own_ the data to which it refers.  In such case, no other owning references of that data are permitted (roughly speaking).  For example, if two references refer to the same thing (i.e. the middle diagram above) only one can be the owner.

The restrictions on the owning references impact on how we write programs.  Suppose we tried to _copy_ an owning reference from one variable `p` to another variable `q`:

{{<img class="text-center" src="/images/2020/PartialMoves_OwnerCopy.png" height="175px" alt="Illustrating owning reference being copied to another variable.">}}

This doesn't make sense because it breaks the _ownership invariant_.  If we allowed it, we would have two owning references to the same thing which is not permitted.  So, what can we do?  _We can move it instead_:

{{<img class="text-center" src="/images/2020/PartialMoves_OwnerMove.png" height="175px" alt="Illustrating owning reference being moved to another variable.">}}

Here, the value of variable `p` has been _voided_ by the move and we cannot use `p` again until we assign it something new.  Yes, this does have a pretty big impact on how we go about writing programs in Rust!  _But, I'm not talking about that here._

### Partial Moves

Thus far, we've looked at moving an entire variable at a time (e.g. from `p` to `q` above).  However, we can also perform a *partial move* whereby only part of a given variable is moved.  Suppose now that our variable `p` is actually a pair where each element contains an owning reference.  Then, we can move the second element of `p` into some other variable `q` as follows:

{{<img class="text-center" src="/images/2020/PartialMoves_PartialMove.png" height="180px" alt="Illustrating owning reference in struct being moved to another variable.">}}

What is interesting about this case is that, unlike before, variable `p` can still be used in a limited way *even though part of it has been voided*.  Specifically, we can use `p.0` but we cannot use `p.1`.  Furthermore, Rust prevents us from copying or moving variable `p` as a whole (though, to my mind, that seems somewhat unnecessary).  Putting the above into code looks like this:

```rust
fn main() { 
    let mut x = 123;
    let mut y = 456;
    let mut p = (&mut x,&mut y);
    let mut q = p.1;
    ...
}
```

At this point, everything is fine.  However, replacing the `...` with
e.g. `let mut z = p;` and we get the following error message:

```
error[E0382]: use of partially moved value: `p`
 --> src/main.rs:6:17
  |
5 | let mut q = p.1;
  |             --- value partially moved here
6 | let mut z = p;
  |             ^ value used here after partial move
```

This is simply telling us that we cannot use a value which has been
voided by some previous move.  Personally, I don't see why Rust
prevents moves like this, since it could easily reason that `z` is
only partially defined in the same way that it already does for `p`.
Presumably, though, assigning `p` indirectly through some reference
would be problematic regardless.

### Conclusion

Rust is a pretty awesome language, but there is still a lot of subtle
features.  Hopefully this helps explains one piece of the puzzle!
