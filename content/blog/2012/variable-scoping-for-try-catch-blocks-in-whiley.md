---
date: 2012-05-22
title: "Variable Scoping for Try-Catch Blocks in Whiley"
draft: false
---

A friend of mine was talking about how variable scoping for `try-catch` blocks in Java really frustrated him sometimes.  Specifically, the problem was related to variables declared inside `try` blocks not being visible in their `catch` handlers.  The example would go something like this:

```java
int val;
try {
 int tmp = f(); // cannot throw MyException
 val = g(tmp); // throws MyException
} catch(MyException e) {
 log(tmp);
 throw e;
}
return h(val);
```

This code does not compile because `tmp` is not in scope inside the `catch` handler.  Of course, we can declare `tmp` outside the `catch` handler --- but this is mildly annoying because it's only used within that block!

Anyhow, during the discussion I realised that Whiley doesn't have this problem because it uses flow typing.  In fact, there is no real notion of scoping for local variables.  The rule is fairly simple: *if the variable definitely has a value then it's in scope*.  The above example would then look like this in Whiley:

```whiley
try:
   tmp = f() // cannot throw MyException
   var = g(tmp) // throws MyException
catch(MyException e):
   log(tmp)
   throw e
// var is in scope
return h(var)
```

Here, the flow typing system will reason that there is no control-flow branch within the block until after `tmp` is assigned.  Therefore, `tmp` can be safely used within the `catch` handler.  

Ok, so this particular aspect of flow typing is hardly going to set the world on fire ... but I thought it was quite neat!
