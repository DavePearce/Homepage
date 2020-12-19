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
order](https://abseil.io/docs/cpp/guides/synchronization).  More
specifically, it detects cycles in the _acquires-before_ graph where a
cycle indicates a potential deadlock.  **(SAY MORE.  Not in
production.  What is this graph?)**

The key challenge faced here is that the graph is changing in real
time.  Therefore, we want to perform the least amount of work when the
graph is updated to check whether there is a cycle or not, and this is
where my algorithm comes in.

### Cycle Detection

Talk about Tarjan's algorithm briefly.
