---
date: 2012-12-11
title: "A Source File with 72KLOC!?"
draft: false
---

Yesterday, I was looking at the stats on [Ohloh for the Whiley project](http://www.ohloh.net/p/whiley/) and noticed that my total line count for the project had increased from around 65KLOCto 143KLOC over a very short amount of time:

{{<img class="text-center" width="600px" src="/images/2012/LOC.png">}}

Confused, I was pondering this for a while.  Then it struck me: *I'd checked in a [single file with 72KLOC](https://github.com/DavePearce/Whiley/blob/89480643dc8525a543fd505d5188e2be3a1601e0/modules/wyil/src/wycs/Solver.java)!* How could I forget that? That explains the sudden jump and, I'm sure, will mess with any metrics for the project.  And, you guessed it, *that file has no commenting whatsoever*!

Anyhow, the question remains: *how could I possibly have such a huge file?* Well, it's a fair question that deserves an answer.  The good news is that I didn't write that file --- it was automatically generated for me.  Which explains the lack of comments, and general ugliness of the file.  The file implements a complete [automated theorem prover](http://en.wikipedia.org/wiki/Automated_theorem_proving) which is generated from a [DSL](http://en.wikipedia.org/wiki/Domain-specific_language) describing rewrite rules (you can see some examples [here](https://github.com/DavePearce/Whiley/blob/89480643dc8525a543fd505d5188e2be3a1601e0/modules/wyil/src/wycs/theory/logic.wyone) and [here](https://github.com/DavePearce/Whiley/blob/89480643dc8525a543fd505d5188e2be3a1601e0/modules/wyil/src/wycs/theory/numerics.wyone)).

Finally, it turns out this is not the first time I've done this either!  My last project was building a Java Compiler from scratch (called [JKit](http://homepages.ecs.vuw.ac.nz/~djp/jkit/)) which contains a [single file with 33KLOC](https://github.com/DavePearce/jkit/blob/master/jkit/java/parser/JavaParser.java).  Again, this was automatically generated from an [ANTLR](http://antlr.org/) [grammar of the Java language](https://github.com/DavePearce/jkit/blob/master/jkit/java/parser/Java.g), and is used to parse source files into an [Abstract Syntax Tree](http://en.wikipedia.org/wiki/Abstract_syntax_tree).
