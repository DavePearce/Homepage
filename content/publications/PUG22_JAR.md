---
date: 2022-01-15
kind: "journal"
tags: ["whiley"]
title: "Verifying Whiley Programs with Boogie"
authors: "David J. Pearce, Mark Utting and Lindsay Groves"
booktitle: "Journal of Automated Reasoning (JAR)"
pages: "(to appear)"
copyright: "Springer"
DOI: "10.1007/s10817-022-09619-1"
preprint: "PUG22_JAR_preprint.pdf"

---

**Abstract:**   The quest to develop increasingly sophisticated verification systems continues unabated.  Tools such as Dafny, Spec#, ESC/Java, SPARK Ada, and Whiley attempt to seamlessly integrate specification and verification into a programming language, in a similar way to type checking.  A common integration approach is to generate verification conditions that are handed off to an automated theorem prover.  This provides a nice separation of concerns, and allows different theorem provers to be used interchangeably.  However, generating verification conditions is still a difficult undertaking and the use of more ``high-level'' intermediate verification languages has become common-place.  In particular, Boogie provides a widely used and understood intermediate verification language.  A common difficulty is the potential for an impedance mismatch between the source language and the intermediate verification language.  In this paper, we explore the use of Boogie as an intermediate verification language for verifying programs in Whiley.  This is noteworthy because the Whiley language has (amongst other things) a rich type system with considerable potential for an impedance mismatch.  We provide a comprehensive account of translating Whiley to Boogie which demonstrates that it is possible to model most aspects of the Whiley language.  Key challenges posed by the Whiley language included: the encoding of Whiley's expressive type system and support for flow typing and generics; the implicit assumption that expressions in specifications are well-defined; the ability to invoke methods from within expressions; the ability to return multiple values from a function or method; the presence of unrestricted lambda functions; and the limited syntax for framing.  We demonstrate that the resulting verification tool can verify significantly more programs than the native Whiley verifier which was custom-built for Whiley verification.  Furthermore, our work provides evidence that Boogie is (for the most part) sufficiently general to act as an intermediate language for a wide range of source languages.
