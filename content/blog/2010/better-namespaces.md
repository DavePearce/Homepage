---
date: 2010-10-04
title: "Better Namespaces"
draft: false
---

An interesting problem I've encountered many times in Java is that of conflicting names.  For example,  suppose I have the following code:

```java
import wyil.lang.*;
import wyil.lang.Type.*;

...

public static Type T_Bool = new Type.Bool();
```

This is all well and good.  The problem arises when I want to import another class called `Type` from some package.  Suppose the `wyjc.lang` package contains a class called `Type`:

```java
import wyil.lang.*;
import wyil.lang.Type.*;
import wyjc.lang.*;

...

public static Type T_Bool = new Type.Bool();
public static wyjc.lang.Type WYJC_BOOL = new wyjc.lang.Type.Bool();
```

This is not really a problem --- it's just annoying.  I now have to provide full package information whenever I want to work with one of the `Type` classes.

What would be really great is to have a more flexible `import` statement, like [Python](http://wikipedia.org/wiki/Python_(programming_language)).  For example:

```java
import wyil.lang.*;
import wyil.lang.Type.*;
import wyjc.lang.Type as wyjcType;

...

public static Type T_Bool = new Type.Bool();
public static wyjcType WYJC_BOOL = new wyjcType.Bool();
```

This would be a nice and elegant way to resolve this problem of [namespaces](http://wikipedia.org/wiki/Namespace_(computer_science)).  So, I think I'll put something like this in Whiley ...

And, it looks like others are complaining as well ...  see [here](http://www.jelovic.com/articles/java_namespaces_suck_big_time.htm) and [here](http://tech.jonathangardner.net/wiki/Why_Java_Sucks#import_is_Useless).
