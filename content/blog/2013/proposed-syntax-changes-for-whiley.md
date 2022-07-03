---
date: 2013-12-19
title: "Proposed Syntax Changes for Whiley"
draft: true
---

Now that I've had the chance to write <a href="https://github.com/Whiley/WyBench/tree/master/src">a reasonable amount of code in Whiley</a>, it is time to reflect on some things I don't like.  In particular, there are a number of issues with the syntax which I'd like to improve which I'll document here.
<h2>Function &amp; Method Declarations</h2>
Currently, the syntax for a function or method in Whiley looks something like this:
[whiley]
int abs(int x) 
// Return value cannot be negative
ensures $ &gt;= 0:
    //
    if x &gt;= 0:
       return x
    else:
       return -x
[/whiley]
Although I quite like this syntax, I very much dislike the use of <code>$</code> for indicating the return value in a post-condition (i.e. <code>ensures</code> clause).  It occurred to me that explicit naming of the return value would be better (in fact, <a href="http://research.microsoft.com/en-us/projects/dafny/">Dafny</a> does this already).  So, my proposed new syntax for function declarations is thus:
[whiley]
function abs(int x) =&gt; (int y)
// Return value cannot be negative
ensures y &gt;= 0:
    //
    if x &gt;= 0:
       return x
    else:
       return -x
[/whiley]

Strictly speaking, the <code>function</code> keyword is not necessary, however I think it adds some clarity.  In particular, <em>functions</em> (which are <a href="http://en.wikipedia.org/wiki/Pure_function">pure</a>) now contrast explicitly with <em>methods</em> (which may have side-effects):
[whiley]
method read(String filename) =&gt; ([byte] y)
    //
    f = File.Reader(filename)
    ...
[/whiley]
Note, the syntax for <code>method</code> declarations is still not finalised, and I've been discussing it at length over in the issue tracker (see <a href="https://github.com/Whiley/WhileyCompiler/issues/204">here</a>, <a href="https://github.com/Whiley/WhileyCompiler/issues/309">here</a> and <a href="https://github.com/Whiley/WhileyCompiler/issues/310">here</a>).

The final aspect of the proposed syntax change is that <em>you can omit the return value when it's <code>void</code></em>.  For example, this:
[whiley]
void ::f(Reader r):
   ...
[/whiley]
becomes this:
[whiley]
method f(Reader r):
   ...
[/whiley]
Although this is hardly a significant issue, is still a nice abbreviation.

<h2>Type and Constant Declarations</h2>
One of things I really like about the current syntax for Whiley is the use of the <code>define</code> statement, which is simple and clean.  However, it turns out there were some issues I hadn't considered.  The issue revolves around the fact that the <code>define</code> keyword is overloaded.  For example:
[whiley]
define PI as 3.1415926536      // defines a constant
define nat as int where $ &gt;= 0 // defines a type!

nat f():
    return Math.round(PI)
[/whiley]
Here, we see <code>define</code> being used for defining a new constant, as well as a new type.  This is problematic, since the compiler must figure out which it is.  Of course, it can do that but, unfortunately, this leads to difficult to understand error messages.  The compiler initially assumes a <code>define</code> statement defines a type and, if this fails, backtracks and assumes it defines a constant.  For example:
[whiley]
define Point as {int x, int y,}
[/whiley]
This produces the following cryptic error message:
<pre>
./test.whiley:1: unrecognised term (int)
define Point as {int x, int y,}
                 ^^^
</pre>

The programmer was trying to define a new <code>Point</code> type, but accidentally left a comma at the end.  The error message is confusing, because it appears to say the <code>int</code> type doesn't exist!  Ultimately, the problem is that the compiler initially tried to parse it as a type, but failed.  So, it backtracked and tried for a constant which also failed, this time leaving an error message about why <code>{int x, int y,}</code> is not a well-formed expression.

The proposed syntax for constants and type declarations tries to be consistent with that for functions and methods:
[whiley]
constant PI is 3.1415926536      // defines a constant
type nat is (int x) where x &gt;= 0 // defines a type!
[/whiley]
With this syntax, there is no ambiguity between what is supposed to be a constant and what is supposed to be a type.  Furthermore, I've removed the <code>$</code> syntax in favour of explicitly named variables.

<h2>Local Variable Declarations</h2>
One of the unusual aspects of the current syntax is that you cannot <em>declare</em> local variables.  Whilst this is nice, it does causes problems.  One of these arises in verification, and wasn't something I initially considered.  For example:
[whiley]
define nat as int where $ &gt;= 0

nat sum([nat] xs):
   r = 0 // result
   i = 0 // index
   while i &lt; |xs| where i &gt;= 0 &amp;&amp; r &gt;= 0:
      r = r + xs[i]
      i = i + 1
   //
   return r
[/whiley]
This is a simple example illustrating that summing over a list of natural numbers yields a natural number.  The loop invariants are, alas, necessary for the verifier to accept this program.  In principle, the compiler could attempt to infer these loop invariants since they are simple.  

Another option (which I prefer) is to allow variable declarations:
[whiley]
nat sum([nat] xs):
   nat r = 0 // result
   nat i = 0 // index
   while i &lt; |xs|:
      ...
[/whiley]
Since <code>r</code> and <code>i</code> are both declared to be of type <code>nat</code>, the loop invariants are no longer required since the verifier will enforce the <code>nat</code> constraints at all program points.

There are still some unresolved questions here.  Firstly, I will probably still allow variables to be declared as <code>var</code>, which means they can take any type at any point.  Furthermore, flow typing will still be used to restrict the type of a variable in various ways.  For example:

[whiley]
int get(int|null x):
    if x is int:
        return x // automatically retyped
    else:
        return 0
[/whiley]

And, similarly:

[whiley]
int f():
    int|null x = null
    ... // here, x has type null
    x = 1
    ... // now, x has type int
    return x
[/whiley]

Finally, an interesting question is whether or not there is any difference between the declaration <code>any x</code> and <code>var x</code>.  I'll have to think about this!

<h2>References</h2>

Currently, reference types are denoted with the <code>ref</code>, like so:
[whiley]
define Point as {int x, int y}

void ::shift(ref Point p, int amount):
    p-&gt;x = p-&gt;x + amount
[/whiley]
Here, method <code>m</code> accepts a <em>reference</em> to a <code>Point</code> object.  Therefore, by assigning through it, we can produce a side-effect.  My proposed syntax change is <a href="http://static.rust-lang.org/doc/0.4/rust.html#type-system">inspired by the syntax for borrowed pointers in Rust</a>:

[whiley]
type Point is {int x, int y}

method shift(&amp;Point p, int amount):
    p-&gt;x = p-&gt;x + amount
[/whiley]

For some reason, I prefer <code>&</code> over e.g. <code>*</code> (as in C).  Also, it remains an open question as to whether or not to stick with the C-like notation of <code>-></code> for dereferencing fields and, likewise, <code>*</code> for general dereferencing.

Thoughts and comments welcome!!
