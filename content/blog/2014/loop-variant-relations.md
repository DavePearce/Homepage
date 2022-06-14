---
date: 2014-07-10
title: "Loop Variant Relations"
draft: false
---

Proving that a loop always terminates is a common requirement when verifying software. The usual approach to doing this is to provide a [loop variant](http://en.wikipedia.org/wiki/Loop_variant) function. This is typically an integer expression which decreases on every iteration of the loop. Consider the following loop:

```whiley
function contains([int] items, int item) => bool:
    int i = 0
    //
    while i < |items|:
        if items[i] == item:
            return true
        i=i+1
    //
    return false
```

In this case, a suitable loop variant would be `|items| - i` which is guaranteed to decrease on each iteration of the loop. Furthermore, we know that when this reaches zero the loop will terminate (if it hasn't already).
## Overview of Relations
An alternative to a loop variant function is to use what I'm calling a *loop variant relation*.  The essential idea is to have an expression which relates the state before the loop body to that after it (which is not dissimilar, in fact, to a post-condition).   In particular, we can refer to a variables state before and after the body has executed within the same expression.  For example, if `x` is a variable modified in the loop then `x'` is the value that holds after the loop body.

Let's reconsider our simple example from above:

```whiley
function contains([int] items, int item) => bool:
    int i = 0
    //
    while i < |items| variant i < i':
        if items[i] == item:
            return true
        i=i+1
    //
    return false
```

Here, I've included the loop variant relation `i < i'` which dictates that `i` is strictly increased by the loop. Using this relation, which can also establish that the loop terminates (more on this later).
## Why Relations?
An important question here is: *why consider loop variant relations over loop variant functions?* The answer is simply that, with a loop variant relation, we can everything we could with a loop variant function *and more*.  For example, loop variant relations can be used to show *the absence of change*.  Whilst this might seem odd, consider the following example:

```whiley
function add([int] v1, int c) => ([int] v2)
ensures |v1| == |v2|:
   //
   int i = 0
   //
   while i < |vs| invariant i >= 0:
       v1[i] = v1[i] + c
       i = i + 1
   //
   return v1
```

This function simply adds a constant onto every element of an integer list. Unfortunately, this will not verify as is because the postcondition cannot be established. I have previously discussed this problem [here](/2014/06/20/understanding-ghost-variables-in-software-verification/) and proposed a solution using *ghost variables*. However, loop variant relations gives us a better solution. The problem in verifying this function is that the verifier knows nothing about variables modified in a loop, except for what is specified in the loop invariant. Since `v1` is modified in the loop and nothing is given about it in the loop invariant, the verifier does not know that its size remains unchanged. Using a loop variant relation, we can expression this as follows:

```whiley
function add([int] v1, int c) => ([int] v2)
ensures |v1| == |v2|:
   //
   int i = 0
   //
   while i < |vs| invariant i >= 0 variant |v1| == |v1'|:
       v1[i] = v1[i] + c
       i = i + 1
   //
   return v1
```

Here, the loop variant relation `|v1| == |v1'|` simply establishes that the size of `v1` is unchanged by the loop and, using this, the verifier can (in principle) verify this function.
## Another Example
Another common situation where loop variants can be helpful is related to the partial update of records.  For example, consider this simple parsing function:

```whiley
type State is { string input, int pos } where 0 <= pos && pos <= |input|

function parseNumber(State s) => State
// input being parsed is not modified
ensures s.input == r.input:
    //
    while s.pos < |s.input| && isNumericAt(s):         
	   s.pos = s.pos + 1
    //
    return s

function isNumericAt(State s) => bool
requires s.pos < |s.input|:
    //
    char c = s.input[s.pos]
    return '0' <= c && c <= '9'
```

Intuitively, we can see that this function meets its postcondition (i.e. returns a valid instance of `State` over the same input `string`). Surprisingly, this function will not verify as is. The reason for this is that, since variable `s` is modified in the loop, all information connecting the value of `s.input` before the loop with that after the loop is lost. We can resolve this by adding the loop variant `s.input == s'.input` (note, it can also be resolved using a ghost variable).
## Challenges
Unfortunately, loop variant relations are not completely straightforward to understand and implement.  For example, based on the above discussion, one might think that the loop variant holds after the loop (as the invariant does). Whilst this is in some sense true, we must be careful to interpret it correctly. The following illustrates:

```whiley
function alt(int x, int n) => (int r)
ensures x != r:
    //
    int i = 0
    //
    while i < n variant x != x':
        x = -x
        i = i + 1
    //
    return x
```

For the loop variant given, one might conclude that this function is correct and should verify. However, in fact, it cannot pass verification because the postcondition does not hold for even values of `n`. The problem is that the loop variant holds after the loop, in the sense that `x != x'` where `x` is the value of `x` at the beginning of the last iteration and `x'` the value at the end of the last iteration. Unfortunately, this is not enough to connect the value of `x``before` the loop with that from `after` the loop.
## Termination
An important reason for using loop variant functions is to ensure that a loop terminates.  *Therefore, the question is whether or not we can use loop variant relations like this as well? *Certainly, the answer to this is "yes" if we are reasoning by hand.  However, I'm looking for a more general (i.e. mechanical) rule which we can apply.  Indeed, a way to extend the existing [Hoare Logic](http://en.wikipedia.org/wiki/Hoare_logic) rule for `while` loops.

One approach might be to exploit the loop condition and ariant to show, in such case, the loop *won't* terminate.  For example, consider this loop:

```whiley
function f(int n) => int:
    int i = 0
    //
    while i < n variant i' < i:
        i = i - 1
    //
    return i
```

Here, it follows that `i < n && i' < i` implies `i' < n` and, hence, we can conclude the loop does not terminate. In contrast, for this loop:

```whiley
function f(int n) => int:
    int i = 0
    //
    while i < n variant i < i':
        i = i + 1
    //
    return i
```

It follows that `i < n && i < i'` does *not* imply `i' < n` and, hence, we can conclude the loop *might* terminate. The real question is whether we can establish anything stronger than this ...
## Further Reading
There are lots of interesting papers which discuss loop invariants and variants.  However, I haven't found so many which consider loop variants as relations in this way.  Here are those I've found (so far):

   * **Invariant relations, invariant functions, and loop functions**, Lamia Labed Jilani and Asma Louhichi and Olfa Mraihi and Ali Mili.  *Innovations in Systems and Software Engineering*, 8(3), pp. 195-212, 2012. ([Link](http://link.springer.com/article/10.1007%2Fs11334-012-0189-0))

   * **Invariant Relations: An Alternative Tool to Analyze Loops**Asma Louhichi, Olfa Mraihi, Wided Ghardallou, LamiaLabed Jilani, Khaled Bsaies and Ali Mili.  Technical Report. ([Link](http://web.njit.edu/~mili/tac.pdf)).

   * **Transition Invariants**, Andreas Podelski and Andrey Rybalchenko.  In Proceedings of Conference on Logic in Computer Science (LICS), 2004. ([Link](http://www.avacs.org/Publikationen/Open/podelski.lics.04.draft.pdf))

   * **Elimination of Ghost Variables in Program Logics,** Martin Hofmann and Mariela Pavlova.  In *Proceedings of the Symposium on Trustworthy Computing*, pages 1--20, 2008. ([Link](http://link.springer.com/chapter/10.1007%2F978-3-540-78663-4_1))

   * **An Improved Rule for While Loops in Deductive Program Verification**, Bernhard Beckert and Steffen Schlager and Peter H. Schmitt.  In Proceedings of the International Conference on Formal Engineering Methods, 2005. ([Link](http://digbib.ubka.uni-karlsruhe.de/volltexte/documents/2839))

