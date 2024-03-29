---
draft: false
date: 2016-01-01
kind: "journal"
title: "A Space Efficient Algorithm for Detecting Strongly Connected Components"
authors: "David J. Pearce"
booktitle: "Information Processing Letters"
volume: "116"
number: "1"
pages: "47--52"
copyright: "Elsevier"
DOI: "10.1016/j.ipl.2015.08.010"
preprint: "Pea16_IPL_preprint.pdf"
---

**Abstract.**  Tarjan’s algorihm for finding the strongly connected components of a directed graph is widely used and acclaimed. His original algorithm required at most `v(2 + 5w)` bits of storage, where w is the machine’s word size, whilst Nuutila and Soisalon-Soininen reduced this to `v(1 + 4w)`. Many real world applications routinely operate on very large graphs where the storage requirements of such algorithms is a concern. We present a novel improvement on Tarjan’s algorithm which reduces the space requirements to `v(1 + 3w)` bits in the worst case. Furthermore, our algorithm has been independently integrated into the widely-used SciPy library for scientific computing.

**Notes.**  This algorithm was incorporated into [SciPy](https://www.scipy.org/), a widely used Python Library for scientific computing (see [here](https://docs.scipy.org/doc/scipy/reference/generated/scipy.sparse.csgraph.connected_components.html)).  Some discussion of the SciPy implementation can be found [here](http://www.timl.id.au/SCC/) as well.  A straightforward Java implementation of the algorithm is available [here](http://github.com/DavePearce/StronglyConnectedComponents) and another C++ implementation is available [here](https://github.com/edervishaj/strongly-connected-components).
