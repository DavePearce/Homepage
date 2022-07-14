---
date: 2014-03-02
title: "Flying the CrazyFlie Quadcopter!"
draft: false
---

Last week, my [CrazyFlie nano-quadcopter](https://www.bitcraze.io/products/old-products/crazyflie-1-0/) finally arrived and, since then, I've been learning how to fly! The copter is much smaller than I was expecting, and it requires you to solder a few bits together.

{{<img class="text-center" width="75%" src="/images/2014/CrazyFlie_1.jpg">}}
{{<img class="text-center" width="75%" src="/images/2014/CrazyFlie_2.jpg">}}

At first, I had a lot of problems trying to fly it.  After a lot of fairly major crashes, I figured out the problem was interference from Wifi devices.  After trying out a few different frequency channels, a [forum post tipped me off to channel 80](http://forum.bitcraze.se/viewtopic.php?f=5&amp;t=661) --- and that works much better.  I'm using a Playstation controller connected by USB to my MacBook, and that's a good setup.  I've also flown it from my Android phone as well, but that's much harder.

Here's a video of me practicing my flight skills:

{{<youtube id="lVehvLJZumE">}}

At this point, you can see I'm starting to get the hang of it.  The copter is surprisingly responsive and flies nicely.  You can fly outside as well, but wind is definitely an issue and it quickly becomes unstable when there's a gust.  

From my perspective, the interesting thing about the CrazyFlie is that the [firmware is completely open source](https://github.com/bitcraze/crazyflie-firmware) and builds on [FreeRTOS](http://www.freertos.org/).  That means, in principle at least, I can run Whiley code on the copter itself ...
