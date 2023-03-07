---
date: 2023-06-03
kind: "conference"
title: "Formal and Executable Semantics of the Ethereum Virtual Machine in Dafny"
authors: "Franck Cassez, Joanne Fuller,  Milad K. Ghale, David J. Pearce and Horacio M. A. Quiles"
booktitle: "Proceedings of the Symposium on Formal Methods (FM)"
copyright: "Springer"
DOI: "10.1007/978-3-031-27481-7_32"
preprint: "CFGPQ23_FM_preprint.pdf"
website: "https://fm2023.isp.uni-luebeck.de/"
---

**Abstract:** 

The Ethereum protocol implements a replicated state machine.  The network participants keep track of the system state by: 1) agreeing on the sequence of transactions to be processed and 2) computing the state transitions that correspond to the sequence of transactions.  Ethereum transactions are programs, called _smart contracts_, and computing a state transition requires executing some code.  The Ethereum Virtual Machine (EVM) provides this capability and can execute programs written in EVM _bytecode_.  We present a formal and executable semantics of the EVM written in the verification-friendly language Dafny: it provides **(i)** a readable, formal and verified specification of the semantics of the EVM; **(ii)** a framework to formally reason about bytecode. 
