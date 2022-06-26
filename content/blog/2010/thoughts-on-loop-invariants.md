---
date: 2010-09-30
title: "Thoughts on Loop Invariants"
draft: true
---

With the recent addition of for and while loops to Whiley, I've been able to fiddle around with [loop invariants](http://wikipedia.org/wiki/loop_invariants) and already I noticed a few things.  Consider this little program:

```whiley
// The current parser state
define state as { string input, int pos } where pos >= 0 && pos <= |input|

state parseWhiteSpace(state st):
  while st.pos < |input| && st.input[st.pos] == ' ':
    st.pos = st.pos + 1
  return st
```

This is a cut-down version of the function for parsing white-space in my Calculator benchmark.  Basically, it just moves the current position to the next non-space character.  The function looks OK, right?  Well, it won't compile because the loop invariant is wrong.

Here's a fixed version:

```whiley
// The current parser state
define state as { string input, int pos } where pos >= 0 && pos <= |input|

state parseWhiteSpace(state st):
  while st.pos >= 0 && st.pos < |input| && st.input[st.pos] == ' ':
    st.pos = st.pos + 1
  return st
```

The key is that we also need to state the position does not become negative during the loop.  This is quite fustrating really.  Hopefully, it should be easy enough for the compiler to infer simple properties like this, by looking at the loop body...