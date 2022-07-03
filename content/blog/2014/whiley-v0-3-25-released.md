---
date: 2014-06-09
title: "Whiley v0.3.25 Released!"
draft: true
---

Time for another release of the Whiley compiler. This is relatively low key, though includes the first steps towards an improved bytecode API (see <a href="https://github.com/Whiley/WhileyCompiler/issues/190">#190</a>). There are also a range of bug fixes for various minor issues:
<ul>
	<li>Refactoring <code>wyil.lang.Code</code> and <code>wyil.lang.CodeBlock</code>.  In order to support proper nested bytecode blocks, I am refactoring the WyIL bytecode API.  This will allow a <code>Block</code> to nested within another <code>Block</code>.  The main advantage of this is from a usability perspective, as it will eliminate the <code>LoopEnd</code> bytecode which has proven problematic.</li>
	<li>Fixed bug <a href="https://github.com/Whiley/WhileyCompiler/issues/190">#352</a> which was a problem with the verifier.</li>
</ul>
Recently, I have been making relatively slow progress on the compiler.  This is because I have been distracted writing grant applications and teaching; I've also been working on the <a href="https://github.com/Whiley/WhileyDocs/tree/master/WhileyLanguageSpecification">Whiley Language Specification</a> as well.  Whilst I still have a few more administrative things to sort out, I'm hoping to put more time into the compiler in the near future.  My current goal is to complete a full pass through the <code>wyil</code> module, improving the overall design, refactoring code and adding more documentation.  Following this, the next goal is to complete the <a href="https://github.com/Whiley/WhileyCompiler/issues?direction=desc&milestone=22">plugin framework</a> (which I have also been working on already).

[wpfilebase tag=file id=82 tpl=custom]

[wpfilebase tag=file id=81 tpl=custom]