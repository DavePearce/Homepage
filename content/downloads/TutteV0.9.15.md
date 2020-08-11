---
date: 2011-09-01
tag: "tutte"
title: "Version 0.9.15"
download: "tuttepoly-v0.9.15d.tgz"
---

The following updates have been made:

   * For the `--chromatic` option, the tool now automatically removes
     multi-edges on the input graph.  Thanks to Alan Sokal for
     pointing out this bug.

   * For the `--chromatic1 option, the tool now reports the result as
     zero if the input graph contains a loop, and outputs an error
     message as well.

   * For the `--cache-stats` option, a bug was fixed for large graphs
     which caused arithmetic overflow of the counters.  Thanks to Gary
     for pointing out this bug.

   * Added `--stdin` option to allow reading graphs from stdin, rather than a file.
