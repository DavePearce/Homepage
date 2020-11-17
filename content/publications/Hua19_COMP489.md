---
date: 2019-01-01
kind: "honsproject"
tags: ["whiley"]
title: "Compiling Whiley for WebAssembly"
authors: "Wei Hua"
thesis: "Final Year Project (COMP489)"
school: "Victoria University of Wellington"
preprint: "Hua19_COMP489.pdf"
---

**Abstract.** Whiley is a multi-paradigm programming language which supports Extended Static Checking through formal specification. At compile time, Whiley can identify common errors which are uncaught by a type checker, including division by zero, null reference and array out of bounds errors. WebAssembly is a portable binary code format supported by major browsers. WebAssembly is designed to enable high-performance applications on web pages. This project is about developing a Whiley Compiler back-end plugin which translates Whiley Intermediate Language into WebAssembly. This plugin can support almost all of Whiley syntax. It not only supports basic features like control flow and compound types, but also advanced features like recursive types, templates, lambda expressions. This document gives a general background of this project, the design of the solution, how we implement it. Finally, we compared performance between WebAssembly and JavaScript using WyBench benchmark suite on Node.js, where WebAssembly is 20%-30% faster than JavaScript.

