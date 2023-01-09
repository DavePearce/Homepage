---
date: 2013-01-14
title: "Whiley Puzzler"
draft: false
---

I was having an interesting discussion with a colleague today about various aspects of Whiley, and we came up with an interesting bit of example code which is something of a puzzler.  Consider these two different versions of a function `f(any)`:

Version 1:

```whiley
int f(any x):
    if x is string:
        return g(x)
    else:
        return g(x)
```

Version 2:

```whiley
int f(any x):
    return g(x)
```

The question is: *are the above two examples equivalent or not?* (answer below)

{{<img class="text-center" src="/images/2013/Answer.png" width="300px">}}

Well, the answer is: *no, they're not equivalent*.  You probably guessed that, but the question is: *why not?* Well, consider these two definitions for the function `g()` used in the examples:

```whiley
int g(any x):
    return 1

int g(string x):
    return 2
```

In the first example, after the type test `x is string` Whiley's flow-type system automatically retypes variable `x` to have type `string`.  Therefore, the call `g(x)` on the true branch is statically dispatched to `g(string)`, whilst on the false branch it dispatches to `g(any)`.

Anyhow, this puzzler is an interesting (and perhaps surprising) artifact of the choice to employ flow typing in Whiley.  I wonder what other artifacts we'll find...
