---
date: 2020-12-02
title: "Automated Testing for Whiley"
draft: false
metaimg: "images/2020/QuickCheckDemo.jpg"
metatxt: "New functionality for automated testing is available on whileylabs.com.  This generates test inputs for functions and runs them through the code looking for problems (e.g. divide-by-zero, index-out-of-bounds, etc). In many ways, it is similar to the QuickCheck line of tools (with the added benefit that Whiley has first-class specifications)."
twitterimgalt: "Image of Editing Code in WhileyLabs"
twittersite: "@whileydave"
twitter: "https://twitter.com/whileydave/status/1341215592733274112"
reddit: "https://www.reddit.com/r/programming/comments/khwdyb/automated_testing_for_free/"
---

Recently, the [online editor for Whiley](http://whileylabs.com) was updated with some new features.  Actually, the update represents a _complete rewrite of the front-end in Whiley_.  Obviously, I am very excited about that!  Previously it was written using raw (i.e. ugly) JavaScript, but now uses a framework for [Functional Reactive Programming](https://www.youtube.com/watch?v=yYGEcyCHiZk) (called [Web.wy](https://github.com/DavePearce/Web.wy)).  That has made a huge difference to how the code looks.  Still, I'm not going to talk about that here.  Rather, it is the new _check_ feature (highlighted in red below) that I'm interested in:

{{<img class="text-center" width="80%" link="http://whileylabs.com" src="/images/2020/AutomatedTesting_WhileyLabs.png" alt="Illustrating a screenshot of whileylabs.com with the check option highlighted.">}}

_What is this new "check" feature then?_ In a nutshell, it automatically generates test inputs for functions and runs them through the code looking for problems (e.g. _divide-by-zero_, _index-out-of-bounds_, etc).  In many ways, it is similar to the [QuickCheck](https://en.wikipedia.org/wiki/QuickCheck) line of tools (with the added benefit that Whiley has first-class specifications).  We can think of it as a "half-way" step towards formal verification.  The key is that it is easier to check a program than it is to statically verify it (more on this below).  Some might think that having a check feature like this doesn't make sense when you also have static verification.  But, I prefer to think of them as _complementary_.  Thinking about a developer's workflow, we might imagine checking a function first as we develop it (since this is easier and quicker) before, finally, attempting to statically verify it (since this is harder and may force the specification to be further refined).  

## Example

As a simple example to illustrate how it works, consider the following
signature for the `max(int[])` function:

```c
function max(int[] items) -> (int r)
// items cannot be empty
requires |items| > 0
// result not smaller than any in items
ensures all { i in 0..|items| | r >= items[i] }
// result is element in items
ensures some { i in 0..|items| | r == items[i] }:
   ...
```

We're not really concerned with the implementation here (and we can
assume it might have bugs).  To check this function, the tool will do
the following:

   1. **(Generate)** The tool generates some number of valid inputs.
   That is, input values which meet the precondition.  For our example
   above, that is any `int[]` array containing at least one element.

   2. **(Execute)**. The function in question is then executed using
   each of the generated inputs.  This may lead to obvious failures
   (e.g. out-of-bounds or divide-by-zero errors), in which case we've
   already found some bugs!

   3. **(Check)**. For any execution which completed, the tool then
   checks the result against the postcondition.  If the postcondition
   doesn't hold then, again, this indicates a bug somewhere.

The key is that the specification acts as the [test
oracle](https://en.wikipedia.org/wiki/Test_oracle).  In other words,
_having written the specification we get testing for free_!  And, the
tool takes care of the difficult stuff so we don't have to.  For
example, generating valid inputs efficiently is harder than it looks
(more on this below).

## Technical Stuff

The main technical challenge is that of efficiently generating _input
values under constraints_.  To start with, we can generate raw input
values for the various data types in Whiley:

   * **Primitives.** These are pretty easy.  For example, to generate
       values of type `int`, we'll use some (configurable) domain
       (e.g. `-2 .. 2`) of values.  We can easily sample uniformly
       from this domain as well (e.g. using Knuth's [Algorithm
       S](https://rosettacode.org/wiki/Knuth%27s_algorithm_S)).

   * **Arrays**.  Here, we limit the maximum length of an array
       (e.g. length `2`), and then enumerate all arrays upto this
       length.  To generate values in the array, we recursively call
       the generator associated with the element type.  For the array
       type `int[]` and assuming max length `2` and integers `-1..1`,
       we might generate: `[]`, `[-1]`, `[0]`, `[1]`, `[-1,-1]`,
       `[0,-1]`,`[1,-2]`, etc.

   * **Records**.  These are similar, except there is no maximum
       length.  For each field, we recursively generate values using
       the generator associated with its type.  For the record type
       `{bool flag, int data}` and assuming and integers `-1..1`, we
       might generate: `{flag:false,data:-1}`, `{flag:true,data:-1}`,
       `{flag:false,data:0}`, `{flag:true,data:0}`,
       `{flag:false,data:1}`, etc.

   * **References**.  These are more tricky as we must allow for
       aliasing between heap locations.  For a single reference type
       `&int`, we generate heap locations for each of the possible
       integer values.  However, for two reference types
       (e.g. parameters `&int x, &int y`), we have to additionally
       consider both the _aliased_ and _non-aliased_ cases.

   * **Lambdas**.  These are very challenging as, in principle, it
       requires enumerating all implementations of a function!
       Consider the lambda type `function(int)->(int)` --- *how do we
       generate values (i.e. implementations) of this type?* There is
       no easy answer here, but there are some strategies we can use.
       Firstly, we can scour the source program looking for functions
       with the same signature.  Secondly, we can generate simple
       input/output mappings (e.g. `{-1=>-1, 0=>0, 1=>1}`).  We can go
       beyond this by applying _rotations_ (e.g.  `{-1=>0, 0=>1,
       1=>-1}`).

Given the ability to generate arbitrary values for a type, the more
difficult question is how to generate values which meet some
constraint.  For example, consider this data type:

```c
type List<T> is {
     int length,
     T[] items
}
where 0 <= length
where length <= |items|
```

The current approach we take is rather simplistic here.  We simply
generate all values of the _underlying type_ and discard those which
don't satisfy the invariant.  This works, but it has some problems:

   1. *(Scale)*.  Enumerating all values of a complex data type upto a
      given bound can be expensive.  We might easily need to generate
      _1K_, _10K_, _100K_ (or more) values, of which only a tiny
      fraction may satisfy our invariant.  For our `List` example
      above, assuming a maximum array length of `3` and integer range
      `-3..3`, there are _2800_ possible values of the underlying type
      of which only _1534_ meet the invariant (55%).  That's not too
      bad, but as we get more invariants this changes quite quickly.
      Consider this example taken from an implementation of [Tarjan's
      algorithm](https://en.wikipedia.org/wiki/Tarjan%27s_strongly_connected_components_algorithm)
      for computing _strongly connected compoenents_:
      ```c
      type Data is {
	    bool[] visited,
	    int[] lowlink,
	    int[] index
      }
      where |visited| == |lowlink|
      where |lowlink| == |index|
      ```
      In this case, there are
      _2.4M_ values of the underlying type, of which only _946K_ match
      the invariant (39%).  And, as we add more invariants (e.g. that
      no element in `lowlink` can be negative) this ratio continues a
      downward trend.

   2. *(Sampling)*.  Another interesting issue arises with sampling.
      Typically, we want to sample from large domains (i.e. rather
      than enumerate them) to allow checking in reasonable time.  The
      problem is that, for complex data types with invariants, the
      probability that a given value sampled from the underlying
      domain meets the invariant is often very low.  We have observed
      situations where sampling _1K_ or _10K_ values from such a
      domain produced _exactly zero_ values meeting the invariant.  In other
      words, it was completely useless in these cases (though not in others).

Whilst these may seem like serious problems, they stem from the
simplistic fashion in which we generate values for constrained types.
Actually, it is possible to generate values of constrained types directly
(i.e. without enumerating those of the underlying type) and
efficiently.  The next evolution of the check functionaliy will implement
this and, hopefully, it will offer some big improvements.


## Pros / Cons

There are several advantages to checking over static verification,
some of which may be unexpected:

   * *(Incomplete Specifications)*.  We can check our programs even
      when the specifications are incomplete.  Suppose we are
      developing two functions in tandem, say `max(int[])` which uses
      `max(int,int)` as a subroutine.  With static verification, we
      cannot verify `max(int[])` before we have specified and verified
      `max(int,int)`.  However, we can start checking `max(int[])` as
      soon as we have implementated `max(int,int)` (i.e. since
      checking just executes it).
      
   * *(Loop Invariants)*.  We can check our programs without writing
      any loop invariants.  Since writing loop invariants can be
      challenging, this is an important benefit.  With static
      verification, we cannot verify a function containing a loop
      without correctly specifying the loop invariant first.  That can
      be a real pain, especially in the early stages of developing a
      program.

   * *(Counterexamples)*.  Whenever checking finds a problem in our
      program, it will also tell you the input values causing it and,
      furthermore, they always correspond to real failures in your
      program.  Static verification, on the other hand, cannot always
      generate an execution trace and, even when it does, they are not
      always _realisable_ traces of the program.  That can make life
      quite frustrating!

Of course, there are some disadvantages with checking as well.  Most
importantly, checking doesn't guarantee to find all problems with your
program!  But, in our experience, it usually finds most of them.
There are also problems (discussed above) with generating enough
values for complex data types to properly test our functions.  

### Conclusion

Overall, despite some limitations, we find checking to be incredibly
useful --- _especially as its free_.  Here's a short demo to give you
a taste:

{{<youtube id="d_liFzxlpjA" width="560" height="315">}}

So, head on over to the [online editor for
Whiley](http://whileylabs.com) and give it a go!