---
draft: false
date: 2004-01-01
title: "Dynamic Topological Sort"
description: "Efficient algorithms for updating the topological sort of a directed graph after one or more edge insertions.  Such algorithms can also be used for detecting cycles in dynamically changing graphs as well."
tags: ["dts"]
---

The problem of topologically sorting a directed graph is about arranging its nodes so that all edges go in the same direction. For example, consider the following directed graph:

![A directed graph with an invalid topological order](graphA.jpg)

A topological sort of this graph is:

![A directed graph with a corrected topological order](graphB.jpg)

There are often many possible solutions for a given graph and we are simply interested in obtaining one of them. This is a well-known problem and optimal algorithms are known which need O(v+e) time, where v is the number of nodes and e the number of edges.

A variation on this, called the *Dynamic Topological Sort (DTS)* problem, is that of updating the topological sort after a new edge is added to the graph. For example, consider adding an edge from A to C in our sorted graph above. In this case, we need only swap the positions of A and C to update the sort. Here, the difficulty lies in performing the least amount of work to obtain a new topological sort. It turns out this problem has not been studied very much in the past and details of existing algorithms can be found in my papers below.

