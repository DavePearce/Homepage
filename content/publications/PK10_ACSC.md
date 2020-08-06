---
date: 2010-01-01
kind: "conference"
tag: "dts"
title: "A Batch Algorithm for Maintaining a Topological Order"
authors: "David J. Pearce and Paul H.J. Kelly"
booktitle: "Australasian Computer Science Conference (ACSC)"
pages: "79--88"
copyright: "Australian Computer Society, Inc."
preprint: "PK10_ACSC_preprint.pdf"
---

**Abstract:** The dynamic topological order problem is that of efficiently updating a topological order after some edge(s) are inserted into a graph. Much prior work exists on the unit-change version of this problem, where the order is updated after every single insertion. No previous (non-trivial) algorithms are known for the batch version of the problem, where the order is updated after every batch of insertions. We present the first such algorithm. This requires `O(min{k · (v+e),ve})` time to process any sequence of k insertion batches. This is achieved by only recomputing those region(s) of the order affected by the inserted edges. In many cases, our algorithm will only traverse small portions of the graph when processing a batch. We empirically evaluate our algorithm against previous algorithms for this problem, and find that it performs well when the batch size is sufficiently large.