---
draft: false
date: 2003-01-01
title: "DJProf"
subtitle: "An experimental Java profiling tool which employs AspectJ to insert the necessary instrumentation for profiling rather than, for example, the Java Machine Profiler Interface (JVMPI). DJProf can be used to profile Java programs without modification (i.e. there is no need to recompile them for profiling) and does not require the user to have any knowledge of AspectJ. "
tag: "djprof"
---

DJProf is an experimental Java profiling tool which employs AspectJ to insert the necessary instrumentation for profiling rather than, for example, the Java Machine Profiler Interface (JVMPI). DJProf can be used to profile Java programs without modification (i.e. there is no need to recompile them for profiling) and does not require the user to have any knowledge of AspectJ. The Load-Time Weaving capability of AspectJ is utilised to make this possible. The tool (including its source code) is release under a very straightforward (and unrestrictive) license for the benefit of all.

DJProf supports several different modes of profiling:

   * **Heap usage**. In this mode, the memory consumed by each function is recorded. This is implemented by placing advice before each call to new. Reflection is used to estimate the size of a given object.

   * **Object lifetime**. In this mode, the average lifetime of objects of each class is recorded. An object's lifetime is defined as the time between creation (via new) and garbage collection (of that object). Weak references are used to determine when an object has been garbage collected. Rather than tracking the lifetime of every object (since this would be expensive) a sampling mechanism is used, where only every X objects are actually tracked (the actual sampling interval X is configurable).

   * **Wasted time**. Following Röjemo and Runciman, the lifetime of an object can be broken up into the Lag, Drag and Use phases. Here, Lag is the time between the creation and first use of an object; Drag is the time between the last use of an object and it being garbage collected; while Use covers the remainder. We regard the Lag and Drag phases of an object's lifetime as being wasted time and, in this mode, DJProf will report the average amount of wasted time for all instances of each class. As with lifetime profiling above, sampling is used to reduce the overhead of doing this. Furthermore, DJProf utilises the field get/set join points in order to determine when an object has been used (although these can be expensive).

   * **Time spent**. This is one of the classic profiling metrics, where the time spent in each method is reported. DJProf provides two different ways of doing this, both of which employ sampling in one form or another. The first approach employs AspectJ to track which method is currently executing and then samples this information periodically (although this turns out to be rather expensive in practice). The second approach does not use AspectJ at all, but instead uses the method `Thread.getAllStackTraces()` which has recently been introduced in Java 1.5. Again, this is sampled periodically to generate the profiling data.

Details of how the profiler works can be found in our paper entitled "Profiling with AspectJ" (see below).

**History.** DJProf was designed as a tool for research in AspectJ and AOP. It was developed by David J. Pearce in the summer of 2004 during an internship with the AspectJ team based at IBM Hursley, UK. The work was supervised by Dr Paul H.J Kelly (Imperial College) and Dr Robert Berry (IBM). Since that time, IBM has kindly assigned the copyright of DJProf over to David and, hence, he has now made it available for all to use. The name "DJProf" comes from the author's initials, which are DJP!

**Installation.**  The current version of `djprof` should work out-of-the-box on Linux/UNIX machines, and on Windows machines with Cygwin. However, for windows without Cygwin, you currently have to create your own batch file.  Installing DJProf is quite straightforward, although there is no automatic installer as yet. The first step is to download and install the latest version of AspectJ, assuming it's not already installed on your system. Then, download and unpack the latest tarball given above. This includes the compiled Java class files, so there is no need to compile DJProf from scratch. At this point, there are three things you need to do:

   1. Add the `bin/` directory (which contains the djprof) script to your `PATH` environment variable.
   2. Set the `DJPROF_HOME` environment variable to the djprof directory.
   3. Make sure that the `ASPECTJ_HOME` environment variable holds the location the AspectJ package (this should be done as part of the AspectJ install).

**Command-Line Options.**

The following command-line options are used to select DJProf's profiling mode. At the moment, they can only be used one at a time:

   - (`-heap`) Perform exact heap profiling by intercepting every new call and recording the sizeof the allocated object.

   - (`-time`) Perform sampling-based cpu profiling by using the Thread.getAllStackTraces() method to determine the currently executing method (preferred over -cpu in practice).
   
   - (`-time-exact`) Perform sampling-based cpu profiling by using AspectJ to monitor the currently executing method (currently this mode is expensive and inaccurate).
   
   - (`-lifetime`) Profile the average lifetime (time between construction and collection) of objects created in the system by intercepting new calls to determine creation times and weak references to determine collection times. This mode uses sampling to reduce the overhead.
   
   - (`-waste`) Perform sampling-based wasted time profiling by: 1) intercepting new calls to determine creation times; 2) weak references to determine collection times; 3) get()/set() pointcuts to determine object uses. A HashMap is used to associate the necessary state with each object. Generally speaking, wasted-time profiling is expensive
   
   - (`-itd-waste`) This is similar to `-waste`, except that AspectJ's intertype declarations are used to associate state with each object.

   - (`-pt-waste`) This is similar to `-waste`, except that it uses AspectJ's pertarg declarator to associate state with each object. Note, this mode is very experimental and does not work properly!

DJProf also supports the following general command-line options:

   - (`-o outputfile`) Tells DJProf to write its output into the file given by filename.

   - (`-period X`) Sets the period to X for sample-based profiling.
   
   - (`-timeout X`) Tells DJProf to timeout after `X` seconds (useful when wasted-time profiling, since this can cause large overheads).
   
   - (`-cp X`) Override the default `CLASSPATH` with `X`.
   
   - (`-classpath X`) Override the default `CLASSPATH` with `X`.
   
   - (`-show-weaveinfo`) Prints out information about what weaving AspectJ has done (useful for debugging)
   
   - (`-X EXTRA`) Runs java with the parameter `-XEXTRA` (e.g. `-Xmx512M`)