---
date: 2010-06-30
title: "Example: Sum Over a Positive List"
draft: false
---

So, here's the first Whiley programming example.  Obviously, it's pretty simple as Whiley is not quite ready for big code just yet.  The idea is to compute the sum of a positive list which, of course, will give you a positive number.  We want Whiley to realise this and correct us if there's a mistake.  So, first, the definition of a positive list:

```whiley
define poslist as [int] where no {x in $ | x < 0}
```

This is fairly straight-forward.  We require a list of integers which contains no element that is below zero.  Now, the main recursive method:

```whiley
int sum(poslist ls, int i) requires i >=0 && i <= |ls|, ensures $ >= 0:
   if(i == |ls|):
     return 0
   else:
     return ls[i] + sum(ls,i+1)
```

This one is a bit more involved.  Firstly, i'm iterating through the list using recursion because Whiley doesn't support a while loop yet (see the [FAQ](http://whiley.org/docs/faq/)).  Second, we can see that the iterator `i` is required to be `>= 0` and `<=` the length of the list on entry.  This may seem slightly strange, since we don't want to access a list past it's bounds ... however, I use the `i==|ls|` as the base case which just returns `0`.  Furthermore, I want to support summing over an empty list.

The key is that the code states explicitly that the return value `$` must be `>= 0` (which we know since we're summing over a positive list).  The beauty of it is that Whiley knows this too, and will pass this code as being correct.  Make any changes, however, such as using a normal list instead of a `poslist`, and it will give you a syntax error.

*So, how does it work?* Well, Whiley begins by generating 4 check statements which are inserted before the final return like so:

```whiley
 ...
 check i>=0, "list index might be negative"
 check i<|ls|, "list index might exceed length"
 check ((i+1>=0) && (i+1<=|ls|)) && (no {x in ls | x<0}), "function precondition not satisfied"
 check ls[i]+sum(ls,i+1)>=0, "function postcondition not satisfied"
 return ls[i] + sum(ls,i+1)
```

We can see that each check is associated with a message, which will be reported if the check fails at compile time.  For each check, Whiley generates a *Verification Condition (VC)* which is an encoding of the check that takes into account what is known from the context.  For example, the verification condition for the first check is:

```
 [int] ls; int i;
 i!=|ls| && 0<=i && i<=|ls| && all [x : ls | 0 <= ls[x]] && i<0
```

Here, we see  `i!=|ls|` (gleaned from if condition), `0<=i && i<|ls|` (gleaned from the pre-condition) and `all [x : ls | 0 <= ls[x]]` (also gleaned from pre-condition).  The final part, `i<0` comes from the check condition itself, but is the inverted version of it.  The reason it is inverted is that we want to check for *unsatisfiability*; that is, if the verification is unsatisfiable (i.e. no assignment of values will make it true) then that implies there is no possible value which could have invalidated the check condition.  This is then passed to the theorem prover which attempts to decide whether it's[ unsatisfiable or not](http://en.wikipedia.org/wiki/Satisfiability_Modulo_Theories).

The above verification condition is easy for the theorem prover to prove as unsatisfiable since it contains `i<=0 && i<0` ... which is clearly impossible.  Just for a taste, here's the VC for the final check:
```
[int] ls; int i,$; int([int],int) &sum;
i!=|ls| && 0<=i && i<=|ls| && all [x : ls | 0 <= ls[x]] &&
 0<=sum(ls,i+1) && 0>ls[i]+sum(ls,i+1)
```

This one is more involved and, to be honest, i'm not going to delve into the details.  If you're interested in learning more about this, then checkout the [Whiley Theorem Prover](https://github.com/Whiley/WhileyTheoremProver/).
