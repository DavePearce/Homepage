---
date: 2012-09-30
title: "Profiling Field Initialisation in Java"
draft: false
---

Recently, I attended the annual [Conference on Runtime Verification (RV2012)](http://rv2012.ku.edu.tr/) and gave a talk entitled "Profiling Field Initialisation in Java" (the paper itself is [here](/publications/NPN12_RV_preprint.pdf)).  This is the work of my PhD student, Stephen Nelson, and he should take all the credit for the gory details.  However, I thought I'd write up a little here, since the results are interesting to a general Java audience.

The paper is focused around measuring so-called *stationary fields* (a term coined in [another paper](http://dx.doi.org/10.1145/1328897.1328463)).  A field is stationary if the last write to that field in a given object occurs before its first read in that object and, furthermore, that this holds for all objects with that field.  Final fields are a perfect example of this, where the field is written in the constructor and, subsequently, can be read but not modified further.  However, stationary fields include more than just final fields.  For example, the first write of a field could occur after the constructor has completed (ignoring the default initialisation of course).  Initilisation of cyclic data structures is a common situation where this may happen:

```java
abstract class Parent {
  private final Child child;
  public Parent(Child c) { this.child = c; }
}

abstract class Child {
  private Parent parent; // cannot be marked as final
  public void setParent(Parent p) { this.parent = p; }
}
```

Here, the programmer intends that every `Parent` has a `Child` and vice-versa and, furthermore, *that these do not change for the life of the program*. He/she has marked the field `Parent.child` as `final` in an effort to enforce this. However, he/she is unable to mark the field `Child.parent` as `final` because one object must be constructed before the other.

In the above example, the method `Child.setParent(Parent)` is used as a *late initialiser*. This is a method which runs after the constructor has completed and before which the object is not considered properly initialised.  Another common situation where late initialisers are used is for classes with many configurable parameters. In such case, the programmer is faced with providing a single large constructor and/or enumerating many constructors with different combinations of parameters. Typically, late initialisation offers a simpler and more elegant solution.

We experimentally measured the number of stationary fields using a custom runtime profiling framework. The aim was to identity how many non-final stationary fields there were.  To do this, our profiler recorded all constructor exit/return events and field read/writes.  Using this, we determined which fields of each class were stationary.  The results we obtained for the [DaCapo benchmark suite](http://dacapobench.org) are:

{{<img class="text-center" width="100%" src="/images/2012/RV-results.png">}}

In the above chart, the height of each bar gives the percentage of fields which were observed to be stationary.  Each bar is then coloured to identify the sub-categories as follows: the black component indicates the number of stationary fields which were declared final; the light blue component indicates the number which could have been declared `final`; finally, the dark blue component indicates those stationary fields which couldn't be declared final (like e.g. `Child.parent` above).  

The results show a surprisingly large proportion of fields in the given Java programs are, in fact, stationary *but could not have been declared final*.  This suggests that further work developing language-level support for late initialisation would be useful.  One limitation of our experiment is that we gathered data only for one run (i.e. input) of each benchmark.  Ideally, it would have been nice to try multiple inputs to ensure as much of each benchmark had been tested as possible.

Anyway, if you're interested in this kind of thing, the [paper](/publications/NPN12_RV_preprint.pdf) contains a lot more details and information about exactly what we did ...
