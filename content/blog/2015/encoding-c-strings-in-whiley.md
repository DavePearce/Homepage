---
date: 2015-11-12
title: "Encoding C Strings in Whiley"
draft: false
---

In this post, we're going to consider representing the classic [C string](http://en.wikipedia.org/wiki/C_string_handling) in Whiley. This turns out to be useful as we can then try to verify properties about functions which operate on C strings (e.g. `strlen()`, `strcpy()`, etc). If you're not familiar with C strings, then the main points are:
   * Roughly speaking, C Strings are arrays of 8bit [ASCII](http://en.wikipedia.org/wiki/ASCII) characters.

   * C Strings are terminated by the special character `'\0'` (also called [null terminated strings](http://en.wikipedia.org/wiki/Null-terminated_string)).

   * C Strings do not carry any other length information (e.g. for the size of the containing memory chunk).


The interesting thing about Whiley is that we can encode these constraints within the language itself using only the built-in list and integer types. Specifically, we're going to encode a C string as a constrained list of constrained integers. The list will be constrained to ensure it is `null` terminated, whilst the contained integers will be constrained to ensure they are between `0` and `255`.

Before giving our definition of a C string in Whiley, we need to first define the notion of an 8bit ASCII character. This is done as follows:

```whiley
// Define the ASCII 8bit character
type ASCII_char is (int n) where 0 <= n && n <= 255
```

Here, we have defined an 8bit ASCII character to be an integer which is constrained between `0` and `255` (i.e. to ensure it fits in 8 bits). Using this, we can now define our notion of a `C` string as follows.

```whiley
// Define the null terminator
constant NULL is 0

type C_string is (ASCII_char[] str)
// Must have at least one character (i.e. null terminator)
where |str| > 0 && some { i in 0 .. |str| | str[i] == NULL }
```

Here, we have then defined a `C_String` to be a list of integers which is constrained to ensure it is always null terminated.  That is, there is always at least one NULL terminator somewhere in the array (though there may be several).

As an example to illustrate the use of our `C_string`, we provide an implementation of the well-known `strlen()` function below:

```whiley
// Determine the length of a C string.
function strlen(C_string str) -> (int r)
ensures str[r] == NULL
ensures all { k in 0 .. r | str[k] != NULL }:
  //
  int i = 0
  //
  while str[i] != NULL
  where i >= 0 && i < |str|
  where all { k in 0 .. i | str[k] != NULL}:
    i = i + 1
  //
  return i
```

The Whiley compiler statically verifies this function does not overrun the string bounds. The loop invariant given by the `where` clause is needed as a hint to the verifier, but does not affect the function's execution in any way.
