---
date: 2011-05-26
title: "JavaScript Playground"
draft: false
---

I've been doing a bit of JavaScript programming recently, and I came up with the "Creature Playground" (which is primarily aimed at teaching).  Here's a little taster (click on "Create" a few times):

{{<rawhtml>}}
<hr/>
<center><canvas id="display" width="400" height="400"><h2>Oh No!</h2>Your web-browser does not appear to support the HTML 5 Canvas.  Sorry, but this means the playground won't work.  Try using <a href="http://www.mozzila.com">Mozilla Firefox</a> instead!</canvas></center>

<script type="text/javascript">
function random(min,max) {
  return min + Math.floor(Math.random() * (max-min));
}
function create() {
  var canvas = document.getElementById("display");
  var display = canvas.getContext("2d");
  var x = random(50,350);
  var y = random(50,350);
  var width = 10;
  var height = 10;
  var dx = 5;
  var dy = 5;
  function onTick() {
    display.fillStyle = "#FFFFFF";
    display.fillRect(x,y,width,height);
    x = x + dx;
    y = y + dy;
    if(y <= 5 || y >= 385) { dy = -dy; }
    if(x <= 5 || x >= 385) { dx = -dx; }
    display.fillStyle = "#000000";
    display.fillRect(x,y,width,height);
  }
  display.fillStyle="#F0F0F0";
  display.strokeRect(0,0,400,400);
  setInterval(onTick,25);
}
</script>
<button onclick="create()">Create</button>
<hr/>
{{</rawhtml>}}

In my full version, you can modify the creature's code directly and play around with different stuff.  I couldn't manage to embed it in this blog properly unfortunately ...

The source code for this little ditty is:

```javascript
function random(min,max) {
  return min + Math.floor(Math.random() * (max-min));
}

function create() {
  var canvas = document.getElementById('display');
  var display = canvas.getContext('2d');
  var x = random(50,350);
  var y = random(50,350);
  var width = 10;
  var height = 10;
  var dx = 5;
  var dy = 5;

  function onTick() {
    // this is called every 25ms
    display.fillStyle = '#FFFFFF';
    display.fillRect(x,y,width,height);
    x = x + dx;
    y = y + dy;
    if(y <= 5 || y >= 385) { dy = -dy; }
    if(x <= 5 || x >= 385) { dx = -dx; }
    display.fillStyle = '#000000';
    display.fillRect(x,y,width,height);
  }
  // draw the border
  display.fillStyle='#F0F0F0';
  display.strokeRect(0,0,400,400);
  // start the timer
  setInterval(onTick,25);
}
```

I must say, it's amazing how easy it is to get stuff done in JavaScript!!
