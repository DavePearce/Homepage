---
date: 2010-10-18
title: "Indentation Syntax in Whiley"
draft: false
---

Like Python, Whiley uses indentation syntax instead of curly braces for delimiting blocks.  When I started using indentation syntax with Python, I was pretty skeptical, but  it grew on me fast and now I really like it.  However, this wasn't the only reason I chose to use indentation syntax in Whiley.  The other is that *we’d have too many braces otherwise*.  The thing is, sets and [set comprehensions ](http://en.wikipedia.org/wiki/List_comprehension)are  very important in Whiley, and their syntax uses curly braces (e.g. `{1,2,3}` and `{ x+1 | x in xs, x > 0 }`).  So, indentation syntax is one way to reduce the amount of curly (or other) braces.

One of the challenges with indentation syntax is the treatment of [whitespace](http://wikipedia.org/wiki/whitespace_character).  In traditional languages, characters including newlines, tabs and spaces are dropped by the lexer.  With indentation syntax, this is not possible as newlines and tabs form part of the syntax.  By itself, this is straightforward to handle.  The main issue arises when we want to wrap lines.  For example, consider the following:

```whiley
int f(int x):
    x = x +
    1
    return x
```

The question is whether or not this is syntactically correct.  More importantly, if we decide it is, *then how does the parser know when to ignore newlines and tabs?*

My answer to this is surprisingly simple.  When parsing an incomplete expression, tabs, newlines, spaces (and other forms of whitespace) are ignored.  Only once an expression is completed, are they are again used for determining indentation.  Thus, the above example is syntactically correct in Whiley.  However, the following is not:

```whiley
int f(int x):
    x = x
    + 1
    return x
```

This is not syntactically correct because `x = x` is considered a complete statement and, thus, the parser is expecting a new statement at `+1`.

This approach is similar, but not identical, to the way Python handles line wraps (see [here](http://docs.python.org/reference/lexical_analysis.html) for specifics).  Python distinguishes *implicit* line wraps from *explicit* ones.  An explicit line wrap is denoted using the `\` symbol.  For example, the following is valid Python:

```whiley
def f(x):
    x = x \
    + 1
    return x
```

An implicit line wrap is one which is permitted without using the `\` symbol.  In Python, expressions in parentheses, square brackets or curly braces can be split over multiple lines without using an explicit line wrap.  In Whiley, I have essentially just taken this a bit further to include *any incomplete expression*, not just those involving e.g. curly braces.

I suppose the real question is whether or not Whiley should also support an explicit line wrap operator.  For now, I'll just defer this decision as it's not mission critical ...
