---
date: 2011-01-26
title: "The Case Against Structural Subtyping ... ?"
draft: false
---

My previous post on [structural subtyping](/2010/12/13/why-not-use-structural-subtyping) generated quite a few [comments](http://www.reddit.com/r/programming/comments/ekmtf/why_dont_more_languages_use_structural_subtyping/) over on [reddit](http://reddit.com).  There were a lot of mixed opinions as to the pros and cons of having a {{<wikip page="Structural_type_system">}}structural type system{{</wikip>}} instead of a {{<wikip page="Nominal_typing">}}nominal type system{{</wikip>}}. To help me digest and ponder it all, I thought I'd discuss the main themes here in more detail.

## Issues

I'll start by looking at the various issues people highlighted with structural subtyping, and provide a few comments of my own later.

   * **Programmer Intent**.  Perhaps the biggest problem raised with structural subtyping is that programmer intent may be lost.  In a nominal type system, the names of types captures their intent (to some extent), and the system prevents information flows that don't make sense. In Java, one might create a class `Length` with an `int amount` field, which holds the length in centimeters.  In a nominal type system, an instance of a different class (say `Time`) which happens to have an `int amount` field as well, cannot flow into a variable of type `Length`.  Thus, one cannot accidentally mix up lengths and times.  With a structural subtyping system, this is not true because an instance of a structure with a field `int amount` can be used interchangeably as a `Length` or a `Time`.

   * **Invariants**.  Another important problem is that structural subtyping makes it harder to enforce invariants over data structures.  Suppose we're implementing a `Date` class in a language with nominal typing, like Java.  We might have fields `day`, `month` and `year` which work as expected.  There are several  invariants amongst these fields, such as `1 <= day <= 31` and, `1 <= month <= 12` (and these can obviously be refined, e.g. `1 <= day <= 28 if month == 2`).  In a language like Java, we can easily ensure these invariants are enforced by making the fields private and adding specific getters, and setters which specifically check against invalid values.  In a structural subtyping system, it's not clear exactly how one would enforce such invariants and still retain the advantages of structural typing.  We can provide some kind of data-hiding mechanism in the language to ensure access to fields is controlled --- but this rather defeats the purpose of structural typing as objects are no longer easily interchanged.
   
   * **Performance**.  Another cited issue with structural subtyping is that it may incur a performance hit.  The argument is that, if you cannot determine the static offsets of all fields in a structure, then you are forced to employ some kind of dictionary (i.e. {{<wikip page="Hash_table">}}hash table{{</wikip>}}) lookup on every field access.   See [my earlier post](/2011/01/14/one-approach-to-efficient-structural-subtyping/) for more on this problem.  **Note**, this problem is similar, for example, to that of implementing Java interfaces efficiently (see e.g. [this](http://domino.research.ibm.com/comm/research_people.nsf/pages/dgrove.hpcn01.html)).

   * **Error Messages**.  Generating error messages in a structural type system is something of a challenge.  This is because, in general, you can only report the entire structure involved, rather than just report its name (since structures have no name).
   
## Comments

In my opinion, many of the issues raised above can be adequately resolved with a little bit of care and thought.  Let's consider the easy ones first:

   * **Programmer Intent.** Whilst I agree this is an issue, units of measure in languages like Java are often passed around simply as `int`s anyway;  also, we can protect ourselves by using more meaningful field names (e.g. `amountInCms`, instead of `amount`) or even by using {{<wikip page="Type_system#Existential_types">}}existential types{{</wikip>}} in  situations where we are concerned about potential mix ups (see <a href="http://www.cs.utexas.edu/%7Ewcook/Drafts/2009/essay.pdf">this paper</a> for more).

   * **Performance**.  Whilst there may be some performance hit, it is likely to be negligible for a well engineered language.  In particular, the approach discussed in [this post](/2011/01/14/one-approach-to-efficient-structural-subtyping/) will go quite a way towards minimising overhead.

   * **Error Messages**.  Whilst I have encountered this problem
     myself during development of Whiley, I don't think it is hard to
     fix.  My current solution is to retain nominal information from
     `define` statements, and use that purely for error reporting. 
     There are some problematic issues here, however.  For example:
     ```whiley
     define T1 as int
     define T2 as int
     
     int f(T1 x, T2 y):
       if x > y:
         z = x
       else:
         z = y
       // what nominal info to retain here?
       return z
     ```
     The problem here is that we want to retain some nominal type information for the variable `z` --- either `T1` or `T2`.  After the `if` statement, we must either choose one name to retain, or retain both using some kind of union.
   A similar issue is that, when variables are assigned raw values there is no possible nominal information we can retain.  However, in such circumstances, it's unlikely that a particularly complex structure is being assigned --- meaning the type will be fairly simple anyway,

Now, the issue of maintaining invariants in a structural subtyping system appears (to me at least) to be the hardest of all.  Here's my take on it:

   * **Invariants**.  One obvious approach here is to use some kind of {{<wikip page="Type_system#Existential_types">}}existential type{{</wikip>}} to implement information hiding  (again, see [this paper](http://www.cs.utexas.edu/%7Ewcook/Drafts/2009/essay.pdf) for more on this).  What this does, is to provide a mechanism whereby we can hide the fields for part or all a record.  This means the fields require getters and setters, and invariants can be enforced through them (i.e. in exactly the same way as for a nominal type system; indeed, the only real advantage of using existential types over nominal types here is that we can expose some parts of a record and they will then be structurally subtyped).  
    
*Neither of these two solutions are really satisfying for me!*  Now, all of this discussion (from my perspective at least) is in the context of the [Whiley](http://whiley.org) language.  The aim of this language is to make invariants first-class entities which are checked at compile-time by the compiler.  In such a setting, the invariants can be explicitly written as part of the structural type, thereby eliminating this problem altogether!  For example, with the `Date` class from before, we might have: 

```whiley
define Date as { int day,int month,int year } where
 0<=day && day<=31 && 0<=month && month<=12 && ...
```

The beauty of this, is that we can now only interchange `Date`s with structures that have suitable invariants as well.  However, *the invariants need not match exactly*.  For example:

```whiley
// a date with no invariant
define DumbDate as { int day, int month, int year }
// a date in Februrary
define FebDate as Date where $.month == 2

DumbDate f(Date x):
    return x

DumbDate g(FebDate y):
    return f(y)
```

Here, we see that records can flow into variables requiring structural subtypes with invariants *which are no stricter*.  This gives an interesting advantage over the nominal type solution to this problem... 

Righto, that's enough thinking for now!!
