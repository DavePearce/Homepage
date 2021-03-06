---
date: 2005-01-01
kind: "thesis"
tags: ["dts"]
title: "Some directed graph algorithms and their application to pointer analysis."
authors: "David J. Pearce"
thesis: "PhD"
school: "Imperial College of Science, Technology and Medicine, University of London"
preprint: "Pearce05_PhD.pdf"
---

**Abstract:** This thesis is focused on improving execution time and precision of scalable pointer analysis. Such an analysis statically determines the targets of all pointer variables in a program. We formulate the analysis as a directed graph problem, where the solution can be obtained by a computation similar, in many ways, to transitive closure. As with transitive closure, identifying strongly connected components and transitive edges offers significant gains. However, our problem differs as the computation can result in new edges being added to the graph and, hence, dynamic algorithms are needed to efficiently identify these structures. Thus, pointer analysis has often been likened to the dynamic transitive closure problem.
Two new algorithms for dynamically maintaining the topological order of a directed graph are presented. The first is a unit change algorithm, meaning the solution must be recomputed immediately following an edge insertion. While this has a marginally inferior worse-case time bound, compared with a previous solution, it is far simpler to implement and has fewer restrictions. For these reasons, we find it to be faster in practice and provide an experimental study over random graphs to support this. Our second is a batch algorithm, meaning the solution can be updated after several insertions, and it is the first truly dynamic solution to obtain an optimal time bound of O(v+e+b) over a batch b of edge insertions. Again, we provide an experimental study over random graphs comparing this against the standard approach to topological sort. Furthermore, we demonstrate how both algorithms can be extended to the problem of dynamically detecting strongly connected components (i.e. cycles), thus achieving the first solutions which do not need to traverse the entire graph for half of all edge insertions.
Several other new techniques for improving pointer analysis are also presented. These include difference propagation, which avoids redundant work by tracking changes in the points-to sets, and a novel approach to field-sensitive analysis of C. Finally, a detailed study of numerous solving algorithms, evaluating our techniques and algorithms against previous work, is contained herein. Our benchmark suite consists of many common C programs ranging in size from 15,000-200,000 lines of code.
