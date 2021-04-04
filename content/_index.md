---
draft: false
banner: "images/cover7.jpg"
banner_alt: "Image of David J. Pearce"
banner_ratio: "2048_850"
---

## Bio

I'm an Associate Professor in the [School of Engineering and Computer Science](https://www.wgtn.ac.nz/ecs) at [Victoria University of Wellington](https://www.wgtn.ac.nz).  I graduated from the [Department of Computing](https://www.imperial.ac.uk/computing) at Imperial College London, and moved to New Zealand in 2004.  My research interests are in *programming languages*, *compilers*, *static analysis tools* and *software verification*.  I currently serve as an editor for the [Science of Computer Programmning](https://www.journals.elsevier.com/science-of-computer-programming).  During my time as a PhD student I was an intern at Bell Labs, New Jersey, working on compilers for FPGAs and also at IBM Hursely, UK, working with the AspectJ development team on profiling systems. 

## Research

During my [PhD](publications/Pearce05_PhD.pdf) I developed several new algorithms for [static pointer analysis](https://en.wikipedia.org/wiki/Pointer_analysis).  Since then I have continued working on techniques for static analysis and verification tools, and several of my algorithms have since found widespread use.  For example, my algorithm for *field-sensitive pointer analysis* is used in [GCC](https://github.com/gcc-mirror/gcc/blob/master/gcc/tree-ssa-structalias.c) and in the [godoc](https://github.com/golang/tools/blob/master/go/pointer/doc.go) tool for [Go](https://golang.org); my algorithm for *dynamic topological sort* is used in [TensorFlow](https://github.com/tensorflow/tensorflow/blob/master/tensorflow/compiler/jit/graphcycles/graphcycles.cc); another for finding strongly connected components is used in [SciPy](https://docs.scipy.org/doc/scipy/reference/generated/scipy.sparse.csgraph.connected_components.html); finally, my algorithm for computing Tutte Polynomials features in both [Mathematica](https://mathworld.wolfram.com/TuttePolynomial.html) and [Sage](https://doc.sagemath.org/html/en/reference/graphs/sage/graphs/tutte_polynomial.html).

My **current research** is focused around the [Whiley programming language](http://whiley.org) and [software verification](https://en.wikipedia.org/wiki/Formal_verification).  Whiley provides support for providing functional specifications in the form of [preconditions](https://en.wikipedia.org/wiki/Precondition) and [postconditions](https://en.wikipedia.org/wiki/Postcondition), and is designed to simplify verifying software.  You can find out more about my work on Whiley [here](projects/whiley).