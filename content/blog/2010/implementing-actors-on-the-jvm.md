---
date: 2010-10-25
title: "Implementing Actors on the JVM"
draft: false
---

Whiley adopts the [Actor model](http://wikipedia.org/wiki/Actor_model) of concurrency, instead of the traditional [multi-threading](http://wikipedia.org/wiki/Thread_(computer_science)) approach used in e.g. Java.  The actor model is simple and easy to use, and is less likely to result in complex [race conditions](http://wikipedia.org/wiki/race_condition) or [deadlocks](http://wikipedia.org/wiki/deadlock). The Actor Model has been around for a while, but [Erlang](http://www.erlang.org/) has recently brought it into the mainstream.  Roughly speaking, an actor corresponds to a Thread.  The key difference is that there is *no shared state between actors*.  This differs from e.g. Java, where two threads can hold a reference to the same object and, hence, there is a problem of contention. To communicate, actors send messages to each other containing whatever data is necessary.  [Synchronisation](http://wikipedia.org/wiki/Synchronization_(computer_science)) is implicit in the Actor Model, since an actor can only process one message at a time.  Thus, a message is queued until the actor is free and can process it.  Interesting questions arise regarding priority of messages (i.e. the order in which queued messages are processed), but that's another story!

## Background
Whiley runs on the JVM, and we must find a way to implement Actors efficiently.  It turns out there is quite a lot of information available on this, including the following papers:

   * **Kilim: Isolation-typed actors for Java**, Sriram Srinivasan and Alan Mycroft.  In Proceedings of ECOOP, pages 104--128, 2008. [[PDF](http://citeseerx.ist.psu.edu/viewdoc/download?doi=10.1.1.145.8349&rep=rep1&type=pdf)]

   * **Actor frameworks for the JVM platform: a comparative analysis**, Rajesh K. Karmani and Amin Shali and Gul Agha.  In Proceedings of PPPF, pages 11-20, 2009. [[PDF](http://citeseerx.ist.psu.edu/viewdoc/download?doi=10.1.1.147.9277&rep=rep1&type=pdf)]


I was lucky enough to spend some time with Sriram in 2009, and his thoughts and ideas on this subject really motivated me to use Actors in Whiley.  Kilim has become quite widely known, and there many articles discussing it (see e.g. [[1](http://www.javaworld.com/javaworld/jw-03-2009/jw-03-actor-concurrency2.html)][[2](http://java.dzone.com/articles/java-actors-with-kilim)][[3](http://www.infoq.com/news/2008/06/kilim-message-passing-in-java)]).  Sriram also gave a nice Google Talk:

{{<youtube id="37NaHRE0Sqw" width="560" height="315">}}

Kilim was used as the starting point for [Erjang](http://github.com/krestenkrab/erjang/wiki) --- an effort to port Erlang to the JVM.  You can see more from Kresten Krab Thorup (Erjang author) about this [here](http://www.infoq.com/interviews/thorup-erjang).

## Understanding Kilim
Anyway, enough background ... *what is Kilim and how does it work?*

Java Threads are really rather heavyweight, and consume a lot of resources.  Typically, the JVM is quite [limited in the number of threads it can handle](http://blog.krecan.net/2010/04/07/how-many-threads-a-jvm-can-handle/), compared with the number of objects (e.g. 10s of thousands versus millions on a typical desktop).  Furthermore, [context switches between threads are slow](http://www.javamex.com/tutorials/threads/thread_scheduling_java.shtml).  For these reasons, Kilim was created to give lightweight threads (aka *fibers*) on the JVM, where we have just one Java Thread per available CPU.  This is done using something akin to *[Continuation Passing Style](http://wikipedia.org/wiki/Continuation-passing_style)* (CPS)]].

Kilim modifies the bytecode of a Java method to insert "pausable" points (i.e. points where a lightweight thread can be paused).  When a lightweight thread "pauses" at a pausable point, Kilim saves the current state of all local variables and exits the method.  The "pause" is then propagated up the calling chain, such that callers  are also paused, etc (in fact, for this reason, pausable points are always placed at method invocations).  Each caller then saves it's own state as the stack unwinds, until we reach the top.

At this point, the paused lightweight thread can be restarted whenever the Kilim scheduler chooses.  Restarting a lightweight thread is basically the opposite of pausing one: we recreate the calling stack by restoring each caller from its saved state.  For efficiency, Kilim restores the state of a given method lazily --- so, the currently executing method is restored first, and only when this terminates is the caller restored, etc.  This is important to reduce the overheads on long running methods, where there may be multiple pauses during execution.

There are quite a few challenges in actually making this work on the JVM.  For starters, Kilim must add an extra parameter to every method to pass through the state of the executing lightweight thread.  Similarly, every method invocation must be modified to enable the pausing/unpausing, which is made difficult because the JVM places strict requirements on the state of local variables and the stack.  For example, one must carefully ensure the stack height is exactly correct after unpausing, and that the types of elements in the stack/local variable array are set correctly (i.e. it is not enough for them to refer to objects of the right type, the JVM must know they do).  More details of how this is all done can be found [here](http://www.malhar.net/sriram/kilim/thread_of_ones_own.pdf).

The question remaining for me, of course, is how I translate all this into the context of Whiley.  This is non-trivial because Kilim requires that pausable methods be identified up-front by the user.  However, in Whiley, I do not want any user intervention --- it must be seemless.  Therefore, *do I simply make all methods and functions pausable*? Or, *do I make only methods pausable*? Or, *do I apply heuristics and make careful decisions on exactly which methods to make pasuable?* Or, *is there something I haven't thought of?* As usual, only time will tell how this plays out ...
