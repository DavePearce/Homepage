---
date: 2011-08-30
title: "Simplification vs Minimisation of Types in Whiley"
draft: false
---

Recently, I've been trying to harden up the implementation of Whiley's type system.  The reason for this is fairly straightforward: *bugs in the code often prevent me from compiling correct programs!*

In thinking about how to restructure the algorithms I'm using, I realised its important to distinguish *simplification* from *minimisation*.  I've talked about minimisation in some detail before (see [here](/2011/02/16/minimising-recursive-data-types/) and [here](/2011/03/07/implementing-structural-types/)).

In fact, there are three phases in computing the ideal representation of a given type (in order of increasing difficulty):
   * **Simplification.** Here, we apply mostly straightforward simplifications.  Examples include: `(T|T) => T`, `(T|any) => any`, `(T|void) => T`, `(T|(S|U)) => (T|S|U)`, etc.

   * **Minimisation. **Here, we ensure that no two nodes in the type graph are *equivalent* under the subtyping operator.  For example, the type `X<[null|[null|X]]>` is not minimised, whilst `X<[null|X]>` is its minimised form.

   * **Canonicalisation.** The final step is to ensure equivalent types have an identical representation on the machine.  This is related to the [Graph isomorphism](http://wikipedia.org/wiki/Graph_isomorphism) problem and, more specifically, the issues of computing a [canonical form](http://wikipedia.org/wiki/Graph_canonization) of a graph.

This all seems fairly straightforward ... but there are of course some tricky bits!!

## Simplification is not that Simple!
The first mistake I made was to assume that simplification was a simple step in the process.  Unfortunately, there are some gotchas:

```whiley
define LinkedList as null | { LinkedList next, int data }
define MyList as int | LinkedList
```

The problem is how to minimise this.  One property I want of the simplified form is to eliminate unions of unions.  But, consider the type graph for `MyList`:

{{<img class="text-center" width="400px" src="/images/2011/SimplifyingRecursiveTypes1.png">}}

(here, circles represent unions, squares represent records, etc)

Now, it's difficult to see how we simplify the above to remove the union (node `0`) of a union (node `2`).  That's because of the recursive link directly into node `2`.  In fact, we can achieve this by judiciously expanding the type like so:

{{<img class="text-center" width="400px" src="/images/2011/SimplifyingRecursiveTypes2.png">}}

This does the trick and (I believe) it's always possible to eliminate unions of unions in this way.
## Simplification Helps Minimisation!
You might be wondering: *why bother with simplification at all? * Well, it's because it simplifies the minimisation algorithm (which is one of the components that features a lot of bugs).  In essence, simplification gives me the following property:
> **Property 1**.  Any two equivalent nodes in a simplified type have identical reachable structure.

In a non-simplified type graph, the above is clearly not true.  For example, in the type `(any|int)` the outer union and `any` are equivalent (but have different structure).  Here's another example:

{{<img class="text-center" width="400px" src="/images/2011/SimplifyingRecursiveTypes3.png">}}

Here, nodes `0`, `1` and `2` are all equivalent.  But, whilst nodes `1` and `2` have identical reachable structure, this differs from node `0`.  In particular, the children of node `0` are in the same equivalence class as node `0`, whilst those for nodes `1` and `2` are not.  In practical terms, my minimisation algorithm would have to handle this edge case and its numerous variations.  With simplified form, however, these awkward cases disappear.
## Minimised is Simplified?
An interesting question is whether a type remains in simplified form after minimisation.  I conjecture that the answer is yes!  Atleast, provided a little care is taken.

The main issue is that the minimisation process can remove union nodes; however, *it cannot introduce them*.  Consider this final example:

```whiley
define LinkedList as null | { LinkedList next, int data }
define InnerList as null | { OuterList next, int data }
define OuterList as null | { InnerList next, int data }
define MyList as LinkedList | OuterList
```

These definitions give rise to the following type graph for `MyList`:

{{<img class="text-center" width="600px" src="/images/2011/SimplifyingRecursiveTypes23.png">}}

This type graph is fully simplified (it's a bit of a monster though!).  This is because simplification does not attempt to eliminate *equivalent* nodes from a union, only *identical* ones.  After minimisation, the outermost union will be removed and we'll be left with just this:

{{<img class="text-center" width="200px" src="/images/2011/SimplifyingRecursiveTypes24.png">}}

Now, back to the original issue.  In the general case, we can remove a union node from between any two nodes.  However, we cannot remove other kinds of nodes unless the entire subtree below that node is removed.  Therefore, we cannot introduce a union of union through this process.
