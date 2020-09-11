---
date: 2020-09-01
title: "Whiley is Ten Years Old!"
draft: true
---

The [first commit](https://github.com/Whiley/WhileyCompiler/commit/0529dcf296877b89f0ddca2c942daff4a5e72429) recorded in the [WhileyCompiler](https://github.com/Whiley/WhileyCompiler) repository on Github is dated June 25th, 2010.  *That means Whiley has been going for just over ten years already!*  Wow, time sure does fly.  It's not a very interesting first commit though.  And, looking closely, you can see that commit is actually from the subversion repository that Whiley was originally hosted in.  The first real commit to the GitHub repository is dated [November 22, 2010](https://github.com/Whiley/WhileyCompiler/commit/63cb03b19b357757660b15058919c29f08fba3e5).  Again, it's not a very interesting commit!  Since those early commits, a lot has happened --- including around 7.6K more commits.  The first post on [whiley.org](http://whiley.org) is date [June 7th 2010](http://whiley.org/2010/06/07/software-engineering-disasters-video/), with the first talking about Whiley comming [on the same day](http://whiley.org/2010/06/07/wjc-status-update/).

Anyway, I'm not going to bore you with a full break down of everything that's happened!  Instead, here are a few highlights for me right now:

### Example Code

There are now a few example projects in Whiley which demonstrate its potential for building useful programs.  For now, these are currently mostly restricted to simple web applications:

{{<img class="inline-block float-left text-center" title="(Game of Life)" src="https://raw.githubusercontent.com/DavePearce/Conway.wy/master/conway.png" height="150px" alt="Game of Life Screenshot" link="https://davepearce.github.io/Conway.wy/">}}

{{<img class="inline-block float-left text-center" title="(Minesweeper)" src="https://raw.githubusercontent.com/DavePearce/Minesweeper.wy/master/assets/screenshot.png" height="150px" alt="Minesweeper Screenshot" link="https://davepearce.github.io/Minesweeper.wy/">}}

{{<img class="inline-block text-center" title="(Asteroids)" src="https://raw.githubusercontent.com/DavePearce/Asteroids.wy/master/assets/asteroids.png" height="150px" alt="Asteroids Screenshot" link="https://davepearce.github.io/Asteroids.wy/">}}

On top of this, a number of libraries have been written which are finding use in various ways (e.g. [STD.wy](https://github.com/Whiley/STD.wy), [JS.wy](https://github.com/Whiley/JS.wy), [DOM.wy](https://github.com/Whiley/DOM.wy), [Web.wy](https://github.com/DavePearce/Web.wy), [Ace.wy](https://github.com/DavePearce/Ace.wy), [LZ.wy](https://github.com/DavePearce/LZ.wy)).  There is even a simple online [package repository](https://github.com/Whiley/Repository)!

### Request For Comments (RFCs)

The [RFC process](https://github.com/Whiley/RFCs/) for developing extensions to the Whiley language has been running since around 2017.  At the time of writing, there are [30 RFCs](https://github.com/Whiley/RFCs/tree/master/text) of which around [15 have been completed](https://github.com/Whiley/WhileyCompiler/projects/9).  A few of the "big ones" which have gone through are:

* [RFC#23 (Templates)](https://github.com/Whiley/RFCs/blob/master/text/0023-templates.md).  This added support for _type polymorphism_ allowing us to types such as `vector<T>` (i.e. as in C++ templates Java generics).  This is a fairly fundamental feature which enabled significant improvements to the standard library (see e.g. [std::collections::vector](https://github.com/Whiley/STD.wy/blob/develop/src/whiley/std/collections/vector.whiley)).

* [RFC#58 (import with)](https://github.com/Whiley/RFCs/blob/master/text/0058-importwith.md).  This extended the syntax for `import` statements to support a `with` clause (e.g. `import std::vector with Vector`).

* [RFC#64 (for each)](https://github.com/Whiley/RFCs/blob/master/text/0063-foreach.md).  This finally added a syntax for `for`-loops to the language which had badly needed for quite a while!

* [RFC#66 (type inference)](https://github.com/Whiley/RFCs/blob/master/text/0066-type-inference.md).  This added support for _bidirectional_ type inference allowed type information to flow both up and down the Abstract Syntax Tree for an expression.  Using this, native types can be implemented more easily (e.g. native JavaScript strings can be represented directly in Whiley).

In addition to the formal RFC submissions, there are also a bunch of [lightweight ideas being floated around](https://github.com/Whiley/RFCs/issues) as well which may eventually be written up into an RFC.

### QuickCheck

Whilst the static verifier that comes with Whiley has slowly improved over the years, it remains difficult to use on large projects.  However, a surprising success has been the [QuickCheck for Whiley](https://whileydave.com/publications/chin18_engr489/) originally developed by Janice Chin.  The essential idea behind this tool arose from the original [QuickCheck for Haskell](https://en.wikipedia.org/wiki/QuickCheck) tool developed by John Hughes and Koen Claessen.  Essentially, this tool tests Whiley programs by generating inputs automatically based on certain criteria (e.g. for integers `-3..3`, arrays of max length `3`, etc).  For example, consider this (broken) program:

```
function find(int[] items, int item) -> (int r)
ensures r >= 0 && r < |items|:
    for i in 0..|items|:
       if items[i] == item:
          return i  // match
    return -1       // no match
```

```
> wy --verbose check
```

```
Failed function main::find(int[],int)->int
==========================================
./src/main.whiley:6: postcondition not satisfied
--> main::find([],0)
    return -1       // no match
    ^^^^^^^^^
Stack Trace:
--> main::find([],0)
```
### Other

* WebAssembley.  Quad Copter.

* Pygments

* Teaching in China and SWEN224, SWEN326.
