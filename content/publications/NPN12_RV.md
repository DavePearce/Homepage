---
date: 2012-01-01
type: "conference"
title: "Profiling Field Initialization for Java"
authors: "Stephen F. Nelson, David J. Pearce and James Noble"
booktitle: "Conference on Runtime Verification (RV)"
pages: "292--307"
copyright: "Springer"
DOI: "10.1007/978-3-642-35632-2_28"
preprint: "NPN12_RV_preprint.pdf"
---

**Abstract:** Java encourages programmers to use constructor methods to initialise objects, supports final modifiers for documenting fields which are never modified and employs static checking to ensure such fields are only ever initialised inside constructors. Unkel and Lam observed that relatively few fields are actually declared final and showed using static analysis that many more fields have final behaviour, and even more fields are stationary (i.e. all writes occur before all reads). We present results from a runtime analysis of 14 real-world Java programs which not only replicates Unkel and Lam’s results, but suggests their analysis may have under-approximated the true figure. Our results indicate a remarkable 72-82% of fields are stationary, that final is poorly utilised by Java programmers, and that initialisation of immutable fields frequently occurs after constructor return. This suggests that the final modifier for fields does a poor job of supporting common programming practices.

