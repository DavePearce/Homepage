---
date: 2012-12-12
title: "Building an Arduino Robot (for Testing Whiley)"
draft: true
---

The <a href="http://whiley.org">Whiley programming language</a> is about developing more reliable software and, of course, embedded systems is one of the biggest areas that could benefit. Obviously, then, we need an "embedded system" to test Whiley with, right?  At least, that's the thinking behind my recent endeavor to create an <a href="http://en.wikipedia.org/wiki/Arduino">Arduino-based</a> Robot.

After some discussion with our workshop folks, I managed to acquire the following bits:
<p style="text-align: center;"><a href="http://whiley.org/wp-content/uploads/2012/12/ArduinoRobot.jpg"><img class="aligncenter size-full wp-image-4607" style="border: 0px;" title="Basic Kit for an Arduino Robot" src="http://whiley.org/wp-content/uploads/2012/12/ArduinoRobot.jpg" alt="" width="560" height="418" /></a></p>
<p style="text-align: left;">Here, we have an Arduino Uno (top right), a <a href="http://www.hobbyist.co.nz/?q=motor-shield-tutorial">Motor Shield</a> (bottom right) and a <a href="https://www.sparkfun.com/products/319?">Tamiya dual motor gearbox</a>.  The Motor shield sits on top of the Arduino Uno, and provides necessary power for driving the two DC motors on the Tamiya gearbox.  I also acquired an Infra-Red sensor from our workshop, which can be used for determining distance to objects ahead:</p>
<p style="text-align: left;"></p>
<p style="text-align: left;"></p>
<p style="text-align: center;"><a href="http://whiley.org/wp-content/uploads/2012/12/IMG_1259.jpg"><img class="size-full wp-image-4624    aligncenter" style="border: 0px;" title="Infra-Red Sensor" src="http://whiley.org/wp-content/uploads/2012/12/IMG_1259.jpg" alt="" width="560" height="420" /></a></p>
<p style="text-align: left;">This sensor plugs into the I/O pins of the Arduino and we can then read its current value from our Arduino program and make decisions based on this.  To complete the robot, we cut a base plate on to fix everything on to ... and a robot was born:</p>
<p style="text-align: center;"><a href="http://whiley.org/wp-content/uploads/2012/12/IMG_1269.jpg"><img class="aligncenter size-full wp-image-4632" style="border: 0px;" title="View from Behind the Robot" src="http://whiley.org/wp-content/uploads/2012/12/IMG_1269.jpg" alt="" width="560" height="420" /></a><a href="http://whiley.org/wp-content/uploads/2012/12/IMG_1275.jpg"><img class="aligncenter size-full wp-image-4633" style="border: 0px;" title="Front View of Robot" src="http://whiley.org/wp-content/uploads/2012/12/IMG_1275.jpg" alt="" width="560" height="420" /></a></p>
<p style="text-align: left;">The robot really needs a better front wheel, and probably some more interesting sensors!  But, for now, it's enough to get the hang of programming the Arduino.  Here's my first program for it:</p>

[c]
/* ============================================= */
/* Motor Code */
/* ============================================= */
const int pwmA = 3;
const int pwmB = 11;
const int dirA = 12;
const int dirB = 13;
const int brakeA = 9;
const int brakeB = 8;

const int STOP = 0;
const int SLOW = 50;
const int MEDIUM = 75;
const int FAST = 100;

void setMotors(int left, int right) {
 analogWrite(pwmA, left);
 analogWrite(pwmB, right);
}

void setReverse() {
  digitalWrite(dirA,HIGH);
  digitalWrite(dirB,HIGH);
}

void setForward() {
  digitalWrite(dirA,LOW);
  digitalWrite(dirB,LOW);
}

void brakesOff() {
 digitalWrite(brakeA,LOW);
 digitalWrite(brakeB,LOW);
}

/* ============================================= */
/* Sensor Code */
/* ============================================= */
const int VERY_CLOSE = 0;
const int CLOSE = 1;
const int FAR = 2;
const int NOTHING = 3;

int frontSensor() {
 int x = analogRead(0);
 if(x &gt; 400) {
  return VERY_CLOSE;
 } else if(x &gt; 300) {
  return CLOSE;
 } else if(x &gt; 200) {
  return FAR;
 } else {
  return NOTHING;
 }
}

/* ============================================= */
/* Setup Code */
/* ============================================= */

void setup() {
 pinMode(pwmA, OUTPUT);  //Set control pins to be outputs
 pinMode(pwmB, OUTPUT);
 pinMode(dirA, OUTPUT);
 pinMode(dirB, OUTPUT);

 analogWrite(pwmA, STOP);
 analogWrite(pwmB, STOP);

 setForward();
 brakesOff();

 delay(3000);
}

/* ============================================= */
/* Controller Code */
/* ============================================= */

void loop()
{
  int sensor = frontSensor();

  switch(sensor) {
    case VERY_CLOSE:
      setReverse();
      setMotors(FAST,FAST);
      break;
    case CLOSE:
      setForward();
      setMotors(STOP,STOP);
      break;
    case FAR:
      setForward();
      setMotors(MEDIUM,MEDIUM);
      break;
    case NOTHING:
      setForward();
      setMotors(FAST,FAST);
      break;
  }
  delay(100);
}
[/c]

This program moves the robot forward at the <code>FAST</code> speed, until an object comes into view.  When the object is <code>FAR</code> away, the motors are reduced to <code>MEDIUM</code> speed; when the object is <code>CLOSE</code>, the motors are set to <code>STOP</code>;  finally, when the object is <code>VERY_CLOSE</code>, the motors are reversed.  Here's a video of it working:
<center>
<iframe width="560" height="315" src="http://www.youtube.com/embed/OAxa996lS50" frameborder="0" allowfullscreen></iframe>
</center>
Building the robot wasn't too hard, although I did have to dig out my soldering iron for the first time in quite a few years! And, having the workshop folks around was a great help, as they gave me useful advice and had access to equipment and tools (esp. for cutting the base plate).

For now, I'm still learning and will look to improve the robot's capability to get a more interesting level of complexity.  Ultimately, I want to be able to compile Whiley code down to run on the Arduino.  We're currently working on a C back-end for Whiley, and this will provide the launching pad for getting Whiley onto the Arduino (and, perhaps, other similar devices as well).

Comments Welcome!
<p style="text-align: left;"></p>