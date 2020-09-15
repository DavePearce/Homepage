---
date: 2020-09-01
title: "Whiley is Ten Years Old!"
draft: false
---

The [first commit](https://github.com/Whiley/WhileyCompiler/commit/0529dcf296877b89f0ddca2c942daff4a5e72429) recorded in the [WhileyCompiler](https://github.com/Whiley/WhileyCompiler) repository on Github is dated June 25th, 2010.  *That means Whiley has been going for just over ten years already!*  Wow, time sure does fly.  It's not a very interesting first commit though.  And, looking closely, you can see that commit is actually from the subversion repository that Whiley was originally hosted in.  The first real commit to the GitHub repository is dated [November 22, 2010](https://github.com/Whiley/WhileyCompiler/commit/63cb03b19b357757660b15058919c29f08fba3e5).  Again, it's not a very interesting commit!  Since those early commits, a lot has happened --- including around 7.6K more commits.  The first post on [whiley.org](http://whiley.org) is dated [June 7th 2010](http://whiley.org/2010/06/07/software-engineering-disasters-video/), with the first talking about Whiley coming [on the same day](http://whiley.org/2010/06/07/wjc-status-update/).

Anyway, I'm not going to bore you with a full break down of everything that's happened since then!  Instead, here are a few highlights for me right now:

### Actual Code

There are now a few projects in Whiley which demonstrate its potential for building useful programs.  For now, these are currently mostly restricted to simple web applications:
{{<showcase>}}

{{<img class="text-center" title="(Game of Life)" src="https://raw.githubusercontent.com/DavePearce/Conway.wy/master/assets/conway.png" height="150px" alt="Game of Life Screenshot" link="https://github.com/DavePearce/Conway.wy">}}

{{<img class="text-center" title="(Minesweeper)" src="https://raw.githubusercontent.com/DavePearce/Minesweeper.wy/master/assets/screenshot.png" height="150px" alt="Minesweeper Screenshot" link="https://github.com/DavePearce/Minesweeper.wy/">}}

{{<img class="text-center" title="(Asteroids)" src="https://raw.githubusercontent.com/DavePearce/Asteroids.wy/master/assets/asteroids.png" height="150px" alt="Asteroids Screenshot" link="https://github.com/DavePearce/Asteroids.wy/">}}

{{</showcase>}}

On top of this, a number of libraries have been written which are finding use in various ways (e.g. [STD.wy](https://github.com/Whiley/STD.wy), [JS.wy](https://github.com/Whiley/JS.wy), [DOM.wy](https://github.com/Whiley/DOM.wy), [Web.wy](https://github.com/DavePearce/Web.wy), [Ace.wy](https://github.com/DavePearce/Ace.wy), [LZ.wy](https://github.com/DavePearce/LZ.wy)).  There is even a simple online [package repository](https://github.com/Whiley/Repository)!

### Request For Comments (RFCs)

The [RFC process](https://github.com/Whiley/RFCs/) for developing extensions to the Whiley language has been running since around 2017.  At the time of writing, there are [30 RFCs](https://github.com/Whiley/RFCs/tree/master/text) of which around [15 have been completed](https://github.com/Whiley/WhileyCompiler/projects/9).  A few of the "big ones" are:

* [RFC#23 (Templates)](https://github.com/Whiley/RFCs/blob/master/text/0023-templates.md).  This added support for _type polymorphism_ allowing us to use types such as `vector<T>` (i.e. as in C++ templates or Java generics).  This is a fairly fundamental feature which enabled significant improvements to the standard library (see e.g. [std::collections::vector](https://github.com/Whiley/STD.wy/blob/develop/src/whiley/std/collections/vector.whiley)).

* [RFC#58 (import with)](https://github.com/Whiley/RFCs/blob/master/text/0058-importwith.md).  This extended the syntax for `import` statements to support a `with` clause.  For example:
  ```whiley
  import std::vector with Vector
  ```

* [RFC#64 (for each)](https://github.com/Whiley/RFCs/blob/master/text/0063-foreach.md).  This finally added a syntax for `for`-loops to the language which had been sorely missing!  For example:
  ```whiley
  for i in 0..|arr|:
     m = m + arr[i]
  ```

* [RFC#66 (type inference)](https://github.com/Whiley/RFCs/blob/master/text/0066-type-inference.md).  This added support for _bidirectional type inference_ allowed type information to flow both up and down the AST for an expression.  Amongst other things, this allows us to omit type parameters in most cases.  For example:
  ```whiley
  // Create empty vector
  Vector<int> vec = Vector()
  // Add something to vector
  vec = push(vec,1)
  ```
  
   In the last line, without type inference, we'd have to give the type parameters explicitly (e.g. `push<int>(vec,1)`).  Type inference also helps implementing native types (e.g. a string literal assigned to a JavaScript string can be implemented directly as such).

In addition to the formal RFC submissions, there are also a bunch of [lightweight ideas being floated around](https://github.com/Whiley/RFCs/issues) as well which may eventually be written up into RFCs.

### QuickCheck

Whilst the static verifier that comes with Whiley has slowly improved over the years, it remains difficult to use on large projects.  A surprising success has been the [QuickCheck for Whiley](https://whileydave.com/publications/chin18_engr489/) originally developed by Janice Chin.  The essential idea behind this tool arose from the original [QuickCheck for Haskell](https://en.wikipedia.org/wiki/QuickCheck) tool developed by John Hughes and Koen Claessen.  Essentially, this tool tests Whiley programs by generating inputs automatically based on certain (configurable) criteria, such as only `-3..3` for integers, restricting arrays to max length `3`, etc.  For example, consider this (broken) function:

```whiley
function find(int[] items, int item) -> (int r)
// Must return value index in items array
ensures r >= 0 && r < |items|:
    for i in 0..|items|:
       if items[i] == item:
          return i  // match
    return -1       // no match
```

This function is broken because its postcondition doesn't permit negative values to be returned.  Running this through QuickCheck quickly generates the following:

```
./src/main.whiley:6: postcondition not satisfied
--> main::find([],0)
    return -1       // no match
    ^^^^^^^^^
Stack Trace:
--> main::find([],0)
```

The great thing about this is that it shows the exact input values which generated the error!  And, what's even better, is that we can run QuickCheck as part of our [CI pipeline](https://github.com/marketplace/actions/whiley-build-action) as well!

### Other

Whilst there are too many other things to mention, here are a few more highlights chosen at random:

* **(Pygments)** This is a widely-used syntax highlighting package which now [supports Whiley](https://pygments.org/languages/), meaning you can get syntax highlighting for Whiley whenever Pygments is used (such as with [Hugo](https://gohugo.io)).

* **(Teaching)** Whiley has been used for teaching here at Victoria University since 2014, and also at other instituations as well.  For example, at [Stellenbosch University](https://www.sun.ac.za/english/Lists/news/DispForm.aspx?ID=4970) under Prof. Bruce Watson and Macquarie University under Dr. Matt Roberts.  It was also used for a [summer school on Formal Methods](https://link.springer.com/book/10.1007/978-3-030-17601-3) in China.

* **(Experimental Platforms)**.  There have been a bunch of experimental projects trying to get Whiley working on different platforms, including [WebAssembley](http://localhost:1313/publications/kumar19_engr489), [an FPGA](http://localhost:1313/publications/ppp18_vmil), the [Crazyflie Quad Copter](http://localhost:1313/publications/stevens14_engr489), the [Ethereum Virtual Machine](http://localhost:1313/publications/kumar19_engr489) and [more](http://localhost:1313/publications/ruarus13_engr489)!  Whilst these have all been instructive in the design of Whiley, perhaps the most promising future target is WebAssembly.

* **(Functional Reactive Programming)**.  The [Web.wy](https://github.com/DavePearce/Web.wy) provides a general platform for React-style [Functional Reactive Programming](https://en.wikipedia.org/wiki/Functional_reactive_programming) (i.e. including a [virtual DOM](https://en.wikipedia.org/wiki/React_(web_framework)#Virtual_DOM)).  Whilst this remains at an early stage, you can find a simple example [here](https://github.com/DavePearce/WebCalc.wy).

