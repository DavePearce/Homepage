---
date: 2016-11-15
title: "Mixfix Function Syntax for Whiley"
draft: false
---

Today I saw an interesting talk about *mix-fix* function syntax.  The idea is to allow a more complex syntax for declaring and calling functions, rather than the very common "uniform" style. Consider the call, f`(x,y)`, to a single function (e.g. declared `f(int,int)`) accepting two parameters. With mixfix functions, we can break our function names into multiple components. Under this approach, a call to a single function might look like `f(x)g(y)` for a function declared `f(int)g(int)` accepting two arguments. The position of the arguments remains important in both the original and mixfix approaches, and the only real difference is the added semantic meaning that can be given.

*Why is this useful?* Well, it can help readability. Consider some interesting examples:

- `add(x)to(list)` --- add an item `x` to a collection `list`.
- `is(x)in(list)` --- check whether an item `x` is in a container `list`.
- `log(message)with(timestamp)` --- log a `message` string with a given `timestamp`.

The last example is particularly interesting as, combined with overloading, it can be quite powerful. For example we could have `log(message)`, `log(message)with(timestamp)` and `log(message)with(id)`.

### Syntax

Allowing support for mixfix functions could be useful for Whiley.  The question is how best to update the syntax. For the most part, I think this is reasonably straightforward.  The major challenge comes down to the question of *function types* and *function values*. Let's consider a simple example to illustrate the issue:

```whiley
function id(int x) -&gt; (int r):
    return x

function add(int x)to(int y) -&gt; (int r):
    return x+y
```

**Function Types**. The type of function `id()` is `(int)->(int)`. This type is used within the compiler in various situations. For example, when determining whether one method with the same name overloads another, we look at its name and type. So, what is the name and type for `add(int)to(int)`? Well, it could be `addto` and `(int,int)->(int)`. But, this would mean that it would clash with a function declared `addto(int,int)`. Another possibility is to have a clear separation in the name representing argument position. So, instead of `addto` we'd have e.g. `add*to` where `*` is used to signal a parameter "space". Thus, `add*to` no longer clashes with `addto`.

**Function Values**.  Currently, you can get a function value using the `&` operator. For example, the expression `&id` returns a value through which we can indirectly call `id()`. The type of this value is `(int)->(int)`. There are two questions then: firstly, what is the syntax for mixfix operators; secondly, what type is returned? For the first, we could require braces to signal parameters positions. Therefore, `&id` is no longer permitted and we have to write `&id()`. This then extends naturally to `&add()to()`. For the second issue, we could simply say that `&add()to()` returns a value of type `(int,int)->(int)`.

### Conclusion

Mixfix syntax seems reasonably easy to add to the Whiley language.  The question is whether or not it's worth the effort!  There aren't many languages I'm aware of that support this technique ([Grace](http://gracelang.org/applications/) is perhaps the only one I've seen).  My feeling is that, whilst this could take a little getting used to, it actually could be rather handy...
