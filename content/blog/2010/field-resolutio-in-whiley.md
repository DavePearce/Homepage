---
date: 2010-10-05
title: "Field Resolution in Whiley"
draft: true
---

An interesting issue has arisen as a result of my recent [decision](http://whiley.org/2010/09/22/on-flow-sensitive-types-in-whiley/) to move away from a declared-type model.  The issue is essentially about [scope resolution](http://wikipedia.org/wiki/Scope_(programming)) of fields and local variables.   For example, consider the following:

```whiley
define MyProc as process { int f }

void MyProc::m(int x):
    f = x
```

Now, `m(int)` is a method acting on a process of type `MyProc`.  Thus, the special variable `this` has type `MyProc` (much like it would in [Java](http://wikipedia.org/wiki/Java)).  So, the question is: does the assignment `f = x` create a new local variable, or update field `f` in the process referred to by `this`?  In Java, this problem wouldn't arise since we would have to have declared variable `f`, in order for it to be treated as a local.

There are a few different ways this could be resolved:
   * We always default to a field if one of the given name exists.

   * We always default to a local variable if one of the given name exists, and always for assignments.


The problem with (1) is that it means a later change to the fields of some type may affect the meaning of methods declared elsewhere in subtle, and hard-to-track ways.  This is because we might add a new field to some existing type, and that field now clashes with what was a local variable in some method (which, following (1), would now refer to the new field).  For this reason, I prefer option (2).  Thus, in the above example, the assignment creates a new local variable which will subsequently be the default scope for variables named `f`.

If we want to update the field `f` in the process referred to by `this`, we need to be explicit:

```whiley
define MyProc as process { int f }

void MyProc::m(int x):
    this.f = x
```

Thus, only in the case that a matching local variable does not exist will the system infer that a variable is, in fact, a field access.  The following example illustrates:

```whiley
void System::main([string] args):
    out.println("Hello World")
```

Here, `out` does not correspond to a local variable and, since `System` is a process type with a field `out`, it is automatically expanded to the following:

```whiley
void System::main([string] args):
    this.out.println("Hello World")
```

Anyway, this seems to be the best way to resolve this particular issue ... I guess time will tell!