---
date: 2014-01-01
type: "thesis"
title: "Maintaining Private Views in Java"
authors: "Paran Haslett"
thesis: "MSc"
school: "Victoria University of Wellington"
preprint: "Haslett14_MSc.pdf"
---

**Abstract:** When developers collaborate on a project there are times when the code diverges. When this happens the code may need to be refactored to best suit the changes before they are applied. In these situations it would be valuable to have a private view. This view would be functionally equivalent to other views and be able to present the code in a different form. It enables a developer to refactor or change the code to their tastes, with minimal impact on other developers. Changes in the order of methods and the addition of comments currently impact other developers even if there is no change in how the code works. The Refactor Categories Tool has been written to detect where Java source code has been moved within a file or comments have been added, removed or edited. This indicates that it would be useful for version control systems to differentiate between changes to a program that also change the behaviour and those that do not.