---
date: 2021-01-01
kind: "journal"
tags: ["whiley"]
title: "Finding Bugs with Specification-Based Testing is Easy!"
authors: "Janice Chin and David J. Pearce"
booktitle: "The Art, Science, and Engineering of Programming"
preprint: "CP21_PROGRAMMING_preprint.pdf"
DOI: "10.22152/programming-journal.org/2021/5/13"
---

**Abstract:**   Automated specification-based testing has a long history with several notable tools having emerged.  For example, QuickCheck for Haskell focuses on testing against user-provided properties. Others, such as JMLUnit, use specifications in the form of pre- and post-conditions to drive testing.  An interesting (and under-explored) question is how *effective* this approach is at finding bugs in practice.  In general, one would assume automated testing is less effective at bug finding than static verification. *But, how much less effective?*  To shed light on this question, we consider automated testing of programs written in Whiley --- a language with first-class support for specifications.  Whilst originally designed with static verification in mind, we have anecdotally found automated testing for Whiley surprisingly useful and cost-effective.  For example, when an error is detected with automated testing, a counterexample is always provided.  This has motivated the more rigorous empirical examination presented in this paper.  To that end, we provide a technical discussion of the implementation behind an automated testing tool for Whiley.  Here, a key usability concern is the ability to parameterise the input space, and we present novel approaches for references and lambdas. We then report on several large experiments investigating the tool's effectiveness at bug finding using a range of benchmarks, including a suite of 1800+ mutants.  The results indicate the automated testing is effective in many cases, and that sampling offers useful performance benefits with only modest reductions in bug-finding capability.  Finally, we report on some real-world uses of the tool where it has proved effective at finding bugs (such as in the standard library).

