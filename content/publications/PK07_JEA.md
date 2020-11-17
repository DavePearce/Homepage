---
draft: false
date: 2007-01-01
kind: "journal"
tags: ["dts"]
title: "A Dynamic Topological Sort Algorithm for Directed Acyclic Graphs"
authors: "David J. Pearce and Paul H.J. Kelly"
booktitle: "ACM Journal of Experimental Algorithmics (JEA)"
volume: "11"
pages: "1.7"
copyright: "ACM Press"
DOI: "10.1145/1187436.1210590"
preprint: "PK07_JEA_preprint.pdf"
---
**Abstract.** We consider the problem of maintaining the topological order of a directed acyclic graph (DAG) in the presence of edge insertions and deletions. We present a new algorithm and, although this has inferior time complexity compared with the best previously known result, we find that its simplicity leads to better performance in practice. In addition, we provide an empirical comparison against the three main alternatives over a large number of random DAGs. The results show our algorithm is the best for sparse digraphs and only a constant factor slower than the best on dense digraphs.

**Notes.** This algorithm is used in the C++ [Abseil
library](https://abseil.io/) (see [here](https://github.com/abseil/abseil-cpp/blob/master/absl/synchronization/internal/graphcycles.cc))
and
[TensorFlow](https://www.tensorflow.org/) (see [here](https://github.com/tensorflow/tensorflow/blob/master/tensorflow/compiler/jit/graphcycles/graphcycles.cc)),
both of which were originally developed at Google and [subsequently
released as open
source](https://opensource.googleblog.com/2017/09/introducing-abseil-new-common-libraries.html).  The algorithm is also found in the widely used [JGraphT](https://jgrapht.org/) library (see [here](https://jgrapht.org/javadoc-1.4.0/org/jgrapht/graph/DirectedAcyclicGraph.html)), and in the SAT solver [Monosat](https://github.com/sambayless/monosat).  There are also a number of implementations available (see e.g. [here](https://blutorange.github.io/js-incremental-cycle-detect/), [here](https://libraries.io/npm/occam-pearce-kelly/2.7.4) and [here](https://github.com/paerallax/pearce-kelly)) as well as my own [C++ implementation](../../files/oto-test-06102005.tgz) also available on [GitHub](https://github.com/DavePearce/DynamicTopologicalSort).  Finally, this algorithm has found interesting uses for [multiple sequence alignment](https://en.wikipedia.org/wiki/Multiple_sequence_alignment) (see e.g. [here](https://academic.oup.com/bioinformatics/article/23/2/e24/202846) and [here](https://www.ncbi.nlm.nih.gov/pmc/articles/PMC2684580/). 

