---
date: 2010-06-26
title: "What is Extended Static Checking?"
draft: false
---

Extended Static Checking (ESC) is the main technique used in Whiley.  *But, what is it? *Since there isn't a huge amount of information available on the web, I thought some discussion about this would be useful.

The term "static" implies that ESC is applied at [Compile-Time](http://en.wikipedia.org/wiki/Compile_time) --- that is, when a program is constructed, rather than when it is run.  The term "extended" implies it catches more errors than the convential techniques used in languages like Java.  Extended Static Checking represents one point on a scale which ranges between making no effort to catch errors, up to performing (usually by hand) a detailed and rigorous proof that a program is correct.  Roughly speaking, this scale looks something like this:

{{<img class="text-center" width="400px" src="/images/2010/range.png">}}

Here, I consider Languages like C and C++ to have **no checking** as, whilst they do have types, they are not strongly enforced and can cause arbitrary crashing at runtime (e.g. the infamous [blue screen of death](http://en.wikipedia.org/wiki/Blue_Screen_of_Death), or the equally annoying [seg fault](http://en.wikipedia.org/wiki/Segmentation_fault)).  **Dynamic type checking** includes languages like [Python](http://en.wikipedia.org/wiki/Python_%28programming_language%29), [PHP](http://en.wikipedia.org/wiki/PHP), [JavaScript](http://en.wikipedia.org/wiki/JavaScript), [Ruby](http://www.ruby-lang.org/en/), [Lisp](http://en.wikipedia.org/wiki/Lisp_%28programming_language%29) and [SmallTalk](http://en.wikipedia.org/wiki/Smalltalk) (to name just a few).  Here, the type system is [strongly enforced](http://en.wikipedia.org/wiki/Strongly_typed_programming_language) at runtime to prevent arbitrary crashing, although type errors can still occur during execution.  **Extended dynamic checking** is less common and represents efforts to check more detailed constraints at runtime.  Examples of this include [Eiffel](http://en.wikipedia.org/wiki/Eiffel_%28programming_language%29), [JML RAC](http://cs.nju.edu.cn/boyland/ftjp/paper_18.pdf), [Contract4J](http://www.contract4j.org/contract4j) and [JContractor](http://jcontractor.sourceforge.net/index.html).

With dynamic checking, *errors are not spotted until they actually occur during a program's execution*.  Obviously, if this happens after the program has been released it is, at best, embarrassing and, in the worst-case, leads to catastrophic failure.  Proponents of dynamic checking argue that modern [software development processes](http://en.wikipedia.org/wiki/Software_development_process), with their strong emphasis on [software testing,](http://en.wikipedia.org/wiki/Software_testing) dramatically reduce the chances of this happening.  In many situations, the risk is out-weighed by the ease of development offered by dynamically checked languages.  However, in situations which are [saftey-critical](http://en.wikipedia.org/wiki/Life-critical_system) or where very large and complex systems are being developed, more rigorous techniques are necessary.

This is where **static checking** comes into play. The  advantage of static checking over dynamic or runtime checking is that it  happens before the program is ever run.  Thus, we have a guarantee that certain kinds of error *can never happen during a program's execution*.  Obviously, this is desirable, but it comes at a cost.  Programming in statically checked languages is often more involved and requires strict adherence to certain rules.  **Static type checking** covers the majority of statically checked programming languages, including [Java](http://en.wikipedia.org/wiki/Haskell_%28programming_language%29), [C#](http://en.wikipedia.org/wiki/C_Sharp_%28programming_language%29) and [Haskell](http://en.wikipedia.org/wiki/Haskell_%28programming_language%29).  In this case, each program variable is given a [fixed type](http://en.wikipedia.org/wiki/Type_system) (e.g. string, int, real, etc) and the language guarantees it only ever holds values of this type.  However, whilst this is certainly useful, it accounts for only a small fraction of the errors which can arise during a program's execution.

**Extended Static Checking** attempts to take this further by catching a much larger range of errors.  The aim is to be fully automatic --- meaning no human intervention is required.  Examples of errors which can be caught using this technique include [division-by-zero](http://en.wikipedia.org/wiki/Division_by_zero), [array out-of-bounds](http://en.wikipedia.org/wiki/Bounds_checking), [integer overflow](http://en.wikipedia.org/wiki/Integer_overflow) and [null dereferencing](http://en.wikipedia.org/wiki/Pointer_%28computing%29#Null_pointer).  The technique enforces detailed constraints on what values a program variable may hold.  For example, that an integer variable can only hold values between 1 and 7 (since it represents a day of the week).  Modern languages like Java cannot statically check constraints such as this and, hence, variables may hold incorrect values during execution.  The term "Extended Static Checking" is really a catch-all for a range of techniques developed and used in both academia and industry over the last 40 years, including [static analysis](http://en.wikipedia.org/wiki/Static_code_analysis), [abstract interpretation](http://en.wikipedia.org/wiki/Abstract_interpretation), [model checking](http://en.wikipedia.org/wiki/Model_checking), [SAT solving ](http://en.wikipedia.org/wiki/Satisfiability_Modulo_Theories)and [automated theorem proving](http://en.wikipedia.org/wiki/Automated_theorem_proving).  Languages which employ extended static checking are few and far between, and none has yet made it into the mainstream.  Examples include [Spec#](http://research.microsoft.com/en-us/projects/specSharp//), [JML](http://www.cs.ucf.edu/~leavens/JML/) (via [ESC/Java](http://secure.ucd.ie/products/opensource/ESCJava2/)), [Dafny](http://research.microsoft.com/en-us/projects/dafny/), [SPARKada](http://www.sparkada.com/) and, of course, [Whiley](http://whiley.org).

Going beyond extended static checking moves us into the area of [full formal verification.](http://en.wikipedia.org/wiki/Formal_verification) Here, pretty much anything goes in an effort to prove that a program is correct with respect to a specification or property of interest.  Proofs are often done by hand, although mechanical proof assistants, such as [HOL](http://en.wikipedia.org/wiki/HOL_theorem_prover), [Isabelle](http://en.wikipedia.org/wiki/Isabelle_(theorem_prover)) and [Coq](http://en.wikipedia.org/wiki/Coq), are increasingly used to help manage the complexity.  A [proof assistant](http://en.wikipedia.org/wiki/Interactive_theorem_proving) is a tool for developing proofs which can help automate much of the grunt-work involved, but which still relies on a human operator to make important decisions.  A good example of a recent full verification project is that of the Microsoft Hypervisor, where [VCC](http://citeseerx.ist.psu.edu/viewdoc/download?doi=10.1.1.150.2420&rep=rep1&type=pdf) was used in [proving 50,000 lines of C code were correct](http://www.microsoft.eu/Futures/Viewer/tabid/64/articleType/ArticleView/articleId/254/categoryId/16/Menu/1/Verifying-50000-lines-of-C-code.aspx).

Anyway, I think that's enough for now!
## Further Reading

   * **Extended Static Checking for Java**, Cormac Flanagan, K. Rustan M. Leino, Mark Lillibridge, Greg Nelson, James B. Saxe and Raymie Stata.  In *Proceedings of the Conference on Programming Language Design and Implementation (PLDI)*, 2002. [[PDF](http://citeseerx.ist.psu.edu/viewdoc/download?doi=10.1.1.113.9957&rep=rep1&type=pdf)]

   * **The  Spec# Programming System: An Overview**, Mike Barnett,  K. Rustan M. Leino  and Wolfram Schulte, In *Proceedings of the Conference on  Construction  and Analysis of Safe, Secure, and Interoperable Smart Devices*,  2005. [[PDF](http://citeseerx.ist.psu.edu/viewdoc/download?doi=10.1.1.70.1523&rep=rep1&type=pdf)]

   * **Calysto: Scalable and Precise Extended Static Checking**, Domagoj Babic and Alan J. Hu.  In *Proceedings of the International Conference on Software Engineering* (ICSE), 2008. [[PDF](http://citeseerx.ist.psu.edu/viewdoc/download?doi=10.1.1.118.5765&rep=rep1&type=pdfhttp://citeseerx.ist.psu.edu/viewdoc/download?doi=10.1.1.118.5765&rep=rep1&type=pdf)]

   * **Improving Computer Security using Extended Static Checking**, B. V. Chess.  In *Proceedings of IEEE Symposium on Security and Privacy*, 2002. [[PDF](http://citeseerx.ist.psu.edu/viewdoc/download?doi=10.1.1.15.2090&rep=rep1&type=pdf)]

   * **Improving the Quality of Web-based Enterprise 		 Applications with Extended Static Checking: A Case 		 Study**,  Frédéric Rioux and Patrice Chalin. *Electronic Notes in Theoretical Computer Science*, 157(2):119--132, 2006. [[PDF](http://dx.doi.org/10.1016/j.entcs.2005.12.050)]

   * **Faster and More Complete Extended Static Checking for the Java Modelling Language**, Perry R. James, Patrice Chalin.  In *Journal of Automated Reasoning*, 44(1-2):145--174, 2009 [[PDF](http://users.encs.concordia.ca/~chalin/papers/JamesChalin2009-10-JAR-EnhancedESC.pdf)]

   * **Extended Static Checking for Haskell**, Dana N. Xu.  In *Proceedings of the ACM workshop  on Haskell*, 2006. [[PDF](http://citeseerx.ist.psu.edu/viewdoc/download?doi=10.1.1.64.4156&rep=rep1&type=pdf)]

   * **Extended Static Checking: a Ten-Year Perspective**, K Rustan Leino.  *Informatics*, 2001. [[PDF](http://citeseerx.ist.psu.edu/viewdoc/download?doi=10.1.1.26.1150&rep=rep1&type=pdf)]

   * **An  extended Static Checker for Modula-3**, K. Rustan  M. Leino and Greg Nelson.  In *Proceedings  of the  Conference on Compiler Construction*,  1998. [[PDF](http://www.springerlink.com/index/w60172xq02212066.pdf)]

   * **Extended Static Checking**, David L. Detlefs, K. Rustan M. Leino, Greg Nelson, James B. Saxe.  Compaq SRC Research Report 159, 1998. [[PDF](ftp://gatekeeper.research.compaq.com/pub/DEC/SRC/research-reports/SRC-159.pdf)]


See also the talk on [Extended Static Checking by Greg Nelson](http://www.researchchannel.org/prog/displayevent.aspx?rID=2761) over on [ResearchChannel](http://www.researchchannel.org), and also the [discussion of Strong Typing](http://www.c2.com/cgi/wiki?StronglyTyped) over on C2.  The ESC/Java tool is also available for download from [here](http://secure.ucd.ie/products/opensource/ESCJava2/).