---
date: 2010-06-23
title: "Rich Hickey on Clojure (SE Radio)"
draft: true
---

I've just been listening to an interesting [interview with Rich Hickery on Clojure over at SE Radio](http://www.se-radio.net/2010/03/episode-158-rich-hickey-on-clojure/).  I'm a big fan of Clojure, since it shares a lot of similar ideas with Whiley (really, it does ... trust me :).  Anyway, Clojure provides the [notion of pure functions and values](http://clojure.org/functional_programming), as Whiley does.  The overriding principle is quite similar as well:
> The philosophy behind Clojure is that most parts of most programs should  be functional, and that programs that are more functional are more  robust.
> 
> *-- *clojure.org

Probably the main difference is that Whiley is about correctness, whilst Clojure is about concurrency.  Anyway, the part that really got my attention was the discussion of  values versus objects:
> "Even if you don't have concurrency, I think that large objected-oriented programs struggle with increasing complexity as you build this large object graph of mutable objects.  You know, trying to understand and keep in your mind what will happen when you call a method and what will the side-effects be ..."
> 
> -- Rich Hickey

This is really spot on, as far as i'm concerned.  In my experience, for anything vaguely complex, you end up with most of your objects having to be immutable.  Otherwise, it just gets too hard to figure out what is going on.  I'm always surprised when people defend this situation, saying that it allows programmers choice, and we don't want to force any particular style, etc.  The bottom line is that it's a complete death-trap for the novice, and even experienced programmers get caught out as they try various tricks to get performance (which usually come back to bite them).  The other often-used argument for having fully mutable objects is performance, particularly in respect with collections. This point is also discussed by Rich:
> "How do we program with Values?  How do you represent things which are bigger than numbers and strings as values? You need efficient data structures for representing collections as values."

Then, he goes on to talk about how it's done in Clojure.  The jist of it being that collections in Clojure are mostly implemented as linked-lists and/or trees.  The implementation of these allows sharing of substructure.  For example, two lists sharing the end portion of their sequences.  Of course, from the programmers point of view this is completely hidden --- they behave exactly as you would expect list values should:
> "... it feels semantically like a copy, but it's not a brute-force duplication of the data ... there is some more clever stuff going on" (host)
> 
> "Exactly, that's the real key.  And it needs to be said because people's first intuition is that it will require copying, and that can't be made efficient, but that's not what's happening." (Rich)

This kind of thing I've seen already when talking to people about Whiley.  It goes something like this: *"Oh, you have first-class lists ... you mean, you COPY the entire thing every time?"* Look, just because we have value semantics doesn't mean we have to copy everything!

Anyway, there's some video of [Rich talking about Clojure](http://blip.tv/file/1313398) which is rather good, although I have to confess I haven't made it all the way through yet!