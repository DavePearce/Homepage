---
date: 2010-10-11
title: "Function Pointer Syntax for Whiley"
draft: true
---

One of the next big issues needing to be addressed is the syntax for [[function pointers|function and method pointers]].  The syntax I'm thinking of is close to that used in C and C++ (see e.g. <a href="http://www.newty.de/fpt/fpt.html">here</a> and <a href="http://www.cprogramming.com/tutorial/function-pointers.html">here</a>).  My initial idea results in something like this:

[whiley]
define MyComparator as {
    int compareTo(MyRecord,MyRecord)
}
[/whiley]


This is defining a record type, <code>MyComparator</code>, which contains a single field, <code>compareTo</code>.  This field is, of course, a function pointer accept two <code>MyRecord</code>'s and returning an <code>int</code>.  We can invoke it like so:

[whiley]
int f(MyComparator comp, MyRecord r1, MyRecord r2):
    return comp.compareTo(r1,r2)
[/whiley]

Similarly, we can "take the address" of a function using the <code>&amp;</code> operator like so:

[whiley]
int comp(MyRecord r1, MyRecord r2):
    ...
    return 1 // positive case

void System::main([string] args):
    r1 = ...
    r2 = ...
    x = f(&amp;comp,r1,r2)
[/whiley]
At this point, the syntax seems a little inconsistent.  I'm using <code>&amp;</code> from C/C++, but not requring a <code>*</code> and/or allowing <code>-&gt;</code>.  Following C/C++ syntax properly would require something like this:
[whiley]
define MyComparator as {
    int (*compareTo)(MyRecord,MyRecord)
}

int f(MyComparator comp, MyRecord r1, MyRecord r2):
    return *(comp.compareTo)(r1,r2)
[/whiley]

To me, this is ugly and unnecessary.  On the other hand, perhaps I should consider dropping the <code>&</code> so everything is consistent.  E.g. by permitting this

[whiley]
void System::main([string] args):
    r1 = ...
    r2 = ...
    x = f(comp,r1,r2)
[/whiley]

This could work, for sure ... But, somehow I'm not a great fan.

<h2>Processes</h2>
Processes and methods present their own problem.  Consider this example:

[whiley]
define Writer as process {
 int Writer::write([byte] bytes)
}
[/whiley]

This declares a process type, <code>Writer</code>, which has one field, <code>write</code>.  In this case, <code>write</code> is a <em>method pointer</em>, as indicated by the <code>::</code> in the above.  It seems redundant to require <code>Writer::write</code>, but it's necessary since <code>write</code> could be a method pointer to some other kind of process.  This can be slightly improved by allowing a short-hand like this:

[whiley]
define Writer as process {
 int ::write([byte] bytes)
}
[/whiley]

And, in fact, I want to go further a permit this equivalent short-hand notation:

[whiley]
process Writer {
 int ::write([byte] bytes)
}
[/whiley]

But, somehow, this doesn't sit quite right for me.  I suppose I need to ponder some other options ...