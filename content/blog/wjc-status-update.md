---
date: 2010-06-07
title: "Whiley Compiler Status Update"
draft: true
---

So, a little update regarding the status of the Whiley compiler.  I have spent a considerable amount of time redesigning the theorem prover over the last 8-12 weeks, and still haven't managed to put everything back together. The reason for this was simply that I reached a brick wall in terms of the original design, and there was a large number of oustanding bugs which couldn't easily be fixed.  Therefore, I am hoping that my new design will take me forward and really get the Whiley compiler singing.  The main new features I've been working on are:

   * **The theorem prover now generates counter-examples.** This is a critical improvement over the previous version, as it enables the three-valued logic I want in Whiley.  Namely, that for each check the system determines one of three things: there's definitely no problem; there definitely is a problem; or, don't know.  Only in the latter case will the compiler insert a runtime check.  The previous verison of the theorem prover could only produce two values and, hence, unless it proved the absence of an error we had to assume there was one.
	
   * **The theorem prover is now somewhat faster**.  It's difficult to gauge how much faster, of course.  But, the key difference is that everytime it performs a "split" --- i.e. the computation tree forks --- the theorem prover does significanly less work than before.

Version 0.2.5 (see downloads) remains the most stable and usable release; however, I am slowly ploughing my way through the large number of bug fixes necessary after completely redesigning the theorem prover.
