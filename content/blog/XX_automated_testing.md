---
date: 2020-12-02
title: "Automated Testing for Whiley"
draft: true
#metaimg: ""
#metatxt: ""
#twitterimgalt: ""
twittersite: "@whileydave"
#twitter: "https://twitter.com/whileydave/status/1333545363165175809"
#reddit: "https://www.reddit.com/r/rust/comments/k47rr0/understanding_partial_moves_in_rust/"
---

Recently, the [online editor for Whiley](http://whileylabs.com) was updated with some new features.  Actually, the update represents a _complete rewrite of the front-end in Whiley_.  Obviously, I am very excited about that!  Previously it was written using raw (i.e. ugly) JavaScript, but now uses a framework for [Functional Reactive Programming](https://www.youtube.com/watch?v=yYGEcyCHiZk) (called [Web.wy](https://github.com/DavePearce/Web.wy)).  That has made a huge difference to how the code looks.  Still, that is not what I am going to talk about here.  Rather, it is the new _check_ feature (highlighted in red below) that I'm interested in here:

{{<img class="text-center" link="http://whileylabs.com" src="/images/2020/AutomatedTesting_WhileyLabs.png" width="60%" alt="Illustrating a screenshot of whileylabs.com with the check option highlighted.">}}

_What is this new "check" feature then?_ In a nutshell, it automatically generates test inputs for Whiley functions and runs them through the code looking for problems (e.g. _divide-by-zero_, _index-out-of-bounds_, etc).  In many ways, it is similar to the [QuickCheck](https://en.wikipedia.org/wiki/QuickCheck) line of tools (with the added benefit that Whiley has first-class specifications).  We can think of it as a "half-way" step towards formal verification.  The key is that it is easier to check a program than it is to statically verify it (more on this below).  A lot of folks might think that having a check feature like this doesn't make sense when you also have static verification.  But, I prefer to think of them as _complementary_.  Thinking about a developer's workflow, we might imagine checking a function first as we develop it (since this is easier and quicker) before, finally, attempting to formally verify it (since this is harder and may force the specification to be further developed).  

## Example

As a simple example to illustrate how it works, consider the following
signature for the `max(int[])` function:

```whiley
function max(int[] items) -> (int r)
// items cannot be empty, otherwise there is no max!
requires |items| > 0
// result cannot be smaller than any element in items
ensures all { i in 0..|items| | r >= items[i] }
// result must match at least one element of items
ensures some { i in 0..|items| | r == items[i] }:
   ...
```

We're not really concerned with the implementation here and, for
example, it might have bugs.  To check this function, the tool will do
the following:

   1. **(Generation)** The tool generates some number of valid inputs.
   That is, input values which meet the precondition.  For our example
   above, that is any `int` array containing at least one element.

   2. **(Execution)**. The function in question is then executed using
   each of the generated inputs.  This may lead to obvious failures
   (e.g. out-of-bounds or divide-by-zero errors), in which case we've
   found some bugs.

   3. **(Checking)**. For any execution which completed, the tool then
   checks the result against the postcondition.  If the postcondition
   doesn't hold then, again, this indicates a bug somewhere.

The key here that the specification acts as the [test
oracle](https://en.wikipedia.org/wiki/Test_oracle).  In other words,
_having written the specification we get testing for free_!  And, the
tool takes care of the technical challenges so we don't have to.  For
example, generating valid inputs efficiently is harder than it looks
(more on this below).

## Pros / Cons

   * Incomplete specifications
   * No need for loop invariants
   * No need to additionally provide properties as for QuickCheck

   * Problems generating enough inputs.  Could look at array example
     from scc benchmark.

## Technical Stuff

   * How to generate inputs
