---
date: 2012-11-28
title: "Comparing I/O in C with Java"
draft: false
---

Recently, I was having a somewhat heated discussion with a friend about the Java I/O library (specificially `java.io.*`).  His position was that the library is unnecessarily cluttered and verbose, and that  I/O in C is much simpler and more productive.  Whilst I agreed with some of that, I also argued that the Java I/O library is powerful and more flexible than C.  Here are some of the main points we covered:
   * **Abstracting stream encodings away from stream processing**.  The ability for objects in Java to delegate to an `InputStream` or `OutputStream` provides a very nice way to decouple the encoding of information from the processing of it.  An interesting example in the Whiley compiler is that of *name mangling*.  That is, encoding the Whiley type of a function into its name.  To generate the mangling, I have an `OutputStream` implementation which takes a Whiley type and serialises it into binary data.  Then, a second implementation of `OutputStream` takes an arbitrary stream of binary data and encodes it into (roughly speaking) 7-bit ASCII --- in other words, it turns the serialised data into a form that can safely be used as part of a method name according to the [JVM Spec](http://docs.oracle.com/javase/specs/jvms/se7/html/index.html).  And, of course, I have the mirror of this for reading the type out of a mangling.

   * **Stateful encoders/decoders are important**.  One idea we discussed was that encoding could be handled if: (1) Java had support for lambdas; and (2) classes like E.g. `FileReader` accepted a decoding (lambda) function.  This would cover a large number of use cases, and conceptually simplify the I/O framework.  However, we would need our decoding functions to have state to be useful (and I don't believe that [JSR 335 supports this](http://openjdk.java.net/projects/lambda/)).  State is necessary typically in situations where we can --- *or want* --- to read chunks larger than necessary.  For example, when performing some kind of buffering we read a large chunk and cache it for later.  Other examples of stateful stream components include unusual things, such as providing the ability to insert logging into the pipeline.


At this point, things were all making sense to me.  Having worked with C for many years (albeit some time ago now), I'm fairly familiar with how I/O is handled in C.  In particular, I/O mostly goes through the `FILE*` structure (although you can read/write to file descriptors directly if you like).  You have to rely on functions like `fopen()` and `popen()` to create `FILE*` instances because we don't know the internal layout of `FILE*` and, hence, cannot construct our own instances.  So, my first attack on this structure was to say something like:
> *"How do you create a FILE* instance from a memory buffer?*"

In Java, this is relatively easy since we can create e.g. a `ByteArrayInputStream` and pass that to anything accepting an `InputStream`.  *Well, it turns out you can do this in C!* I had never heard of it before, but there is a `fmemopen()` function for exactly this use case.

Undeterred, I countered with:
> "How do you create a FILE* instance which automagically encodes/decodes into a user-defined format (e.g. as per my name mangling example above)?"

Again, this is relatively easy to do in Java by providing your own implementation of e.g. `InputStream
`.  At this point, there was a long pause (in fact, overnight) in the discussion.  The next day, my friend comes back and says: *"ah, you obviously haven't heard of the `fopencookie()` function then!"*.  Nope, I hadn't.

This is an excerpt from the manpage on `fopencookie()`:
> The  fopencookie()  function allows the programmer to 
> create a custom implementation for a standard I/O stream.  
> This implementation can store the stream's data at a 
> location of its own choosing; for example, fopencookie() is 
> used to implement fmemopen(3), which provides a stream 
> interface to data that is stored in a buffer in memory.
> 
> In order to create a custom stream the programmer must:
> 
> * Implement four "hook" functions that are used internally
>    by the standard I/O library when performing I/O on the 
>    stream.
> 
> * Define a "cookie" data type, a structure that provides
>    bookkeeping information (e.g., where to store data) 
>    used by the aforementioned hook functions.  The 
>    standard I/O package knows nothing about  the 
>    contents of this cookie (thus it is typed as void * when 
>    passed to fopencookie()), but automatically supplies 
>    the cookie as the first argument when calling the hook 
>    functions.
> 
> * Call fopencookie() to open a new stream and associate 
>    the cookie and hook functions with that stream.
> 
> ...

Well, I guess you learn a new thing every day ...
