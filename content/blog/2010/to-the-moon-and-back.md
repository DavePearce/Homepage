---
date: 2010-06-10
title: "Formal Methods: To the Moon and Back!"
draft: false
---

I recently came across a rather interesting presentation by [Gerard Holzmann](http://en.wikipedia.org/wiki/Gerard_J._Holzmann) (author of the [SPIN](http://spinroot.com/spin/whatispin.html) model checker) who's currently working for [NASA's Jet Propulsion Lab](http://www.jpl.nasa.gov/), and previously worked at [Bell Labs](http://www.alcatel-lucent.com/wps/portal/BellLabs):

[http://www.infoq.com/presentations/Scrub-Spin](http://www.infoq.com/presentations/Scrub-Spin)

One of the things he talks about his is a possible moon-landing in 2019, and the software required compared with that of the original landings in 1969.  Back then, they ran around 10K of code on a machine with 36K ROM, 2K RAM with a 43KHz CPU.  In contrast, they're speculating that the 2019 mission will need 10M lines of code, running on a machine with 1GB RAM and a 1GHz clock cycle.  The difference between them seems somewhat expected, I suppose.  But, I rather like the point he makes about it: that the moon hasn't changed mass, or orbit, etc; that the physics required to land on the moon is the same; so, why do we need so much more?

Speaking of the moon landings, you can now get audio and video for them from the NASA site [here](http://history.nasa.gov/40thann/videos.htm), and a transcript of the first landing [here](http://history.nasa.gov/alsj/a11/a11.landing.html).  The bit I really like from the transcript is:
> 102:38:21 Armstrong: Sure do. Houston, you're looking at our Delta-H.
> 
> 102:38:25 Duke: That's affirmative.
> 
> [They have switched to a data readout which shows Houston the difference between their altitude as determined by the radar and the inertial estimate provided by the PGNS.]
> 
> 102:38:26 Armstrong: (With the slightest touch of urgency) Program Alarm.
> 
> 102:38:28 Duke: It's looking good to us. Over.
> 
> 102:38:30 Armstrong: (To Houston) It's a 1202.
> 
> 102:38:32 Aldrin: 1202. (Pause)
> 
> [Altitude 33,500 feet. In the 16mm film record, the lunar surface can be seen in the very bottom of Buzz's window. As indicated in Figure 5-5 in the Mission Report, the LM is pitched back about 77 degrees at this time but will start coming noticeably more upright between now and P64.]
> 
> 102:38:42 Armstrong (on-board): (To Buzz) What is it? Let's incorporate (the landing radar data). (To Houston) Give us a reading on the 1202 Program Alarm.
> 
> [The 1202 program alarm is being produced by data overflow in the computer. It is not an alarm that they had seen during simulations but, as Neil explained during a post-flight press conference "In simulations we have a large number of failures and we are usually spring-loaded to the abort position. And in this case in the real flight, we are spring-loaded to the land position."]

It seems amazing that this could have happened at such a critical moment, given how much money and time had been invested in this project.  Well, actually, it doesn't seem that amazing given what we know about software ... it's just another notch on the rather long list of near-software-disasters.  I guess the real question is: *what are they doing to prevent this from happening next time?* Gerrard talks about this issue at length, and he argues that things certainly have improved...

**UPDATE:** It seems that the[ SPARK programming language is going to be involved in the lunar lander project](http://www.electronics-eetimes.com/en/lunar-lander-project-relies-on-spark-programming-language.html?cmp_id=7&news_id=222902326&vID=296), which is definitely a step in the right direction!
