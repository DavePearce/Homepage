---
date: 2010-09-20
title: "Java Pathfinder"
draft: false
---

Recently, Simon Doherty gave a short talk on using [Java Pathfinder](http://wikipedia.org/wiki/Java_Pathfinder) to find bugs in Java programs.  Java Pathfinder is a model checker for Java code, particularly suited to reasoning about multi-threaded code and finding concurrency bugs.  Roughly speaking, it searches through all of the different possible execution traces for a given piece of [Java Bytecode](http://wikipedia.org/wiki/Java_Bytecode).  Different traces arise from the different possible schedules for executing threads.  To do this, Java Pathfinder executes the Java Bytecode in a controlled fashion which allows it to go back and restart from different points with a different schedules.  You have to provide some input, so it is really executing the code, rather than using [symbolic execution](http://wikipedia.org/wiki/symbolic_execution).

The reason Simon got interested in Java Pathfinder is simply that he had a concurrency bug that needed fixing.  Having never used it before, he managed to get it going fairly quickly and also find his bug --- which really got him thinking that this was a useful tool.

Anyway, I'm not going to say too much more about it, since I haven't looked more in detail yet.  But, it seems like a very impressive project, and definitely worth checking out.

## Further Reading

   *  The official Java Pathfinder page is [here](http://babelfish.arc.nasa.gov/trac/jpf)

   * Some useful news articles are [here](http://www.nasa.gov/centers/ames/news/releases/2005/05_28AR.html) and [here](http://www.nasa.gov/centers/ames/multimedia/images/2005/javapathfinder.html)

