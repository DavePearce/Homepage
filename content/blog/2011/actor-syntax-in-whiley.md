---
date: 2011-05-16
title: "Actor Syntax in Whiley"
draft: false
---

Recently, I've been doing some work on the syntax for [Actors](http://wikipedia.org/wiki/Actor_model) in Whiley.  After some deliberation, I've decided to go with explicit syntax for both *synchronous* and *asynchronous* message sends.  This means any message can be sent either synchronously or asynchronously.  Obviously, sending asynchronously is preferable.  However, in cases where a return value is desired, then a synchronous send is necessary.

Here's a simple example:

```whiley
define Stack as process { [int] items }

int Stack::pop():
    item = items[|items|-1]
    this.items = items[:|items|]
    return item

void Stack::push(int item):
    this.items = items + [item]

Stack createStack([int] is):
    return spawn { items: is }

void System::main([string] args):
    stack = createStack([])
    stack<-push(1)
    stack<-push(2)
    val = stack<->pop()
    out<->println(str(val))
```

Here, `<-` denotes an asynchronous message send, whilst `<->` denotes a synchronous message send.  The order of delivery matches the order in which messages are sent.  Thus, `val` will always be assigned `2`.  When popping an item from the stack, we must send synchronously to use the result as, for an asynchronous send, the result is always discarded.

The beauty of actors is that they support implicit [synchronisation](http://wikipedia.org/wiki/Synchronization_(computer_science)).  That is, an actor can only process a single message at a time (e.g. `push(2)` cannot be interrupted on a given `Stack`). This means our `Stack` implementation is thread-safe by construction --- and we didn't need to use any explicit locks or `synchronize` blocks!

Quite a few questions remain about the syntax for actors in Whiley.  As it stands, there is no explicit receive mechanism (as found in e.g. [Erlang](http://wikipedia.org/wiki/Erlang_(programming_language))).  Furthermore, every message sent always contains a method which is evaluated lazily on the actor.  In principle, we might want to support sending plain data-types directly (again, like you do in Erlang).  My general feeling is that we'll add a mechanism to override the "default" receive method (which just invokes every message received).  Then, users will be able to implement mailbox prioritisation,  selective receiving, etc.

Finally, the current implementation of Whiley now has proper support for actors.  At the moment, every actor corresponds to a unique Java Thread (which is rather expensive).  In the future, we'll support [green threads](http://wikipedia.org/wiki/green_threads) as well (roughly following [Kilim](http://www.malhar.net/sriram/kilim/)).