---
date: 2008-11-01
kind: "honsreport"
tags: ["jkit"]
title: "Optimising Java Programs with Pure Functions"
authors: "Andrew Walbran"
thesis: "Final Year Project (COMP489)"
school: "Victoria University of Wellington"
preprint: "Walbran08_COMP489.pdf"
---

**Abstract.** Java programs often contain expressions involving method calls that are executed many times in a loop but will always return the same value, such as checking the length of a collection through which the loop is iterating. This can help to make the code readable, but makes it less efficient to execute than it could be. Current optimising compilers are generally unable to perform loop invariant code motion on such method calls and object field accesses due to a lack of knowledge of functional purity, aliasing and other information about the program structure. We propose that the problems of finding this information and of using it for optimisation be separated by the use of a number of annotations which we introduce. We describe under what conditions such optimisations can be made, and implement our scheme in JKit.





