---
date: 2014-09-05
title: "A Story of Cast Expressions"
draft: false
---

Issue {{<issue id="427">}} "*Bug with Dereferencing Array Types*" seemed like just another bug.  Submitted by a user last week ([@Matt--](https://github.com/Matt--)), I didn't think too much of it.  But, as sometimes happens, appearances can be deceiving.  In this case, the bug identified a flaw in the syntax of Whiley with respect to the treatment of cast expressions!

Cast expressions are well known to be a thorny issue.  In Java, there are [well-known limitations with parsing cast expressions](http://stackoverflow.com/questions/400895/how-does-a-java-compiler-parse-typecasts).  *For example, did you know this Java program is invalid?*

```java
public class Test {
    public static void main(String[] args) {
       Double y = new Double(0.1);
       System.out.println((Double) -y);
    }
}
```

Compiling this gives a rather cryptic error message (with `javac`):

```
Test.java:6: cannot find symbol
symbol  : variable Double
location: class Test
       System.out.println((Double) -y);
                           ^
Test.java:6: illegal start of type
       System.out.println((Double) -y);
                          ^
2 errors
```

The problem is that the Java compiler cannot determine whether the name `Double` refers to a type (in which case we have a cast) or some other variable (in which case we have a bracketed expression). For example, it could be a field of some sort declared in this or another class. Since, at the point of parsing the source file, the process of {{<wikip page="Name_resolution_(programming_languages)">}}name resolution{{</wikip>}} has not been performed, the parser does have the information necessary to disambiguate these cases. Of course, we could integrate the parse and name resolution stages together, but this introduces a lot of unnecessary coupling and complexity (and the designers of Java obviously decided against this).

To work around this problem, the Java grammar prevents the use of a non-primitive type in a cast when followed by certain expressions that cause ambiguity (e.g. `-y`, `+y`, etc).``

## The Problem
The problem of parsing cast expressions in Whiley is largely the same as for the Java compiler, although Whiley has different syntax which needs to be accounted for. The example from Issue {{<issue id="427">}} which exposed the problem is the following:

```whiley
method main(System.Console console):
    &[real] b = new [1.0, 2.0, 3.0]
    real x = (*b)[1]
    ...
```

The Whiley compiler was reporting that `(*b)` was not a valid type. This is because it had mistakenly considered the expression `(*b)[1]` to be cast. Clearly it's not, and it should never have thought this! But, solving this is not completely straightforward.

To really understand the problem, we need to consider how the Whiley compiler was originally disambiguating casts from bracketed expressions.  Roughly speaking the procedure upon encountering a left brace, `(`, was:

   * **Attempt to determine whether what follows is definitely a type**.  The structure following a `(` is definitely a type if it contains connectives or keywords that can only appear in a type, but not an expression.  An example is the keyword `int` which denotes the type of integers.  Since it is a keyword, it may not be used as a variable name.  Another example is a record type (e.g. `{Point p, Color c}` which cannot be an expression.
   
   * **If not definitely a type, parse as expression**.  At this point, we know the structure following the `(` can be safely parsed as an expression.  However, it may still represent a type and be part of a cast expression.  For example, in `(Point) x` we can safely parse `Point` as an expression, but it actually represents a type.
   
   * **Determine whether what follows is the start of an expression**.  Having parsed the contents of the bracketed expression, we now look at what follows.  If nothing follows, then we clearly have a bracketed expression.  Otherwise, what follows may be either: **(1)** a token which can only occur at the start of an expression; **(2)** a token which can only occur in the middle of an expression; **(3)** a token which can occur in both situations.  An example of case **(1)** is `(N) x` and we would proceed assuming this was a cast expression and that `N` denotes a type.  An example for case **(2)** is `(N) && b` and we proceed assuming a bracketed expression, and that `N` denotes a variable or constant of some sort.  Finally, the question arises as to whether there are any tokens in category **(3)**.  Initially, I thought that only the binary operators `+` and `-` could be in category **(3)**.  Following Java, I was happy to prohibit these operators from being used at the beginning of an expression following a cast to a non-primitive type.

At this point, you may have spotted some critical mistakes in the above logic.  Unfortunately, I didn't until recently! There are two problems with the above:

   * **There are more tokens in category (3) than original considered**.  In particular, the left square brace, `[` can denote the start of a list constructor (e.g. `[1,2,3]`) and part of a list access expression (e.g. `(*x)[i]`); similarly, the dereference operator, `*`, can denote the start of a dereference expression (e.g. `*ptr`) and part of a multiplication (e.g. `x * y`). Both of these uses are unique to Whiley and not present in Java.
   
   * **There is a class of tokens which can be part of an expression, but not part of a type**. A good example here is the dereference operator, `*`. So, in parsing `(*b)[i]` the compiler should realise that `*b` could never be a valid type and should proceed assuming it was a bracketed expression.


## The Solution
The solution at this point is fairly straightforward. I've simply added an additional stage in the process between steps (2) and (3), which checks whether the parsed expression must definitely be an expression or not.  This is a simple solution but, like Java, it still leaves open some cases which the compiler cannot disambiguate. These all involve the small set of tokens which can appear both at the start of an expression and in the middle of an expression. This set, to the best of my knowledge, is: `+`, `-`, `[`, `*` `&` and `|`. Thus, the following expression forms cannot be correctly disambiguated: `(N) + e`, (`N) - e`, `(N) * e`, `(N) & e`, `(N) | e`, `(N) [ e ]`.

At the moment, when such an ambiguity arises, the compiler takes a punt and assumes it has a bracketed expression, rather than a cast. Unfortunately, this results in difficult to understand error messages. In the future, it might be better for the compiler to simply report the ambiguity rather than proceeding on an assumption...
