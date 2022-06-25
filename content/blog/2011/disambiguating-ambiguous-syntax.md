---
date: 2011-06-28
title: "Disambiguating Ambiguous Syntax?"
draft: false
---

When designing a programming language, being on the lookout for ambiguous syntax is important.  You don't want to realise down the track that your syntax is ambiguous in some subtle way. This is especially true if it means the compiler can't decide how to proceed on some important case(s).  But, spotting these problems is not easy, especially when there are unexpected interactions between constructs.

## The Context

In the latest release of Whiley, I have relented to peer pressure and backed off from my earlier syntax for message sends.  To recall, this used `<->` and `<-` to signal synchronous and asynchronous message sends.  In particular, several people commented on how cumbersome `<->` was.  Instead, the latest version of Whiley uses plain old `.` for synchronous sends, and `!` for asynchronous (i.e. like [Erlang](http://wikipedia.org/wiki/Erlang_(programming_language))).  Whilst in many ways this is rather nice, it does leave open an interesting question of *ambiguity*.

There are essentially two invocation forms the compiler encounters: `func()` and `x.meth()`.  The former is straightforward as it always corresponds to a function invocation.  The latter, however, is more subtle as it has two possible interpretations: a *direct message send*, or an *indirect function invocation* via a field dereference.

Here's an example to illustrate a direct message send:

```whiley
define MyProc as { int data }

int MyProc::func():
    return this.data

int client(MyProc p):
    return p.func() // direct message send
```

And, here's an example to illustrate an indirect function invocation via field dereference:

```whiley
define MyRec as { int() func }

int client(MyRec p):
    return p.func() // indirect function invocation
```

The question is: *how does the compiler disambiguate this?* Well,the (current) rule is simple:
> If a matching external symbol exists then its a direct message send; otherwise, it's an indirect function invocation via field dereference.

In otherwords, priority is given to *direct message sends*.  At this point, alarm bells should be starting to go off.  *Why?* Well, because external symbols may come and go --- we have no control over them, and we don't want changes in external libraries to affect the *semantics* of our code.  Considering the last example, suppose we ended up accidentally importing a matching external symbol:

```whiley
// following symbol from some imported module
int MyProc::func():
    ...

define MyRec as { int() func }

int client(MyRec p):
    return p.func() // indirect function invocation
```

Well, this code no longer compiles because `func` is resolved as an external symbol and, hence, the compiler is expecting `p` to have type `MyProc`.
## The Problem
Now, it seems like the above problem just stems from a bad choice of rule.  Perhaps an alternative ruling would be better?  Well, we could give priority to field-dereferences.  This way, external symbols can't affect field-dereferences.  But, now, field dereferences can affect external symbols!   To see why, consider this:

```whiley
define MyProc as {
 int data,
 int func()
}

int MyProc::func():
    return this.data

int client(MyProc p):
    return p.func() // what is it???
```

The problem here is that *either interpretation makes sense*.  We're either indirectly invoking a function pointer stored in field `func` of process `p`, or making a direct message send to `p`.  Of course, we can choose how to resolve this (i.e. either field dereference or direct message send gets priority).

The problem is, whichever choice we make, it remains the case that *a change in an imported module can affect the semantics of our code*. To see why, assume `MyProc` is defined in an imported module and that some change occurs to that module outside of our control.  There are two cases:
   * **Dereferences get priority.** Then the `MyProc` changes from not including `func` to including `func` (so our client code changes from a direct send to an indirect invoke).

   * **Direct sends get priority.** Then the method `func` doesn't exist initially, but is added later (so our client code changes from an indirect invoke to a direct send).


Neither of these options seems desirable ... *but what to do?*## The Solution?
Basically, at this stage, I don't have a solution ... so suggestions welcome!

One option is to make a clear distinction between dereferencing a field in a record from one in a process.  For example, we could view processes as "pointers" to records, and then use C/C++ syntax such as `p->func` to indicate we're indirectly accessing field `func` in process `p`.

What I don't like about this approach is the discrepancy between message send and process access syntax.  For example, something like `sys.out.println` becomes `sys->out.println()` (this is a direct message send to the process referenced by field `out` in the process referenced by `sys`).