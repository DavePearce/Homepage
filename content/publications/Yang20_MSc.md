---
date: 2020-10-01
kind: "thesis"
title: "Reverse Engineering of an Obfuscated Binary"
authors: "Kaisuho Yang"
thesis: "MSc"
school: "Victoria University of Wellington"
preprint: "Yang20_MSc.pdf"
---

**Abstract:** Reverse engineering is an important process employed by
both attackers seeking to gain entry to a system as well as the
security engineers that protect it. While there are numerous tools
developed for this purpose, they often can be tedious to use and rely
on prior obtained domain knowledge. After examining a number of
contemporary tools, we design and implement a de-noising tool that
reduces the human effort needed to perform reverse engineering. The
tool takes snapshots of a target program’s memory as the user
consistently interacts with it. By comparing changes across multiple
sets of snapshots, consistent changes in memory that could be
attributed to the user action are identified. We go on to demonstrate
its use on three Windows applications: Minesweeper, Solitaire and
Notepad++. Through assistance from the de-noising tool, we were able
to discover information such as the location of mines and values of
cards in these two games before they are revealed, and the data
structure used for input to Notepad++.
