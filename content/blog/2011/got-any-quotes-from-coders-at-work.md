---
date: 2011-04-25
title: "Got Any Quotes from Coders At Work?"
draft: false
---

Recently, I finished reading [Coders at Work](http://www.amazon.com/Coders-Work-Reflections-Craft-Programming/dp/1430219483) by Peter Seibel.  The book consists of a series of interviews with the following (impressive) list of programmers and computer scientists: [Jamie Zawinski](http://wikipedia.org/wiki/Jamie_Zawinski), [Brad Fitzpatrick](http://wikipedia.org/wiki/Brad_Fitzpatrick), [Douglas Crockford](http://wikipedia.org/wiki/Douglas_Crockford), [Brendan Eich](http://wikipedia.org/wiki/Brendan_Eich), [Josh Bloch](http://wikipedia.org/wiki/Josh_Bloch), [Joe Armstrong](http://wikipedia.org/wiki/Joes_Armstrong_(programming)), [Simon Peyton-Jones](http://wikipedia.org/wiki/Simon_Peyton-Jones), [Peter Norvig](http://wikipedia.org/wiki/Peter_Norvig), [Guy Steele](http://wikipedia.org/wiki/Guy_Steele), [Dan Ingalls](http://wikipedia.org/wiki/Dan_Ingalls), [Peter Deutsch](http://wikipedia.org/wiki/L_Peter_Deutsch), [Ken Thompson](http://wikipedia.org/wiki/Ken_Thompson), [Fran Allen](http://wikipedia.org/wiki/Fran_Allen), [Bernie Cosell](http://wikipedia.org/wiki/Bernie_Cosell) and [Don Knuth](http://wikipedia.org/wiki/Don_Knuth).

While I really enjoyed the book, I did find the rather ad-hoc nature of the interviews made it difficult to compare opinions on particular topics.  Nevertheless, Peter Seibel did have a script of sorts, and I wanted to try and extract some real insights from this "data".

So, in a somewhat unscientific fashion, I went through and marked key quotes on the topics I'm interested in (namely, *static typing* and *invariants*).  This is what I came out with.

## Static Typing

> **Brad Fitzpatrick:** "I also want it to blow up at compile time to tell me like "You're  doing something stupid."  Then sometimes I don't care and I want it to  coerce for me at runtime and do whatever."
> 
> **Doug Crockford:** "But in a classical system you can't do  that --- you're always working from the abstract back to the instance.   And then making hierarchy out of that is really difficult to get right.   So ultimately when you understand the problem better you have to go  back and refactor it.  But often that can have a huge impact on the  code, particularly if the code's gotten big since you figured it out"
> 
> **Doug Crockford:** "Refactoring in JavaScript, I find, is really easy.  Whereas refactoring a deep class hierarchy can be really, really painful."
> 
> **Brendan Eich:** "You see crazy, idiotic statements  about how  dynamic languages are going to totally unseat Java and static   languages, which is nonsense"
> 
> **Josh Bloch:** "OO is a funny thing.  It means two things.  It means modularity.  And modularity is great.  But I don't think OO people can claim the right to that.  ... And the other thing is inheritance and I consider inheritance a mixed blessing, as many people do now."
> 
> **Josh Bloch:** "This is an example of why you need safe languages.  This [stack overflow] is just not something that anyone should ever have to cope with".
> 
> **Josh Bloch:** "I still like generics.  Generics find bugs in my code for me.  Generics let me take things that used to be in comments and put them into code where the compiler can enforce them"
> 
> **Joe Armstrong:** "Then the static typing people say,  "Well, we  really rather like the  benefits of dynamic types when we're  marshaling  data structures".  We  can't send an arbitrary program down a  wire and  reconstruct it at the  other end because we need to know the  type"
> 
> **Joe Armstrong:** "We don't have ways of describing this protocol  between things: if I send you one of them and then you send me one of  these.  We have ways of describing packets and their types but we have  very restricted ways of describing protocols"
> 
> **Simon Peyton-Jones:** "But one that is often less rehearsed is maintenance.  When you have a blob of code that you wrote three years ago and you want to make a systemic change to it --- not just a little change to one procedure, but something that is going to have pervasive effects --- I find type systems are incredibly helpful"
> 
> **Simon Peyton-Jones:** "where static typing fits, do it every time because it has just fantastic maintenance benefits"
> 
> **Dan Ingalls:** "Types are essentially assertions about a program.  And I think it's valuable to have things be as absolutely simple as possible, including not even saying what the types are"
> 
> **Ken Thompson:** "Bugs are bugs.  You write code with bugs because you do. If it's a safe language in the sense of run-time safe, the operating system crashes instead of doing a buffer overflow in a way that's exploitable"

## Invariants and Assertions

> **Jamie Zawinski:** "Obviously putting in assert statements is always a good idea for debugging and like you said, for documentation purposes"
> 
> **Brad Fitzpatrick:** "I try to think mostly in terms of preconditions, and checking things in the constructor and the beginning of a function"
> 
> **Brendan Eich:** "In spite of bad assertions  that should've been warnings, we've had more good assertions over time  in Mozzilla.  From that we've had some illumination on what the  invariants are that you'd like to express in some dream type system."
> 
> **Doug Crockford:** "Basically, software is the specification for how the software is  supposed to work.  And anything less than the complete specification  doesn't really tell you anything about how it's ultimately going to  behave."
> 
> **Doug Crockford:** "I thought Eiffel was a much more interesting language and I  liked  the precondition/postcondition constract stuff that it did.  I  would  like to see that bulit into my language, whatever language I'm  using,  but that's another one of those ideas that hasn't really caught  on".
> 
> **Josh Bloch:** "You probably know that assertions were the first construct that I added to the Java Programming Language and I'm well aware that they never really became part of the culture".
> 
> **Josh Bloch:** "As I said, I use assertions to make sure that complicated invariants are maintained.  If invariants are corrupted, I want to know the instant it happens"
> 
> **Simon Peyton-Jones:** "Suppose you declare that your goal is for everything to have a machine-checked proof of correctness.  It's not even obvious what that would mean.  Mechanically proved against what?"
> 
> **Simon Peyton Jones:**"I think much more productive for real life is  to write down some  properties that you'd like the program to have.   You'd like to say,  "This valve should never be shut at the same time as  that valve.  This  tree should always be balanced.  This function  should always return a  result that's bigger than zero." These are all  little partial  specifications.  They're not complete specifictions.   They're just  things that you would like to be true"
> 
> **Peter Norvig:** "Like loop invariants: I've always thought that was more trouble than it was worth"
> 
> **Guy Steele:** "As I've progressed through my understanding of what ought to be recorded I think we want to say a lot more about data structures, we want to say a lot more about their invariants"
> 
> **Guy Steele:** "For example, here's an array and here's an integer and this integer ought to be a valid index into the array.  That's something you can't easily say in Java".
> 
> **Dan Ingalls:** "So if you are allowed to do all sorts of dangerous things in a program, then when you sit down to do the formal proof, it's very hard because at every step you've got to say, "Well, this could happen, that could happen, that could happen"
> 
> **Peter Deutsch:** "My belief is still, if you get the data structures and their invariangs right, most of the code will kind of write itself".
> 
> **Peter Deutsch:** "I now think that the path to software that's  more likely to do what we intend it to do lies not through assertions,  or inductive assertions, but lies through better, more powerful, deeper  declarative notations"
> 
> **Peter Deutsch:** "That's the reason  why theorem-proving technology basically has --- in my opinion ---  failed as a practical technology for improving software reliability.   It's just too damn hard to formalize the properties that you want to  establish".
> 
> **Don Knuth:** "Somebody will say they have a program that's verified and it's only verified because it met its specification according to some verifier.  But the verifier might have bugs in it.  The specifications might have bugs in them."
## Conclusion
It's all pretty fascinating stuff ... I guess the question is: *who do you believe?* There are a lot of other interesting topics covered in the book which could be mined as well.