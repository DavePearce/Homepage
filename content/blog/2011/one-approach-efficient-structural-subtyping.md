---
date: 2011-01-14
title: "One Approach to Efficient Structural Subtyping"
draft: true
---

An interesting challenge with [structural subtyping](http://wikipedia.org/wiki/Structural_type_system) is that of efficient implementation.  In particular, without care, it may be impossible to determine a static offset for each field in a structure at runtime, meaning every field access will require a dictionary lookup.  In this post, I'll review the problem and  outline one alternative approach.
## The Problem
To properly understand the problem faced implementing a structural type system, we must consider things at a low level.  In languages like C or Java, it is possible to determine a static offset for all fields at runtime.  Consider this simple C example:

[c]
typedef struct { int day; int month; int year; } date;

int getYear(date *ptr) {
 return ptr->year
}
[/c]

The C compiler will automatically determine a fixed layout for the `struct` named `date` (which may or may not including padding).  For simplicity, let's assume there's no padding and an `int` corresponds to a [two's-complement](http://wikipedia.org/wiki/Two's_complement) 32 bit integer.  Then, the layout will look something like this:

[{{<img class="text-center" src="http://whiley.org/wp-content/uploads/2011/01/DataLayout.png">}}](http://whiley.org/wp-content/uploads/2011/01/DataLayout.png)
Here, the offsets are measured in bytes and, thus, we know that the `year` field starts at an offset of 8 bytes.  This means that, in function `getYear()` above, the compiler can implement the field access directly by loading the `int` stored in memory at an offset of 8 bytes from `ptr`. 

In the presence of structural subtyping, things are a little more complicated.  Consider a similar example in a psuedo-version of C which supports structural subtyping:

[c]
typedef struct { int day; int month; int year; } date;
typedef struct { int year; } hasYear;

int getYear(hasYear *ptr) {
 return ptr->year
}
[/c]

This is very similar to the original version, except that `getYear()` has been refined to accept a pointer to *any structure* which has a field `year`.  For example, it will accept a pointer to an instance of `date`, as well as a pointer to an instance of `hasYear`.

The problem is that the compiler is now unable to determine a static offset for the field `year` within function `getYear()`.  This is because field `year` is at offset 8 in a `date` instance, and at offset 0 in a `hasYear` instance --- and, indeed, it could be at any offset (depending on the available structural subtypes of `hasYear`).  Therefore, the compiler is forced to resort to an altnerative strategy, such as using a dictionary lookup (i.e. where the `struct` is actually implemented as a dictionary keyed on field name).
## One Solution
There are many different approaches to generating efficient code for languages with structural type systems, and I'm going to present just one here which I haven't seen considered before.  In fact, it's really rather simple --- we're going to distinguish between *exact* subtypes and *variable* subtypes (**note:** in type system terminology, this corresponds to the difference between [depth and width subtyping](http://en.wikipedia.org/wiki/Subtype_polymorphism#Record_types)).  An exact subtype must have exactly the same number of fields with the same names, whilst a variable subtype may have more fields than required.  And, we'll provide a distinct notation, `...`, to distinguish them.  For example:

[c]
typedef struct { int day; int month; int year; } date;
typedef struct { int year; } hasYearExact;
typedef struct { ...; int year; ... } hasYearVariable;

int getYear(hasYearExact *ptr) {
 return ptr->year
}

int getYear(hasYearVariable *ptr) {
 return ptr->year
}
[/c]

In this example, an instance of `date` is not a subtype of `hasYearExact` because it does not have exactly the same number of fields with the same names.  In contrast, an instance of `date` is a subtype of `hasYearVariable`, because this uses the `...` notation to indicate that subtypes are may have more fields.

*So, what's the advantage here?* Well, essentially, the point is that with exact types you can always determine a static offset of fields, whilst with variable types you cannot.  This means that, in cases where performance is important, you can elect to use an exact type instead of a variable type to ensure that field accesses compile down to direct memory accesses.  *This isn't rocket science, but it does give an interesting and alternative approach to the problem!*