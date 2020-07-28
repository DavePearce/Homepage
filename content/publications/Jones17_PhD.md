---
date: 2017-01-01
type: "thesis"
title: "Classless Object Semantics"
authors: "Timothy Jones"
thesis: "PhD"
school: "Victoria University of Wellington"
preprint: "Jones17_PhD.pdf"
---

**Abstract:** Objects have been categorised into classes that declare and implement their behaviour ever since the paradigm of object-orientation in programming languages was first conceived. Classes have an integral role in the design and theory of object-oriented languages, and often appear alongside objects as a foundational concept of the paradigm in many theoretical models.
A number of object-oriented languages have attempted to remove classes as a core component of the language design and rebuild their functionality purely in terms of objects, to varying success. Much of the formal theory of objects that eschews classes as a fundamental construct has difficulty encoding the variety of behaviours possible in programs from class-based languages.
This dissertation investigates the foundational nature of the class in the object-oriented paradigm from the perspective of an ‘objects-first’, classless language. Using the design of theoretical models and practical implementations of these designs as extensions of the Grace programming language, we demonstrate how objects can be used to emulate the functionality of classes, and the necessary trade-offs of this approach.
We present Graceless, our theory of objects without classes, and use this language to explore what class functionality is difficult to encode using only objects. We consider the role of classes in the types and static analysis of object-oriented languages, and present both a practical design of brand objects and a corresponding extension of our theory that simulates the discipline of nominal typing. We also modify our theory to investigate the semantics of many different kinds of implementation reuse in the form of inheritance between both objects and classes, and compare the consequences of these different approaches.