---
date: 2020-12-19
title: "Dynamic Cycle Detection for Lock Ordering"
draft: true
#metaimg: ""
#metatxt: ""
#twitterimgalt: ""
twittersite: "@whileydave"
#twitter: "https://twitter.com/whileydave/status/1333545363165175809"
#reddit: "https://www.reddit.com/r/rust/comments/k47rr0/understanding_partial_moves_in_rust/"
---

Recently, I discovered that an [algorithm of mine]({{<ref "/publications/pk07_jea" >}}) from a few years back is being used in both [TensorFlow](https://www.tensorflow.org/) and the [Abseil C++](https://abseil.io/) library (see [here](https://github.com/tensorflow/tensorflow/blob/master/tensorflow/compiler/xla/service/graphcycles/graphcycles.cc) and [here](https://github.com/abseil/abseil-cpp/blob/master/absl/synchronization/internal/graphcycles.cc)).  That is of course pretty exciting since they are both widely used libraries!  So, I thought it would be interesting to look at what it is being used for.

### Deadlock Detection

In Abseil, the algorithm is used in
[`mutex`](https://github.com/abseil/abseil-cpp/blob/master/absl/synchronization/mutex.cc)
to ensure [locks are acquired in a consistent
order](https://abseil.io/docs/cpp/guides/synchronization).  For
example, suppose we have two mutexes `M0` and `M1` which can be held
at the same time by two threads.  A deadlock can easily occur if, for
example, the first thread acquires `M0` before `M1`, whilst the second
acquires `M1` before `M0`.  This doesn't mean a deadlock will happen
every time.  But if, by chance, the first thread acquires `M0` at the
same time as the second aqcuires `M1` --- then we have a deadlock.

The key is that, if mutexes are acquired according to a globally
consistent ordering (e.g. `M0` always acquired before `M1`), _then no
deadlock can arise_.  The challenge is to determine an appropriate
ordering of mutexes.  In fact, `mutex` does not attempt to determine
this beforehand (presumably as it is considered too hard).  Instead,
it simply observes program execution and reconstructs the ordering
dynamically.  Then, during execution, if some thread attempts to
acquire a mutex in an order inconsistent with this, then a potential
deadlock has been detected and is reported.

### Acquires-Before Graph

To detect deadlocks, `mutex` maintains a (global) ordering of lock
acquisitions called the _aquires-before graph_.  This is implemented
using a global variable called `deadlock_graph` which stores a
directed acyclic graph, such as the following:

{{<img class="text-center" src="/images/2021/DeadlockDetection_Ordering.png" height="96em" alt="Illustrating different examples of aliasing between references.">}}

Here, we have four mutexes and an edge `Mx -> My` means `Mx` must be
_acquired before_ `My`.  More specifically, it indicates that `Mx` has
been acquired before `My` in _all lock acquisitions observed thus
far_.  As an example, consider mutex `M3`.  Both `M0` and `M1` must be
acquired before `M3` is acquired.  In contrast, it currently doesn't
matter whether `M2` is acquired before `M3` or not.

Mutexes which are unordered with respect to each other (as for `M2`
and `M3` above) are ordered _on demand_.  For example, if a thread
comes along and acquires `M2` whilst holding `M3`, then their relative
ordering becomes fixed:

{{<img class="text-center" src="/images/2021/DeadlockDetection_Ordering_Updated.png" height="96em" alt="Illustrating different examples of aliasing between references.">}}

From now on, any attempt to acquire `M2` before `M3` generates an
error message highlighting the potential deadlock.  To make this work,
every thread is associated with the set of mutexes it currently holds.
When a thread holding mutex `Mx` attempts to acquire mutex `My`, an
edge is added to `deadlock_graph`.  If that edge introduces a cycle,
we have a potential deadlock.  Otherwise, the ordering is updated (as
above) and execution proceeds.

At this point, it is becoming clear that performance is an issue.
Whenever any thread acquires a lock, the `deadlock_graph` must be
updated and the current ordering recalculated.  For this reason,
deadlock detection in `mutex` is only enabled when debugging.
Furthermore, an efficient algorithm for updating the ordering is
desirable (and this is where my algorithm comes in).

### Dynamic Cycle Detection

  * During debug mode, every lock acquisition requires updating the
    topological ordering of the graph.

  * Tarjan's algorithm is quite slow for this, and also unnecessary as
    we need only to identify one cycle.
