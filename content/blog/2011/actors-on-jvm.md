---
date: 2011-03-22
title: "Actors on the JVM"
draft: false
---

The [Actor Model](http://wikipedia.org/wiki/Actor_Model) is an interesting alternative to the standard threading model used in languages like Java.  Its been around for a while, but [Erlang](http://www.erlang.org/) has recently brought it into the mainstream.  Roughly speaking, an actor corresponds to a Thread, except that *actors do not share state*.  Actors communicate by sending messages, and [Synchronisation](http://wikipedia.org/wiki/Synchronization_(computer_science)) is implicit because an actor can only process one message at a time.  Thus, a message is queued until the actor is free and can process it.

There are several examples of actors being implemented on top of the JVM, including [Kilim](http://www.malhar.net/sriram/kilim/) (see also [[1](http://java.dzone.com/articles/java-actors-with-kilim)][[2](http://www.javaworld.com/javaworld/jw-03-2009/jw-03-actor-concurrency2.html)][[3](http://www.infoq.com/news/2008/06/kilim-message-passing-in-java)]) and [Erjang](https://github.com/trifork/erjang/wiki) (see also [[1](http://www.javalimit.com/2009/12/erjang-why.html)][[2](http://www.infoq.com/interviews/thorup-erjang)][[3](http://medianetwork.oracle.com/media/show/15496)]).  These systems implement actors on top of JVM threads in a many-to-one fashion, where several actors can map onto a single thread at any given moment.  This requires the ability to interrupt a thread at certain points, in order to allocate it to another actor --- in otherwords, time-slicing several actors across a thread.  Unfortunately, the JVM does not provide any explicit mechanism for restarting threads at different bytecode locations, or for saving their state --- that is, it does not provide support for arbitrary [continuations](http://wikipedia.org/wiki/Continuation).

To work around the limitations of the JVM, systems like Kilim and Erjang intersperse special "pause" points throughout methods executed by actors.  A pause point is essentially a point where the executing thread checks whether or not it's time to switch to a different actor:  if so, the thread "backs out" of the current method by unwinding the stack, saving all necessary state (essentially, the contents of local variables) as it goes.  Once this is completed, the thread chooses another actor to execute, and "unpauses"  it by winding its saved state back onto the stack.

The following recursive method illustrates the main ideas:

```java
int height(Node n) {
 int left = height(n.left());
 int right = height(n.right());

 if(left < right) { return right; }
 else { return left; }
}
```

A system like Kilm will rewrite this method to insert pause points, producing something (very roughly) as follows:

```java
Object height(Node n, StackFrame stack) {
 Frame frame = null;
 int pc = 0;
 if(!stack.isEmpty()) {
  frame = stack.pop();
  pc = frame.pc();
 }
 switch(pc) {
 case 0:
   Object tmp = height(n.left(),stack);
   if(tmp instanceof StackFrame) {
    stack.put(new StackFrame(0));
    return stack;
   }
   frame.set(0,tmp);
 case 1:
   int left = (Integer) frame.get(0);
   Object tmp = height(n.right(),stack);
   if(tmp instanceof StackFrame) {
    stack.put(new StackFrame(1,left));
    return stack;
   }
   frame.set(1,tmp);
 case 2:
   int right = (Integer) frame.get(1);
   if(left < right) { return right; }
   else { return left; }
 }
}
```

Overall, it's pretty messy!  In practice, there's quite a bit more to it, including several optimisation opportunities.  But, hopefully, you get the picture --- we're essentially dividing the method up into points that we can quickly restore from.

There are quite a few questions left here.  In particular, *where do we put the pause points?* Kilim, for example, always puts them at calls sites to other methods (in fact, it uses a special annotation to indicate which methods are `@pausable`).  This approach means, for example, that a loop could contain no pause points and, hence, must run to completion without being interrupted (and, as with [nonpremptive multitasking](http://wikipedia.org/wiki/nonpremptive_multitasking), if that loop does not terminate then neither will the actor).  This seems less than ideal, but at the same time there doesn't seem to be a better alternative.
## Scheduling
Putting aside the question of where to locate pause points, there's another issue which has been vexing me for a while:  *how do we gain from this form of concurrency?* It seems to me there are two possibilities:

   * **I/O Bound.** If we have a situation where threads are often blocked on I/O, then getting them to continue executing other actors at these times would be beneficial.  The problem is that it seems unclear to me how this could be implemented because, once a thread has blocked inside an I/O method, then it's really stuck.  Perhaps, by using non-blocking I/O, this could be achieved.  Or, possibly, by creating one or more I/O threads which are actually responsible for reading/writing I/O and, hence, could be allowed to block.  The downside being that, if we had only one I/O thread, then this would sequentialise all I/O operations; alternatively, having many I/O threads would unnecessarily burden the system.

   * **Lock Contention.** Alternatively, if we have a situation where there is a lot of contention over some shared resource (i.e. where threads are often blocked waiting on a lock), then getting them to execute on behalf of other actors at these times could also be beneficial.  This seems more doable to me, as it just requires the lightweight thread scheduler to get access at the point of a message send.  Then, if the receiver is currently blocked processing some other message, the scheduler can direct the executing thread to pause the current actor (i.e. the sender).


My conclusion from thinking about this, is that there's little or no advantage from blocking actors unless they either: explicitly invoke an I/O operation, or send a message which cannot immediately be processed.  So, it's not really multi-tasking in the way that I think about *multi-threading* --- it's quite different, and much more specific.

Comments welcome!