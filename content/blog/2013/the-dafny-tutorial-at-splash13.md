---
date: 2013-11-01
title: "The Dafny Tutorial at SPLASH'13"
draft: false
---

{{<img class="text-center" width="50%" src="/images/2013/Dafny.JPG">}}
Today I was attending the Dafny tutorial given by [Rustan Leino](http://research.microsoft.com/en-us/um/people/leino/) at [SPLASH'13](http://splashcon.org/2013/). I have to say that this was the highlight of the conference for me. In case you haven't come across it before, [Dafny](http://research.microsoft.com/en-us/projects/dafny) is a programming language designed for software verification. It has a lot in common with Whiley, although Dafny is certainly more mature. You can try it out in your web-browser on the [rise4fun](http://rise4fun.com/Dafny) site.

{{<img class="text-center" width="50%" src="/images/2013/Rustan.JPG">}}

During the tutorial, we went through a number of examples, including several focusing on [loop invariants](http://en.wikipedia.org/wiki/Loop_invariant).  For example, here's the Dafny code for the absolute function:

```dafny
method abs(x:int, y:int) returns (z:int)
ensures x == z || y == z;
ensures x <= z && y <= z; {      if x > y {
    return x;
  } else {
    return y;
  }
}
```

You can find the code for this [here](http://rise4fun.com/Dafny/fHWhB), where you can run it directly in your browser and play around with it. In fact, the browser version is surprisingly good, and seems to have improved a lot lately. Dafny statically verifies (automatically) that this method does indeed meet its specification.

Now, here's one for you to try:

```dafny
method To100by2s()
{
  var n := 0;
  while n < 100
  invariant ???;
  {
    n := n + 2;
  }
  assert n == 100;
}
```

The question is: *what loop invariant can replace `???` to prove the assertion holds?* Try to solve it [here](http://rise4fun.com/dafny/iW5I)! And, there are at least two different ways to do it ...

Finally, here's a way more complicated example which illustrates binary search:

```Dafny
method BinarySearch(arr: array, key: int) returns (r: int)
  requires arr != null;
  requires forall i,j :: 0 <= i < j < arr.Length ==> arr[i] <= arr[j];
  ensures 0 <= r ==> r < arr.Length && arr[r] == key;
  ensures r < 0 ==> forall i :: 0 <= i < arr.Length ==> arr[i] != key;
{
  var lo, hi := 0, arr.Length;
  while lo < hi
    invariant 0 <= lo <= hi <= arr.Length;
    invariant forall i :: 0 <= i < lo ==> arr[i] != key;
    invariant forall i :: hi <= i < arr.Length ==> arr[i] != key;
    {
      var mid := (lo+hi) / 2;
      if arr[mid] < key {
        lo := mid + 1;
      } else if arr[mid] > key {
        hi := mid;
      } else if arr[mid] == key {
        return mid;
      }
    }
    return -1;
}
```

Again, you can run the code directly from [here](http://rise4fun.com/Dafny/itla). This example is pretty neat! Although, it's worth mentioning that it doesn't suffer from [the hidden overflow bug which plagued Java](http://googleresearch.blogspot.com/2006/06/extra-extra-read-all-about-it-nearly.html) because Dafny, like Whiley, uses unbound arithmetic (i.e. it never overflows).
