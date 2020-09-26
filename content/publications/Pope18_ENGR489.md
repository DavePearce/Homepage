---
date: 2018-01-01
kind: "honsreport"
tag: "whiley"
title: "Inferring invariants from postconditions in Whiley"
authors: "Simon Pope"
thesis: "Final Year Project (ENGR489)"
school: "Victoria University of Wellington"
preprint: "Pope18_ENGR489.pdf"

---

**Abstract.** For the formal verification of programs, loop invariants must be used to ensure the verifier understands the properties of a loop. These invariants are often trivial, and many are common between loops. It would be easier for programmers if these types of invariants did not have to be written into code, but were instead generated by the compiler. One way to do this is to infer the loop invariants from the postconditions of the containing function or method. This project looks at implementing this for the Whiley language. This is assisted by techniques of static analysis to better shape mutation.



