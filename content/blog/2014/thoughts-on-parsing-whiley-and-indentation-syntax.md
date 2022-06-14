---
date: 2014-01-23
title: "Thoughts on Parsing Whiley (and Indentation Syntax)"
draft: false
---

Recently, I have been reworking the Whiley compiler's parser to make it more robust.  Doing this has opened up some interesting issues, which I thought were worth discussing.  Whiley uses indentation syntax without explicit end-of-statement terminators (e.g. semi-colons) and these things cause some interesting challenges.

Here's a simple Whiley function to illustrate:

```whiley
// Return least index of matching item in items
function indexOf([int] items, int item) => int|null:
    for i,v in items:
        if v == item:
            // match!
            return i
    // no match
    return null
```
## Indentation Syntax
Parsing indentation syntax is somewhat challenging.  The key challenge is determining *what* indentation is, and how to *compare* two pieces of indentation.  In Whiley, indentation consists of some amount of spaces and tabs. Whiley permits *irregular indentation*, meaning you can use different amounts of indentation in different places.  For example:

```whiley
function abs(int x) => int:
    if x < 0:
          return x
    else:
      return -x
```

In this example there are three *statement blocks* each of which has a different amount of indentation.  The function body has an indentation of 4 spaces, the true branch has an indentation of 4 + 6 spaces, whilst the false branch has an indentation of 4 + 2 spaces. This is quite valid in Whiley.

One of the key challenges in parsing indentation syntax is to provide a mechanism for *comparing* indentation.  Consider the following statements:
   * A statement block begins when the indentation-level of a statement is *greater* than the previous.

   * A statement block ends when the indentation-level of a statement is *less* than the previous.


Both of these statements rely on some way to compare the indentation of the current statement with the previous.  *But, how do we do that?*  The simplest approach is to translate tabs into spaces (e.g. 1 tab = 4 spaces).  Then, any piece of indentation is just a number of spaces and it's easy to compare.  Another approach is to treat indentation as a pair `(t,s)`, where `t` counts the number of tabs and `s` the number of spaces.  At this point, we can easily tell when we have the *same amount* of indentation, but *comparing* is more difficult.  It easy to see that `(1,1) < (1,2)` or that `(1,1) < (2,1)`, but what about comparing `(1,2)` and `(2,1)`?  In such a case, we could just report some kind of syntax error stating that there is a problem with indentation.

*What does Whiley do?* For now, I have chosen to go with a comparator for indentation where `indent1 <= indent2` only if `indent1` is a *prefix* of `indent2`.

*What do other languages do?*  Well, Python 2 translated tabs into spaces assuming a convention of 4 spaces per tab, whilst Python 3 and F# do not permit mixing tabs and spaces at all. See [this link](http://stackoverflow.com/questions/2178426/parsing-off-side-indentation-based-languages) for more.
## Statement Terminators
The age-old debate about statement terminators (e.g. `;`) has raged for a long time, with many people arguing that they should not be needed. For example, from the [Go Language FAQ](http://golang.org/doc/faq#semicolons):
> *"Semicolons, however, are for parsers, not for people, and we wanted to eliminate them as much as possible."*

To keep code clean and minimalistic, Whiley does not require statement terminators (e.g. `;`). The question, then, is how *to determine the end of a statement?* Certainly, we want to permit flexible use of whitespace to improve readability, such as breaking expressions across lines. Again, from Go Language FAQ:
> *"To achieve this goal, Go borrows a trick from BCPL: the semicolons that separate statements are in the formal grammar but are injected automatically, without lookahead, by the lexer at the end of any line that could be the end of a statement. This works very well in practice but has the effect that it forces a brace style. For instance, the opening brace of a function cannot appear on a line by itself."*

In thinking about this problem, there were some choices I considered:
   * **Explicit line breaks.**  This is a simple and effective approach permitted, for example, [in Python](http://www.python.org/dev/peps/pep-0008/).  Instead of requiring statement terminators, we provide explicit syntax for indicating that an expression or statement continues on the next line.  For example:
```whiley
function add(int width, int height, int depth) => int:
    return width + \
           height + \
           depth
```

Here, the `\` character is used to indicate that the current statement continues onto the next line. The advantage of this approach is that there are no surprises.

   * **Exploiting the offside rule**.  We can exploit the indentation syntax of Whiley to help with this problem.  We say that if the indentation on the line following an expression is greater than for the current statement, then it is assumed to be part of that expression.  For example:
```whiley
function add(int width, int height, int depth) => int:
    return width
           + height
           + depth
```

In this case, the indentation of `+` is greater than for `return` and, hence, exactly one expression is parsed. However, there are a few downsides with this rule, as is. Firstly, it does not permit this pattern:

```whiley
function add(int width, int height, int depth) => Box:
    return {
       width: width,
       height: height,
       depth: depth
    }
```

Unfortunately, this is a fairly common pattern in Whiley. Another issue with this approach is that it can make for harder-to-interpret errors. For example:

```whiley
function abs(int x) => int:
    if x >= 0
        return x
    else:
        return -x
```

In this case, the programmer has accidentally left off the `:` to terminate the `if` condition. The parser will then complain about the `return` on the *following line*, saying it's an invalid expression or similar.

   * **Greedy expression matching.**  Here, we continue matching an expression when what follows *must* be part of it.  For example:
```whiley
function add(int width, int height, int depth) => int:
    return width +
           height
           + depth
```

The parser, having matched the expression `width` will examine what follows, and see `+`. Since `+` never indicates the start of a statement in Whiley, the parser concludes the expression must continue. This lookahead ignores any kind of whitespace, including line terminators. This approach is neat, but there are some surprises (see below).

*What does Whiley do?* For now, I have chosen to go with greedy expression matching, as this gives the closest solution to our ideal. However, it places some awkward constraints on the syntax. For example, the left brace of a function invocation (e.g. `f(x)`) must be on the *same line* as the function name. This is because `(` is a valid start-of-statement token (e.g. `(int x,int y) = 1,2` is a valid variable declaration). Similarly, the left brace of a list access expression (e.g. `xs[i]`) must be on the same line as the source expression (i.e. `xs` in this case).  Perhaps the worst problem we've found so far is that `*` is a valid start of statement (e.g. `*x = 1`) and so one cannot break before `*` in a multiplication expression.
### Conclusion
Overall, it seems that choosing the syntax for a programming is no easy feat!  Indeed, Walter Bright recently made [some comments about this](http://www.drdobbs.com/architecture-and-design/so-you-want-to-write-your-own-language/240165488) for Dr Dobbs.  I definitely agree with Walter on most of his points, particularly the last one about parser generators.  I think writing the parser for a language is also an opportunity to really think about its syntax.

Anyway, that's enough for now!
