---
date: 2018-04-23
title: "Verifying leftPad() in Whiley"
draft: true
---

The `leftPad(string,int)` function simply pads a string up to a given size by inserted spaces at the beginning. For example, `leftPad("hello",8)` produces `"   hello"`. This little function [shot to fame in 2016](https://www.theregister.co.uk/2016/03/23/npm_left_pad_chaos/) when a developer pulled all his modules from NPM, of which one provided the `leftPad()` functionality. There were two basic issues causing surprise here: firstly, a developer can suddenly and without warning pull all his libraries (including old versions), thereby breaking anything depending on them (which, in this case, was a lot). secondly, that someone was providing a module to offer this (pretty basic) functionality.

Anyhow, that is all ancient history. A bunch of people have been writing verified implementations of `leftPad()` in various languages. And, `@Hillelolgram` asked if I would write one in Whiley:


[{{<img class="text-center" src="http://whiley.org/wp-content/uploads/2018/04/hillelogram.png">}}](https://twitter.com/Hillelogram/status/988155666257862658)

So, having little spare time, I thought I'd give it a crack.  Here's my first version:

```whiley
import string from std::ascii
import char from std::ascii
import std::array

function leftPad(string str, int n) -> (string result)
// Required padding cannot be negative
requires n >= |str|
// Returned array increased to size n
ensures |result| == n
// First elements are padding
ensures all { i in 0 .. (n-|str|) | result[i] == ' '}
// Everything else copied from original
ensures all { i in 0 .. |str| | result[i+(n-|str|)] == str[i] }:
    // Calculate how much padding required
    int padding = n - |str|
    // Create new string of final length
    string nstr = [' '; n]
    // Copy over everthing from old string
    return array::copy(str,0,nstr,padding,|str|)
```

This is nice and short and makes use of the standard library.  But, sadly, for reasons unknown I could not get this to pass verification.  Since I already had a `resize()` method in the standard library and I knew this verified, I figured I could work from that instead.  Here's version two (after some faffing around):
```whiley
function leftPad(string str, int size) -> (string result)
// Required padding cannot be negative
requires size >= |str|
// Returned array increased to size
ensures |result| == size
// First n elements are padding
ensures all { i in 0 .. (size-|str|) | result[i] == ' '}
// Everything else copied from original
ensures all { i in 0 .. |str| | result[i+(size-|str|)] == str[i] }:
    //
    int padding = size - |str|
    string nstr = [' '; size]
    int i = 0
    //
    while i < |str|
    where i >= 0 && |nstr| == size
    // All elements up to i copied over
    where all { j in 0..i | nstr[j+padding] == str[j] }
    // Untouched str are still padding
    where all { j in 0..padding | nstr[j] == ' ' }:
        //
        nstr[i+padding] = str[i]
        i = i + 1
    //
    return nstr
```

Essentially, I've inlined the call to `array::copy()`.  And, yes, this verifies!!  Unfortunately, you can't verify it on [whileylabs.com](http://whileylabs.com) at the moment because it times out.  But, you can do it using [WhileyWeb](https://github.com/Whiley/WhileyWeb) or the command-line compiler. 

Whilst it's great that it does indeed verify, what's less great is the faffing around I needed to get it through...