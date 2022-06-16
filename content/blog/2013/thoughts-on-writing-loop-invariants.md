---
date: 2013-11-19
title: "Thoughts on Writing Loop Invariants"
draft: false
---

As the Whiley system is taking better shape every day, I'm starting to play around more and discover things.  In particular, there are some surprising issues surrounding `while` loops and their [loop invariants](http://en.wikipedia.org/wiki/Loop_invariant). These are things which I'll need to work on in the future if Whiley is to stand any chance of being widely used. So, I thought I'd give a run down of the observations I've made so far ...

When thinking about loops, the problem of writing *loop invariants* always comes up. These can be notoriously tricky to get right, and can easily stop people in their tracks. Let's look at a very simple example to start with:

```whiley
int f(int n) ensures $ == n:
   i = 0
   while i < n:
      i = i + 1
   return i
```

This function accepts an integer `n` and must return a value which is equal to `n` (here, the return value is given by `$`).  Ok, yes this is an artificial example, but it helps to explain loop invariants.  The thing about this example is that it *won't verify yet*. Here's what you see in the Whiley Eclipse plugin:

[{{<img class="text-center" src="http://whiley.org/wp-content/uploads/2013/11/Wyclipse_Screenshot_1.png">}}](http://whiley.org/wp-content/uploads/2013/11/Wyclipse_Screenshot_1.png)
Here, the Whiley compiler is saying the function's [post-condition](http://en.wikipedia.org/wiki/Postcondition) is not satisfied.  When we look at this function, we can easily think it's correct --- *but it's not*.  To uncover the problem, we  need a loop invariant.  Every loop invariant has three requirements:
   * **Initialised on Entry**.  The loop invariant must hold on entry to the loop.

   * **Preserved by the Body**.  The loop invariant must hold after the loop body, assuming it held immediately before.

   * **Valid on Exit.**  When the loop finishes, the loop invariant must still hold.


We can assume (for now) that the only information known after a loop is the loop invariant and the negated loop condition.  Since the above example doesn't have a loop invariant, all we know after the loop is that `i >= n` --- which is not enough to satisfy the post-condition. Therefore, we'll add a loop invariant as follows:

[{{<img class="text-center" src="http://whiley.org/wp-content/uploads/2013/11/Wyclipse_Screenshot_2.png">}}](http://whiley.org/wp-content/uploads/2013/11/Wyclipse_Screenshot_2.png)
Here, the loop invariant is given by the clause `where i <= n`.  Also, we now have a different error message telling us that the *loop invariant didn't hold on entry*.  Well, now we can see the problem with this function --- namely, that `n < i` might hold on entry to the loop (e.g. if `n == -1` on entry to the function).

We can go ahead and fix this now:

[{{<img class="text-center" src="http://whiley.org/wp-content/uploads/2013/11/wyclipse_screenshot_3.png">}}](http://whiley.org/wp-content/uploads/2013/11/wyclipse_screenshot_3.png)
Since there are no further error messages, the function has been verified as correct. However, it's worth taking a moment to think about *why* its correct. The last item in our list of requirements for loop invariants is that they must be *valid on exit*. Therefore, after the loop finishes we know that both the loop invariant and negated condition hold. In this case, that means `i <= n` and `i >= n` hold which implies `i == n`.
## Some Theory
At this point, we can delve a little deeper into the theory behind loop invariants as this uncovers some interesting issues.

The foundation for verification systems like the Whiley Compiler is [Hoare logic](http://en.wikipedia.org/wiki/Hoare_logic).  This is based around so-called *Hoare triples* which, at a high level, look like this:

[{{<img class="text-center" src="http://whiley.org/wp-content/uploads/2013/11/HoareTriple.png">}}](http://whiley.org/wp-content/uploads/2013/11/HoareTriple.png)
Here, `s` is the statement being considered, `p` is what is know to hold before that statement and `q` is what's known to hold afterwards.  As a very simple example,  consider this:

[{{<img class="text-center" src="http://whiley.org/wp-content/uploads/2013/11/HoareTripleExample.png">}}](http://whiley.org/wp-content/uploads/2013/11/HoareTripleExample.png)
This is saying that, if we know `x >= 0` holds before the statement `x = x + 1` then we know that `x > 0` holds afterwards.  In fact, we can read this triple in the other direction as well.

Hoare logic has provided an excellent theoretical foundation for reasoning about programs, and has been rightly praised within the academic community.  However, when trying to verify programs using a tool based on Hoare logic (such as Whiley) it really helps to have some idea of the rules. In particular, the rule for `while` loops is (very roughly speaking):

[{{<img class="text-center" src="http://whiley.org/wp-content/uploads/2013/11/HoareLoopRule2.png">}}](http://whiley.org/wp-content/uploads/2013/11/HoareLoopRule2.png)
Here, the implication `e1 ==> e3` ensures that the loop invariant is implied by the pre-state (i.e. holds on entry). The triple `{e2 && e3} s {e3}` establishes that the loop invariant is maintained by the loop body assuming it and the loop condition held before the body executed. Finally, the post-state for the rule `{!e2 && e3}` is all the information we can infer as being true after the loop.  This latter bit, it turns out, is quite restrictive ... and we'll look at this next.
## Challenges
The Hoare rule for describing the how `while` loops are handled by the verifier has a number of subtle aspects. For example, consider the following program:

```whiley
int f(int n) ensures $ == n:
   i = 0
   x = n // shadows n
   while i < n:
      i = i + 1
   return x
```

Take a moment and think about this: *would you expect this program to verify?*

Intuitively, it really seems as though the above program should verify.  But, it doesn't under the Hoare rule for `while` loops.  *The thing about the rule is that all it guarantees is that the loop invariant and the negated condition hold after the loop*.  In this case, it tells us that `i >= n` holds. But, it doesn't say anything about the variable `x` after the loop. Specifically, *whether or not it has the same value as `n` after the loop*. Of course, as humans, we know it does; but, a verifying compiler cannot reason like a human and must rely on its logical framework instead (in this case, the rules of Hoare logic).

In fact, the Whiley compiler will verify the above program as correct, simply because it doesn't use the original Hoare rule for While loops! Instead, it uses an extended rule whereby variables which are invariant around the loop (e.g. `x` above) are known to retain their value after the loop. This might seem obvious, and indeed it is.  But, it serves to highlight that the rules of Hoare logic are not (as is) a panacea for practical software verification --- *i.e. they need a little tweaking along the way!*## Further Reading
If you're interested in finding out more about this kind of thing, a useful starting point might be our recent article which discusses this problem and more:
   * **Reflections on Verifying Software with Whiley**. David J. Pearce and Lindsay Groves. In *Proceedings of the Workshop on Formal Techniques for Safety-Critical Software (FTSCS)*, 2013. A preliminarly copy is available [ [PDF](http://homepages.ecs.vuw.ac.nz/~djp/files/FTSCS13.pdf) ]

