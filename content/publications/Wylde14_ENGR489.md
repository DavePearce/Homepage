---
date: 2014-01-01
kind: "honsreport"
tags: ["whiley"]
title: "Verifying Whiley Programs using an Off-the-Shelf SMT Solver"
authors: "Henry Wylde"
thesis: "Final Year Project (ENGR489)"
school: "Victoria University of Wellington"
preprint: "Wylde14_ENGR489.pdf"

---

**Abstract.** This project investigated the integration of external theorem proving tools with Whiley — specifically, Satisfiability Modulo Theories (SMT) solvers — to increase the number of verifiable Whiley programs. The current verifier, the Whiley Constraint Solver (WyCS), is limited and hence there is a difficulty in verifying Whiley programs. This project designed and implemented an extension that supported the use of arbitrary SMT solvers with the Whiley compiler. The evaluation of this extension used the Z3 SMT solver. The evaluation confirmed the value of using external SMT solvers with Whiley by emphasising the extension’s ability to verify simple Whiley programs. However additional research would be required for applying this solution to more complex programs. This project also conducted an experiment that analysed WyCS’s rewrite rules. This research may be used to educate WyCS’s rewrite rule selection criteria, improving its verification abilities.



