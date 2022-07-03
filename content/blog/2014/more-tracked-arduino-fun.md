---
date: 2014-01-31
title: "More Tracked Arduino Fun!"
draft: true
---

Recently, I've been upgrading my <a href="http://youtu.be/pk1uAHJIMXg">tracked arduino robot</a> with a few more sensors.  Check out the video:

<center><iframe width="560" height="315" src="//www.youtube.com/embed/Lonxtf0-WIk" frameborder="0" allowfullscreen></iframe></center>

The robot has two medium range IR sensors (front and back), as well as a downward facing short-range IR sensor.  When the front sensor detects something is very close, the robot backs up (provided the back sensor says there is space).  Finally, when the downward sensor says there is nothing below, the robots stops and won't move any further!

For reference, here's the Arduino code for the robot:
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
const int SLOW = 100;
const int MEDIUM = 200;
const int FAST = 255;

void setMotors(int left, int right) {
 analogWrite(pwmA, left);   
 analogWrite(pwmB, right);   
}

void setDirections(int left, int right) {
  digitalWrite(dirA,left);
  digitalWrite(dirB,right);
}

void brakesOff() {
 digitalWrite(brakeA,LOW);
 digitalWrite(brakeB,LOW); 
}

/* ============================================= */
/* Sensor Code */
/* ============================================= */
const int FRONT_SENSOR_PIN = 0;
const int UNDERNEATH_SENSOR_PIN = 1;
const int BACK_SENSOR_PIN = 2;

const int VERY_CLOSE = 0;
const int CLOSE = 1;
const int FAR = 2;

int frontSensor() {
 int x = analogRead(FRONT_SENSOR_PIN);
 
 if(x &gt; 400) {
  return VERY_CLOSE;
 } else if(x &gt; 300) {
  return CLOSE;
 } else {
  return FAR;
 } 
}

int backSensor() {
 int x = analogRead(BACK_SENSOR_PIN);
 
 if(x &gt; 600) {
  return VERY_CLOSE;
 } else if(x &gt; 400) {
  return CLOSE;
 } else {
  return FAR;
 } 
}

int underneathSensor() {
 int x = analogRead(UNDERNEATH_SENSOR_PIN);
 
 if(x &lt; 300) {
  return CLOSE;
 } else {
  return FAR;
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
 pinMode(FRONT_SENSOR_PIN, INPUT);
 pinMode(UNDERNEATH_SENSOR_PIN, INPUT);

 analogWrite(pwmA, STOP);        
 analogWrite(pwmB, STOP);
 
 setDirections(HIGH,HIGH);
 brakesOff();

 delay(3000);
}

/* ============================================= */
/* Controller Code */
/* ============================================= */

void loop() {  
  int front = frontSensor();
  int back = backSensor();  
  int underneath = underneathSensor();
  
  if(underneath == CLOSE) {
    // Something underneath, continue.
    switch(front) {
      case VERY_CLOSE:
        if(back != VERY_CLOSE) {
          // Only continue going backwards if 
          // there's nothing behind us.
          setDirections(LOW,LOW);
          setMotors(FAST,FAST);      
        }
        break;
      case CLOSE:
        setDirections(HIGH,HIGH);
        setMotors(STOP,STOP);     
        break;
      case FAR:
        setDirections(HIGH,HIGH);
        setMotors(FAST,FAST);
        break;
      }
  } else {
      // Nothing underneath, all ahead stop. 
        setDirections(HIGH,HIGH);
        setMotors(STOP,STOP);           
  }
  
  delay(100);
}

[/c]