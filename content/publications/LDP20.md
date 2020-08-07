---
date: 2020-08-04
kind: "conference"
title: "Putting the Semantics into Semantic Versioning"
authors: "Patrick Lam, Jens Dietrich and David J. Pearce"
pages: "(to appear)"
booktitle: "Onward! Symposium on New Ideas, New Paradigms, and Reflections on Programming and Software"
preprint: "LDP20_ONWARD_preprint.pdf"
---

**Abstract:** The long-standing aspiration for software reuse has made astonishing strides in the past few years. Many modern software development ecosystems now come with rich sets of publicly-available components contributed by the community. Downstream developers can leverage these upstream
components, boosting their productivity.  However, components evolve at their own pace. This imposes obligations on and yields benefits for downstream developers, especially since changes can be breaking, requiring additional downstream work to adapt to. Upgrading too late leaves downstream vulnerable to security issues and missing out on useful improvements; upgrading too early results in excess work. Semantic versioning has been proposed as an elegant mechanism to communicate levels of compatibility, enabling downstream developers to automate dependency upgrades.

While it is questionable whether a version number can adequately characterize version compatibility in general, we argue that developers would greatly benefit from tools such as semantic version calculators to help them upgrade safely.  The time is now for the research community to develop such tools: large component ecosystems exist and are accessible, component interactions have become observable through automated builds, and recent advances in program analysis make the development of relevant tools feasible.  In particular, contracts (both traditional and lightweight) are a promising input to semantic versioning calculators, which can suggest whether an upgrade is likely to be safe.
