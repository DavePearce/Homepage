---
date: 2009-11-01
tags: ["tutte"]
title: "Version 0.9.13"
download: "tuttepoly-v0.9.13.tgz"
---

The following updates have been made:

   1) Version 0.9.12 introduced a feature whereby "large" graphs were
never displaced from the cache.  This was useful for us where we were
computing the polynomial of the Truncated Icosahedron.  Furthermore, I
made the default setting to automatically determine what was
considered a large graph.  However, this caused problems when using
the tool to run large experiments, where each experiment consisted of
several graphs in one batch.  The problem was that lots of "large"
graphs were getting stuck in the cache, eventually clogging it up.
So, I have disabled this default setting.  You can still request
graphs over a certain size be retained, however, but this must be done
explicitly on the command-line.

   2) Some of the options listed with `tutte --help` were wrong, and have
been corrected.

