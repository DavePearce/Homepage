---
date: 2010-10-11
title: "Function Pointer Syntax for Whiley"
draft: false
---

One of the next big issues needing to be addressed is the syntax for {{<wikip page="Function_pointer">}}function and method pointers{{</wikip>}}.  The syntax I'm thinking of is close to that used in C and C++ (see e.g. [here](http://www.newty.de/fpt/fpt.html) and [here](http://www.cprogramming.com/tutorial/function-pointers.html)).  My initial idea results in something like this:

```whiley
define MyComparator as {
    int compareTo(MyRecord,MyRecord)
}
```


This is defining a record type, `MyComparator`, which contains a single field, `compareTo`.  This field is, of course, a function pointer accept two `MyRecord`'s and returning an `int`.  We can invoke it like so:

```whiley
int f(MyComparator comp, MyRecord r1, MyRecord r2):
    return comp.compareTo(r1,r2)
```

Similarly, we can "take the address" of a function using the `&` operator like so:

```whiley
int comp(MyRecord r1, MyRecord r2):
    ...
    return 1 // positive case

void System::main([string] args):
    r1 = ...
    r2 = ...
    x = f(&comp,r1,r2)
```
At this point, the syntax seems a little inconsistent.  I'm using `&` from C/C++, but not requring a `*` and/or allowing `->`.  Following C/C++ syntax properly would require something like this:
```whiley
define MyComparator as {
    int (*compareTo)(MyRecord,MyRecord)
}

int f(MyComparator comp, MyRecord r1, MyRecord r2):
    return *(comp.compareTo)(r1,r2)
```

To me, this is ugly and unnecessary.  On the other hand, perhaps I should consider dropping the `&` so everything is consistent.  E.g. by permitting this

```whiley
void System::main([string] args):
    r1 = ...
    r2 = ...
    x = f(comp,r1,r2)
```

This could work, for sure ... But, somehow I'm not a great fan.

<h2>Processes</h2>
Processes and methods present their own problem.  Consider this example:

```whiley
define Writer as process {
 int Writer::write([byte] bytes)
}
```

This declares a process type, `Writer`, which has one field, `write`.  In this case, `write` is a <em>method pointer</em>, as indicated by the `::` in the above.  It seems redundant to require `Writer::write`, but it's necessary since `write` could be a method pointer to some other kind of process.  This can be slightly improved by allowing a short-hand like this:

```whiley
define Writer as process {
 int ::write([byte] bytes)
}
```

And, in fact, I want to go further a permit this equivalent short-hand notation:

```whiley
process Writer {
 int ::write([byte] bytes)
}
```

But, somehow, this doesn't sit quite right for me.  I suppose I need to ponder some other options ...
