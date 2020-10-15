---
date: 2020-10-15
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

Here, the value of variable `p` has been _voided_ by the move and we cannot use `p` again until we assign it something new.  Yes, this does have a pretty big impact on how we go about writing programs in Rust!  _But, I'm not talking about that here..._

### Partial Moves (Intuition)

### Partial Moves (Code)

### Conclusion

## TODO:
-- What does the book say about them?
