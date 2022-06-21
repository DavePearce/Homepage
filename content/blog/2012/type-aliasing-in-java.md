---
date: 2012-03-02
title: "Type Aliasing in Java?"
draft: false
---

A problem I often encounter in Java is that I want to say "these two things are the same", but Java won't let me. Suppose I want to maintain an `int[]` array which is always sorted in my program.  So, whenever I get one of these things, I can rely on it being sorted.  Here's what I want it to look like:

```java
// find lowest index of matching entry, or return -1 if no match
int findLowest(sorted arr, int value) {
   int index = Arrays.binarySearch(arr,value)
   if(index < 0) { return -1; } // no match
   // matched, so find lowest index
   index = index - 1;
   while(index >= 0 && arr[index] == value) {
       index = index - 1;
   }
   return index + 1;
}
```

I often find a method like this useful because, when there are multiple `int`s of the same value, `Arrays.binarySearch()` doesn't guarantee which is found.

Of course, the above isn't legal Java though!  That's because there is no way to alias a name (e.g. `sorted`) with a type (e.g. `int[]`).  Now hold on, you say: you can just use `int[]` above, instead of `sorted`, and it will work fine!  True.  But, I want to use `sorted` to better document my program.  In C, you could use a `typedef`for this, and I've often found that useful.

So, overall, I think allowing such "type aliases" in Java would help: firstly, by providing better documentation; secondly, by allowing them to be type checked.  Now, there are quite a few gritty details to work out, such as the actual syntax for declaring a type alias.  I'm not proposing a JSR or anything here, but how about this:

```java
alias sorted of int[]

// find lowest index of matching entry, or return -1 if no match
int findLowest(sorted arr, int value) {
   ...
}
```

Seems simple enough.  Now, if we want aliases to be type checked, then we need a way to turn a given type (e.g. `int[]`) into an alias (e.g. `sorted`).  Well, I suppose a plain-old cast could do the job:

```java
alias sorted of int[]

// find lowest index of matching entry, or return -1 if no match
int findLowest(sorted arr, int value) {
   ...
}

sorted createSorted(int[] unsorted) {
   Arrays.sort(unsorted);
   return (sorted) unsorted;
}
```

Those type theorists amongst you will immediately raise your arms: *what if we subsequently update unsorted and break the sorting guarantee?* Well, yup, that could happen.  And, without something like [uniqueness types](http://en.wikipedia.org/wiki/Uniqueness_type), there's not much we can do.  But, perhaps we can just live with it, given that we're already living with similar issues related to generics and erasure.

Anyhow, it's just a thought.  And, I'm obviously not the first person to some with this idea (see e.g. [this](http://stackoverflow.com/questions/683533/type-aliases-for-java-generics), [this](http://stackoverflow.com/questions/5604390/how-do-i-create-some-variable-type-alias-in-java) and [this](http://www.comp.lancs.ac.uk/~ss/java2c/diff/typedef)).
