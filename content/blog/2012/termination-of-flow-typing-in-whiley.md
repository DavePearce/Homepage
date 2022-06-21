---
date: 2012-04-17
title: "Termination of Flow Typing in Whiley"
draft: false
---

Whiley uses *flow typing* to give it the look-and-feel of a dynamically typed language (see [this page](http://whiley.org/guide/typing/flow-typing/) for more on flow typing).  In short, flow typing means that variables can have different types at different program points.  For example:

```whiley
define Node as { int data, Tree left, Tree right }
define Tree as null | Node

// Insert item into tree
Tree insert(Tree tree, int item):
    if tree is null:
        return {
          data: item,
          left: null,
          right: null
        }
    else if item < tree.data:
        tree.left = insert(tree.left,item)
    else:
        tree.right = insert(tree.right,item)
    // done
    return tree
```


Here, Whiley's flow typing system automatically *retypes* the variable `tree` to `Node` on the false branch of the condition `tree is null`.  Thus, we can safely use `tree.left` and `tree.right` without having to explicitly cast `tree` to a `Node`.  In this way, we see that variable `tree` has different types at different points.  In this case, it always has one of the following types: `Tree`, `Node` or `null` (the latter being its type on the true branch of the condition `tree is null`).

In the above example, the different types of tree are related --- i.e. `Node` and `null` are both subtypes of `Tree`.   However, Whiley's flow typing system can also work with unrelated types.  A more involved example, based on my Chess benchmark, illustrates:

```whiley
define LongPos as { int col, int row }
define LongMove as {
  Piece piece,
  LongPos from,
  LongPos to
}

define ShortPos as {int row} | {int col}
define ShortMove as {
  Piece piece,
  ShortPos from,
  LongPos to
}

// Find matching pieces on the board.
[Pos] find(Piece p, Board b):
    ...

// Intersect destination with possible moves of matching pieces.
[Pos] narrow(ShortMove m, [Pos] ms, Board b):
    ...

// Convert move in short notation into long notation.
LongMove convert(ShortMove m, Board b) throws Error:
    matches = find(m.piece,b)
    matches = narrow(m.to,matches,b)
    if |matches| != 1:
        throw Error("invalid move")
    m.from = matches[0]
    return m
```


This code converts moves expressed in *short algebraic notation* into *long algebraic notation* (see [this](http://en.wikipedia.org/wiki/Algebraic_notation_(chess)) for more).  In the short notation, moves are given in an abbreviated (and potentially ambiguous) form with only the destination square given.  For example, a move `Nf6` indicates a Knight moving to square `f6`.  If the player has only one knight, then the move must refer to it.  However, if there are two Knights, the system must determine which it is.  This is done by `narrow()` above, which intersects the destination with the possible moves of the matching pieces.  In the case of multiple matches, an `Error` is thrown.  The notation also permits explicit disambiguation by providing either the *rank* or *file* of the given piece.  For example, `Neg3` indicates the Knight on file `e` moves to square `g3`.

Flow typing is crucial in type checking the above fragment.  The critical issue resolves around the statement `m.from = matches[0]`.  This retypes variable `m` from `ShortMove` to `LongMove`, allowing the subsequent statement `return m` to be type checked.  In this case, there is no subtyping relationship between `ShortMove` and `LongMove` --- they are essentially unrelated.
## A Problem of Termination
We now come to the main topic of this post.  Consider the following very simple example:
```whiley
define Rec as int | { Rec f }

Rec loopy(int x, int y):
  z = { f : x }
  while x < y:
     z.f = z
     x = x + 1
  return z
```
This program currently causes the Whiley Compiler to enter an infinite loop! *But, why is that?* Well, the key is that flow typing is implemented in the style of a [dataflow analysis](http://en.wikipedia.org/wiki/Data-flow_analysis).  Roughly speaking, the algorithm employs an *environment* which maps variables to their current type.  It then processes each statement updating the environment as it proceeds.  The following illustrates the process for the first two statements above:
```whiley
Rec loopy(int x, int y):
  // x->int, y->int
  z = { f : x }
  // x=>int, y=>int, z=>{int f}
  ...
```
In many ways, this is the natural way to implement the flow typing algorithm and follows, for example, the approach used in the JVM bytecode verifier (perhaps the most widely used example of flow typing).

*So, why does the above short program not terminate?* Well, a dataflow analysis handles loops by iterating until a *fixed-point* is reached.  This is done by first propagating through the loop body to update the environment, like so:
```whiley
  // x=>int, y=>int, z=>{int f}
  while x < y:
      // x=>int, y=>int, z=>{int f}
      z.f = z
      // x=>int, y=>int, z=>{{int f] f}
      x = x + 1
      // x=>int, y=>int, z=>{{int f] f}
```
At this point, it merges the new environment with that originally going into the loop and repeats the process:
```whiley
  // x=>int, y=>int, z=>{int f}
  while x < y:
      // x=>int, y=>int, z=>{{int f}|int f}
      z.f = z
      // x=>int, y=>int, z=>{{{int f}|int f} f}
      x = x + 1
      // x=>int, y=>int, z=>{{{int f}|int f} f}
```
And then repeats again:
```whiley
  // x=>int, y=>int, z=>{int f}
  while x < y:
      // x=>int, y=>int, z=>{{{int f}|int f} f}
      z.f = z
      // x=>int, y=>int, z=>{{{{int f}|int f} f} f}
      x = x + 1
      // x=>int, y=>int, z=>{{{{int f}|int f} f} f}
```
And so on, until there is no change in the environment (i.e. the fixed point is reached).  For a typical dataflow analysis, this should happen within a few iterations. However, for the above flow typing algorithm, *this process will never terminate because it is constructing successively larger types for variable `z`*.  

In fact, we can obtain a valid flow-typing for our simple program as the following illustrates:
```whiley
Rec loopy(int x, int y):
  // x->int, y->int
  z = { f : x }
  // x=>int, y=>int, z=>{int f}
  while x < y:
      // x=>int, y=>int, z=>{Rec f}
      z.f = z
      // x=>int, y=>int, z=>{Rec f}
      x = x + 1
   // x=>int, y=>int, z=>{Rec f}
   return z
```
The key is that assigning `{Rec f}` to field `f` gives the type `{{Rec f} f}` --- which is a subtype of `{Rec f}`.  Hence, the above typing is valid (albeit conservative).  The problem is how to go about finding this typing, given that the standard dataflow approach does not succeed.

At this point, I'm not going to say too much more.  That's because I've written up a [detailed account of the solution in a technical report](/publications/ECSTR12-10.pdf).  The key idea is to use a *constraint-based* approach, rather than a dataflow approach.  Whilst this is slightly more complex, it has one important advantage: *the algorithm always terminates!*
