---
date: 2011-10-19
title: "What Kind of Revert are You?"
draft: false
---

Reverting is tough.  There's no doubt about it!  I don't mean tough as in *technically challenging* --- no, version control systems make this easy!  I mean tough as in *mentally challenging*.  You're faced with days or weeks of effort going down the drain, and you have to decide when to pull the plug.  Sure, it'll still be in your history somewhere.  But, you'll probably never get it out again.  And, if you do, it won't integrate with the latest version as this is constantly marching on.

At some point though, you know it's time.  You made a bad call, and you need to undo that and get back on track. It seems there are few different way to end up in this situation:

   * **The Obvious Disaster.** Perhaps this is the most common case.  You have an idea and start pushing it through.  But, after a while, it becomes obvious *it will never work*.  If you'd thought a bit harder up front, you'd probably have realised this.  Normally, not too much time is wasted.

   * **The Winding Road.** This one is ugly.  You've had an idea which, unbeknown to you, is fatally flawed.  The unfortunate thing is that the reason behind the flaw is very, very subtle.  It's only after a fairly significant amount of effort that you (finally) realise: *this approach can't work*.  This costs serious time, and is fustrating at best.  For me, I try not to lose to much sleep over these ones --- they come with the territory.

   * **The Good Idea.** This one is even uglier!  You've had an idea *which will actually work*.  The problem is, it's going to take a significant amount of effort to push through.  Furthermore, the gains from the improvement maybe somewhat marginal.  You're completely paralysed: undo the work so far and waste what is a good idea simply because it's going to take weeks or months to finish; OR, stubbornly keep going and push the damn thing through anyway.  I hate these ones, since I don't like knowly going backwards.  These ones I lose lots of sleep over.

   * **The Ping Pong**.   This is where you revert, and then realise actually maybe you should have stuck with it.  So you undo the revert!  Then, maybe, you realise why you reverted in the first place and revert again, and so on.  I think experience makes you aware of this cycle, and helps you avoid it by forcing you to think carefully before reverting.


So, the question right now as I stare at my code is: *what kind of revert are you?*