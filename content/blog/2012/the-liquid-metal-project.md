---
date: 2012-07-04
title: "The Liquid Metal Project"
draft: false
---

One of the most interesting projects I came across at [PLDI/ECOOP in Beijing](http://pldi12.cs.purdue.edu/) was the [Liquid Metal](http://researcher.watson.ibm.com/researcher/view_project.php?id=122) project being developed at [IBM's TJ Watson Research Center](http://www.watson.ibm.com/index.shtml). From the Liquid Metal homepage:
> The Liquid Metal project at IBM aims to address the difficulties that programmers face today when developing applications for computers that feature programmable accelerators (GPUs and FPGAs) alongside conventional multi-core processors.

There are a few demos on the Liquid Metal site, including [an N-Body simulation](http://researcher.watson.ibm.com/researcher/view_project_subpage.php?id=2183). During the conference, I got to see the demo live ... and it was pretty impressive!

Anyway, from my perspective, the most interesting part of the project is the [LIME](http://domino.research.ibm.com/library/cyberdig.nsf/1e4115aea78b6e7c85256b360066f0d4/43e23ec91521c1e3852577bc0048ea37!OpenDocument&Highlight=0,lime,language,manual) programming language being developed.  This is similar to Java, but aims to enable easy migration of code onto a [GPU](http://en.wikipedia.org/wiki/Graphics_processing_unit) or even an [FPGA](http://en.wikipedia.org/wiki/Field-programmable_gate_array).  Fundamental to this is the notion of  "arrays that behave as values" --- that is, arrays which are immutable and can't be `null`.  More specifically:
> 
> A value type represents a deeply immutable object type (e.g., data structure or array) declared using the value modiﬁer on a type.

The reason for these value types is that they represent data which can be safely transferred to/from a GPU or FPGA. At the language level, "tasks" are used to represent asynchronous computation performed by "workers":
> The worker methods input immutable (value types) arguments (if any) and must return values (if any). This ensures that data exchanged between tasks does not mutate in ﬂight, and provides the compiler and runtime greater opportunities for optimizing communication between tasks without imposing undue burden on the compiler to infer invariants involving aliasing

The project has already managed to demonstrate some impressive speedups using GPUs. The work on compiling down to FPGAs appears to be a little less developed which, after speaking with David Bacon at length about this, seems to have been significantly hampered by: a) difficulty of getting reliable FPGA boards; b) the large barrier to entry in working with FPGAs. But, they seem to have been making some good progress despite this, and I think there's some really exciting stuff going on there. Indeed, I noticed that [Forbes recently had an article on using FPGAs to power financial computations](http://www.forbes.com/sites/tomgroenfeldt/2012/03/20/supercomputer-manages-fixed-income-risk-at-jpmorgan/) ... so maybe, finally, we are beginning to see rise of the FPGA for general purpose computing...
## References
Some direct links for papers on LIME:

   * [Compiling a High-Level Language for GPUs (via Language Support for Architectures and Compilers)](http://researcher.ibm.com/files/us-bacon/Dubach12Compiling.pdf)
. 
[Christophe Dubach](http://researcher.watson.ibm.com/researcher/view.php?person=us-cdubach), Perry Cheng, [Rodric Rabbah](http://researcher.watson.ibm.com/researcher/view.php?person=us-rabbah), [David F. Bacon](http://researcher.watson.ibm.com/researcher/view.php?person=us-bacon), [Stephen J. Fink](http://researcher.watson.ibm.com/researcher/view.php?person=us-sjfink). In *Proceedings of the ACM Conference on Programming Language Design and Implementation*, 2012.

   * [Lime: a Java-compatible and Synthesizable Language for Heterogeneous Architectures](http://researcher.ibm.com/files/us-bacon/Auerbach10Lime.pdf). 
[Joshua Auerbach](http://researcher.watson.ibm.com/researcher/view.php?person=us-josh), [David F. Bacon](http://researcher.watson.ibm.com/researcher/view.php?person=us-bacon), Perry Cheng, [Rodric Rabbah](http://researcher.watson.ibm.com/researcher/view.php?person=us-rabbah). In *Proceedings of the ACM Conference on Object-Oriented Programming Systems, Languages, and Applications*, 2010.

