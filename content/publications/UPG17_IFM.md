---
date: 2017-01-01
kind: "conference"
tag: "whiley"
title: "Making Whiley Boogie!"
authors: "Mark Utting, David J. Pearce and Lindsay Groves"
booktitle: "Conference on Integrated Formal Methods (IFM)"
pages: "69--84"
copyright: "Springer"
DOI: "10.1007/978-3-319-66845-1_5"
preprint: "UPG17_IFM_preprint.pdf"
website: "http://ifm2017.di.unito.it/"
---

**Abstract:** The quest to develop increasingly sophisticated verification systems continues unabated. Tools such as Dafny, Spec#, ESC/Java, SPARK Ada, and Whiley attempt to seamlessly integrate specification and verification into a programming language, in a similar way to type checking. A common integration approach is to generate verification conditions that are handed off to an automated theorem prover. This provides a nice separation of concerns, and allows different theorem provers to be used interchangeably. However, generating verification conditions is still a difficult undertaking and the use of more “high-level” intermediate verification languages has become common-place. In particular, Boogie provides a widely used and understood intermediate verification language. A common difficulty is the potential for an impedance mismatch between the source language and the intermediate verification language. In this paper, we explore the use of Boogie as an intermediate verification language for verifying programs in Whiley. This is noteworthy because the Whiley language has (amongst other things) a rich type system with considerable potential for an impedance mismatch. We report that, whilst a naive translation to Boogie is unsatisfactory, a more abstract encoding is surprisingly effective.