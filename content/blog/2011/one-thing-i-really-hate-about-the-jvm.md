---
date: 2011-04-15
title: "One Thing I Really Hate About the JVM"
draft: false
---

Whilst I think the [Java Virtual Machine](http://wikipedia.org/wiki/Java_Virtual_Machine) is generally speaking a fantastic piece of kit, there is one thing that I really hate about it: *I can't stop threads!!!*

Sure, there's a method called `Thread.stop()` ... but it's *deprecated*.  There's even a whole article [devoted to why it should be deprecated](http://download.oracle.com/javase/1.4.2/docs/guide/misc/threadPrimitiveDeprecation.html).

Great.   That's just great.   *So, how do I stop a thread?*

Perhaps some context would be helpful.  I use an automated marking script that goes through each student's submission, runs their code and compares it against correct output.  It's really useful, and catches lots of subtle mistakes they make.

Now, the problem of course is that *student code doesn't always terminate.* That means I need a way to timeout their code within my marking system, and just report an error.

Which brings me back to the problem of *stopping threads*.  After the timeout, I must stop the thread executing the student's code --- otherwise, my system is quickly going to overload as I plough more and more student submissions into it.  A really good discussion on the right way to stop threads in Java can be found [here](http://forward.com.au/javaProgramming/HowToStopAThread.html).  The discussion revolves around interrupting running threads via `Thread.interrupt()`.  From my perspective, the salient quote is:
> Now if the thread has started, and was not in a [sleep()](http://java.sun.com/j2se/1.5.0/docs/api/java/lang/Thread.html#sleep%28long%29) or [wait()](http://java.sun.com/j2se/1.5.0/docs/api/java/lang/Object.html#wait%28long%29) method, then calling [interrupt()](http://java.sun.com/j2se/1.5.0/docs/api/java/lang/Thread.html#interrupt%28%29) on it does not throw an exception.

The issue is that, essentially, to stop a thread via this mechanism it must spend time in a `sleep()` or `wait()` method.  Now, I can try to force this to be true by putting it into a library method which the students must use.  But, usually, *their code still gets stuck in an infinite loop which doesn't call sleep or wait*.

Problem? Indeed.  My solution, whilst not exactly pretty, does seem to work most of the time --- *but, it relies on using the deprecated method `Thread.stop()`*.  You can get the code [here](/files/2011/StopTimerMethod.java).

The moral of the story seems to be: *don't stop people from doing useful things because they might do bad things.* Sure, the reasoning behind why `Thread.stop()` was deprecated is very sensible --- but, it forgets that there are valid use cases where it can be used without causing harm and, furthermore, is necessary for those use cases to work.

**UPDATE:** I should add that sandboxing is not really an issue here.  Each student's code already runs as a separate JVM process as an unpriviledged user.  However, there are many tests executed *for each student* which are currently controlled via threads.

**UPDATE:** An alternative implementation which uses `wait()` and `notify()` instead of Thread.sleep() can be found [here](/files/2011/WaitTimerMethod.java).  However, it doesn't appear to be as realiable as the original ... and if anyone can point out the bug(s), that would be appreciated!!!

**UPDATE:** Another alternative implementation which uses `ProcessBuilder` can be found [here](/files/2011/ProcessTimerMethod.java).  This seems to work pretty well.
