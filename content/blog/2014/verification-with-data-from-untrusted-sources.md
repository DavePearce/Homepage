---
date: 2014-12-09
title: "Verification with Data from Untrusted Sources"
draft: false
---

Recently, I was listening to the latest edition of the [Illegal Argument podcast](http://illegalargument.com/episode-125), and it turns out they were discussing Whiley! (about 103:16 minutes in). The discussion was about how verification interacts with data from an *untrusted source* (e.g. from a database, network connection, etc). In particular, whether or not we can safely feed data directly from an untrusted source into a function which makes assumptions over that data (i.e. through preconditions or other invariants). The discussions missed a few things, which I thought would be good to clarify.

To start with, lets suppose something like this:

```whiley
type Transaction is {
  int id,
  int amount
} where amount >= 0

function balance(int id, [Transaction] records) => int:
    int b = 0
    for r in records:
        if r.id == id:
           b = b + r.amount
    // done
    return b
```

This function totals up the transaction amounts for a user over a given period of transactions. This is a contrived example, but illustrates the main point discussed in the podcast. Imagine feeding data directly from a database query into this function. *How can we be sure the necessary invariants are met?* (i.e. that transaction amounts aren't negative) Since we can't be sure the database enforces our invariants, this kind of information flow is unsafe.

The question now is: *does Whiley handle information flow from untrusted sources safely?* The answer, of course, is yes! That is, in Whiley, you cannot take untrusted data and feed it directly into a function which makes assumptions about it. Instead, Whiley forces you to first check whether the data meets your assumptions. In other words, Whiley requires you to validate untrusted data before moving it into the trusted zone.

At this point, the question now becomes: *if I have to validate all my data anyway, is Whiley actually doing anything useful?* Well, that depends on how long your data spends in the trusted zone. If it doesn't spend much time there, then there's probably not much benefit. In contrast, if your data spends lots of time in the trusted zone, then you can gain a lot from Whiley --- namely, that it guarantees at compile-time your invariants are always met.

Anyway, food for thought!
