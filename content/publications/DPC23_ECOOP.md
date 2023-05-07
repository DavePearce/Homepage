---
date: 2023-07-17
kind: "conference"
title: "On Leveraging Tests to Infer Nullable Annotations"
authors: "Jens Dietrich, David J. Pearce and Mahin Chandramohan"
booktitle: "European Conference on Object Oriented Programming (ECOOP)"
copyright: "Schloss Dagstuhl--Leibniz-Zentrum fuer Informatik"
preprint: "DPC23_ECOOP_preprint.pdf"
website: "https://2023.ecoop.org/"
---

**Abstract:** 

Issues related to the dereferencing of null pointers are a pervasive
and widely studied problem, and numerous static analyses have been
proposed for this purpose. These are typically based on dataflow
analysis, and take advantage of annotations indicating whether a type
is nullable or not.  The presence of such annotations can
significantly improve the accuracy of null checkers.  However, most
code found in the wild is not annotated, and tools must fall back on
default assumptions, leading to both false positives and false
negatives.  Manually annotating code is a laborious task and requires
deep knowledge of how a program interacts with clients and components.

We propose to infer nullable annotations from an analysis of existing
test cases. For this purpose, we execute instrumented tests and
capture nullable API interactions. Those recorded interactions are
then refined (santitised and propagated) in order to improve their
precision and recall. We evaluate our approach on seven projects from
the spring ecosystems and two google projects which have been
extensively manually annotated with thousands of `@Nullable`
annotations.  We find that our approach has a high precision, and can
find around half of the existing `@Nullable` annotations.  This
suggests that the method proposed is useful to mechanise a significant
part of the very labour-intensive annotation task.
