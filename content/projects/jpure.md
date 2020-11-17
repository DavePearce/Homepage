---
draft: false
date: 2010-01-01
title: "JPure"
subtitle: "JPure"
description: "JPure is a novel purity system for Java that employs modularly checkable purity annotations."
tag: "jpure"
---

Purity Analysis is the problem of determining whether or not a method may have side-effects.  This has applications in automatic parallelisation, extended static checking, and more.  JPure is a novel purity system for Java that employs purity annotations which can be checked modularly.  For example, you can add `@Pure` annotations as follows:

```java
public class Doc {
  private final List<String> items = ...;
  
  ...
  
  @Pure boolean has(String x) {
    Iterator iter = items.iterator();
    while(iter.hasNext()) {
      String i = iter.next();
      if(x == i) { return true; }
    }
    return false;
   }
}
```

This is done using a flow-sensitive, intraprocedural analysis.  The system exploits two properties, called *freshness* and *locality*, to increase the range of methods that can be considered pure.  JPure also includes an inference engine for annotating legacy code.  JPure builds upon the [Java Compiler Kit]({{<ref "/projects/jkit" >}}).

**Notes.**  You will need to download the [ANTLR runtime](http://www.antlr.org/download) library as well (v3.2 last known to work); this should be loaded onto the classpath, or copied into the jpure/lib directory (where it should be picked up by the bin/jpure script). 
