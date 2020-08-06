---
date: 2019-01-01
kind: "conference"
title: "Dependency Versioning in the Wild"
authors: "Jens Dietrich, David J. Pearce, Jacob Stringer, Amjed Tahir and Kelly Blincoe"
booktitle: "Conference on Mining Software Repositories (MSR)"
pages: "349--359"
copyright: "IEEE"
DOI: "10.1109/MSR.2019.00061"
preprint: "DPSTB19_MSR_preprint.pdf"
website: "https://conf.researchr.org/home/msr-2019"
---

**Abstract:** Many modern software systems are built on top of existing packages (modules, components, libraries). The increasing number and complexity of dependencies has given rise to automated dependency management where package managers resolve symbolic dependencies against a central repository. When declaring dependencies, developers face various choices, such as whether or not to declare a fixed version or a range of versions. The former results in runtime behaviour that is easier to predict, whilst the latter enables flexibility in resolution that can, for example, prevent different versions of the same package being included and facilitates the automated deployment of bug fixes.
We study the choices developers make across 17 different package managers, investigating over 70 million dependencies. This is complemented by a survey of 170 developers. We find that many package managers support — and the respective community adapts — flexible versioning practices. This does not always work: developers struggle to find the sweet spot between the predictability of fixed version dependencies, and the agility of flexible ones, and depending on their experience, adjust practices. We see some uptake of semantic versioning in some package managers, supported by tools. However, there is no evidence that projects switch to semantic versioning on a large scale.
The results of this study can guide further research into better practices for automated dependency management, and aid the adaptation of semantic versioning.
