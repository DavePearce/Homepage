---
date: 2015-01-27
title: "My ATtiny85 Games Console"
draft: false
---

One my goals for Whiley in 2015 is to focus more on embedded systems (see here for more). A recent project of ours was [compiling Whiley code to run on a QuadCopter](https://whileydave.com/publications/Stevens14_ENGR489.pdf) and this identified several challenges here.  In particular, Whiley does not provide good memory management for such resource constrained environments. My plan is to introduce various features to provide more control to the programmer, such as [object lifetimes in Rust](http://rustbyexample.com/lifetime.html), and for testing I want an extremely resource constrained device.  As such, I've been developing a simple embedded system based around the [Amtel ATtiny85 microcontroller](http://www.atmel.com/devices/attiny85.aspx).  This has only 512bytes for SRAM, the same amount of EEPROM and 8K of Flash.

The console is pretty simple and only contains an 8x8 LED display and a little joystick for input.  Initially, I implemented the console on some prototyping board:

{{<img class="text-center" width="640px" src="/images/2015/IMG_3293-small.jpg">}}

Here, you can see the ATtiny85 is the little chip right in the middle between the display and the joystick.  The Arduino UNO is there only to provide power for the board.  After I got this working, I then designed a PCB in Eagle and it was milled out in our workshop:

{{<img class="text-center" width="640px" src="/images/2015/TinyConsolePCB.jpg">}}
{{<img class="text-center" width="640px" src="/images/2015/TinyConsoleFinal.jpg">}}

As you can see, the board includes it's own jack with a voltage regulator so it can run off a 9V battery.  There's an LED as well showing power, and that's really about it!!  Here's a little video showing the console in action with an implementation of Tetris I wrote:

{{<youtube id="rKzNA7W7LV4" width="560px" height="315">}}

In practice, the display is rather small but it is enough for simple games like Tetris or Snake.  My plan for the future is to develop a second generation console which includes a bigger display, and various other useful features (e.g. USB connector, SD Card connector, some buttons, etc).  Also, I'm not sure about the joystick itself, as it's a little clunky for a device like this.  Some kind of four-way rocker switch would probably be better.
