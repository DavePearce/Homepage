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

Recently, I discovered that an [algorithm of mine]({{<ref "/publications/pk07_jea" >}}) from a years back is being used in both [TensorFlow](https://www.tensorflow.org/) and the [Abseil C++](https://abseil.io/) library (see [here](https://github.com/tensorflow/tensorflow/blob/master/tensorflow/compiler/xla/service/graphcycles/graphcycles.cc) and [here](https://github.com/abseil/abseil-cpp/blob/master/absl/synchronization/internal/graphcycles.cc)).  That is of course pretty exciting since they are both widely used libraries!  So, I thought a summary of the algorithm and what it does might be in order ...

### Deadlock Detection

The algorithm is being used in `mutex` to ensure [locks are acquired
in a consistent
order](https://abseil.io/docs/cpp/guides/synchronization).  For
example, suppose we have two mutexes `M0` and `M1` which can be held
at the same time by threads `T0` and `T1`.  A deadlock can easily
occur if, for example, `T0` acquires `M0` before `M1`, whilst `T1`
acquires `M1` before `M0`.  Of course, this doesn't mean a deadlock
will happen every time but, if `T0` acquires `M0` and then `T1` has
aqcuires `M1`, then they are waiting on each other and it's a
deadlock.

The key is that, if mutexes are acquired according to a globally
consistent ordering (e.g. `M0` always before `M1`), _then no deadlock
can arise_.  The challenge is to determine an appropriate consistent
ordering of mutexes.  In fact, `mutex` does not attempt to determine
this beforehand (presumably as it is considered too hard).  Instead,
it simply observes program execution and reconstructs the ordering
dynamically.  Then, during execution, if some thread attempts to
acquire a mutex in an order inconsistent with this, then a potential
deadlock has been detected.

### Acquires-Before Graph

To detect deadlocks, `mutex` maintains a (global) ordering of lock
acquisition called the _aquires-before graph_.  This is implemented
using a global variable called `deadlock_graph` which stores a
directed graph, such as the following:

{{<img class="text-center" src="/images/2021/DeadlockDetection_Ordering.png" height="96em" alt="Illustrating different examples of aliasing between references.">}}

In this example, we have four mutexes and to interpret the graph we
should consider that an edge from `Mx` to `My` indicates that `Mx` has
been acquired before `My` in _all lock acquisitions observed thus
far_.

In addition to `deadlock_graph`, every thread is associated with a
structure holding the set of mutexes it currently holds.  When a
thread holding mutex `Mx` attempts to acquire mutex `My` an edge is
added to `deadlock_graph`.  If that edge is consistent with the
current ordering (i.e. does not introduce a cycle) then the
acquisition is successfull, otherwise an error message is reported
highlighting the potential deadlock.

#### NOTES

The key challenge faced here is that the graph is changing in real
time.  Therefore, we want to perform the least amount of work when the
graph is updated to check whether there is a cycle or not, and this is
where my algorithm comes in.

  * Only in debug mode
  
  * Requires annotations

  * From looking at the code.  It holds a per-thread structure which
    records all mutexes currently held, along with the deadlock graph.
    When a new lock is acquired, edges are added from all held mutexes
    to the new one.  What it does is dynamically build up the
    ordering. The `deadlock_graph` is a global data structured shared
    amongst all threads.  hence why only in debug mode.

### Efficiency

  * During debug mode, every lock acquisition requires updating the
    topological ordering of the graph.

  * Tarjan's algorithm is quite slow for this, and also unnecessary as
    we need only to identify one cycle.
