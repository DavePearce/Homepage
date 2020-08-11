---
date: 2009-08-01
tag: "tutte"
title: "Version 0.9.12"
download: "tuttepoly-v0.9.12.tgz"
---

The following updates have been made:

   1) A long standing bug related to memory corruption has been fixed.

   2) Support for 64bit machines now works correctly.  This means you can
   use very large caches (e.g. > 4G)

   3) Removed the tutteviz component, since was causing a lot of
   compilation problems.

   4) The `--split` option has been added.  This means you can split an
   input graph into a number of smaller graphs, which can then in turn
   be distributed over a grid.
