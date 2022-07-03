---
date: 2014-03-02
title: "Flying the CrazyFlie Quadcopter!"
draft: true
---

Last week, my <a href="http://www.bitcraze.se/crazyflie/">CrazyFlie nano-quadcopter</a> finally arrived and, since then, I've been learning how to fly! The copter is much smaller than I was expecting, and it requires you to solder a few bits together.

<a href="http://whiley.org/wp-content/uploads/2014/03/IMG_2402.jpg"><img style="border: 0px none;" alt="CrazyFlie Quadcopter" src="http://whiley.org/wp-content/uploads/2014/03/IMG_2402_small.jpg" width="269" height="201" /></a><a href="http://whiley.org/wp-content/uploads/2014/03/IMG_2398.jpg"><img style="border: 0px none;" alt="CrazyFlie Quadcopter" src="http://whiley.org/wp-content/uploads/2014/03/IMG_2398_small.jpg" width="269" height="201" /></a>

At first, I had a lot of problems trying to fly it.  After a lot of fairly major crashes, I figured out the problem was interference from Wifi devices.  After trying out a few different frequency channels, a <a href="http://forum.bitcraze.se/viewtopic.php?f=5&amp;t=661">forum post tipped me off to channel 80</a> --- and that works much better.  I'm using a Playstation controller connected by USB to my MacBook, and that's a good setup.  I've also flown it from my Android phone as well, but that's much harder.

Here's a video of me practicing my flight skills:

<iframe width="560" height="315" src="//www.youtube.com/embed/lVehvLJZumE" frameborder="0" allowfullscreen></iframe>

At this point, you can see I'm starting to get the hang of it.  The copter is surprisingly responsive and flies nicely.  You can fly outside as well, but wind is definitely an issue and it quickly becomes unstable when there's a gust.  

From my perspective, the interesting thing about the CrazyFlie is that the <a href="https://github.com/bitcraze/crazyflie-firmware">firmware is completely open source</a> and builds on <a href="http://www.freertos.org/">FreeRTOS</a>.  That means, in principle at least, I can run Whiley code on the copter itself ...