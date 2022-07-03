---
date: 2012-12-20
title: "Testing out my Papilio FPGA!"
draft: true
---

Recently, I got hold of a <a href="http://papilio.cc/index.php?n=Papilio.Hardware">Papilio One</a> (which you can think of as the Arduino of FPGAs).  The Papilio board has a <a href="http://en.wikipedia.org/wiki/Xilinx#Spartan_family">Xilinx Spartan 3</a> on board, which is plenty enough to get started learning about FPGAs.  Here's what the board looks like:
<p style="text-align: center;"><a href="http://whiley.org/wp-content/uploads/2012/12/IMG_1279.jpg"><img class="aligncenter size-full wp-image-4769" style="border: 0px;" title="Papilio One 500" src="http://whiley.org/wp-content/uploads/2012/12/IMG_1279.jpg" alt="" width="608" height="456" /></a></p>
Now, it might look big above ... but it's not!  You can see the Spartan FPGA in the middle, the micro USB port in the bottom left and the three banks of I/O pins (two in the top left, one in the top right).

Now, the question is: <em>what do you do with this thing?</em> Well, that's what I've been figuring out.  The first thing you need is the <a href="http://www.xilinx.com/products/design-tools/ise-design-suite/ise-webpack.htm">Xilinx IDE</a> which you can download and use for free.  Then, you need to learn <a href="http://en.wikipedia.org/wiki/VHDL">VHDL</a> or <a href="http://en.wikipedia.org/wiki/Verilog">Verilog</a>. I went for VHDL and have been reading "Free Range VHDL" which has a <a href="http://www.freerangefactory.org/site/pmwiki.php/Main/Books">freely available electronic version</a>.  Then, I got hold of a bread board and some LEDs and connected it all up like this:
<p style="text-align: center;"><a href="http://whiley.org/wp-content/uploads/2012/12/IMG_1286.jpg"><img class="aligncenter size-full wp-image-4772" style="border: 0px;" title="Papilio One connected to a Bread Board" src="http://whiley.org/wp-content/uploads/2012/12/IMG_1286.jpg" alt="" width="456" height="608" /></a></p>
Here, we've got two LEDs connected to the output pins of the Papilio.  Then, I wrote a VHDL program which implements a two bit counter clocked by one of the inputs (i.e. so the counter increments whenever we see a rising edge on the input):
<pre>entity Counter is
Port (
 W1A : in  STD_LOGIC_VECTOR(15 downto 0);
 W1B : out  STD_LOGIC_VECTOR(15 downto 0);
 W2C : out  STD_LOGIC_VECTOR(15 downto 0);
 rx : in STD_LOGIC;
 tx : inout STD_LOGIC;
 clk : in STD_LOGIC
);
end Counter;

architecture Behavioral of Counter is
signal counter : STD_LOGIC_VECTOR(1 downto 0) := (others =&gt; '0');
begin
process(W1A(0))
begin
 if rising_edge(W1A(0)) then
  counter &lt;= counter +1;
 end if;
end process;
 W2C(1 downto 0) &lt;= counter;
 W2C(15 downto 2) &lt;= "00000000000000";
 W1B &lt;= "0000000000000000";
end Behavioral;</pre>
You can see here that pin <code>0</code> from the A bank of inputs (i.e. <code>W1A</code>) is being used to clock the <code>counter</code>.  This is then written to the first 2 pins of the C bank (i.e. <code>W2C</code>), with all other output pins pulled to <code>0</code>.  Anyhow, you can see the whole in action here:
<center><iframe width="420" height="315" src="http://www.youtube.com/embed/mrcnzZ8uPrc" frameborder="0" allowfullscreen></iframe></center>
Anyway, that's a bit of fun.  But, the question is: <em>why am I doing this?</em> Well, the answer is that: <em>I want to compile Whiley programs to run on an FPGA</em>.  Obviously, this is going to take a while to get going, but these are the first steps.  The plan is use the Papilio to drive my tracked robot (which is currently controlled by an Arduino):
<center><iframe width="560" height="315" src="http://www.youtube.com/embed/pk1uAHJIMXg" frameborder="0" allowfullscreen></iframe></center>
Anyhow, that's what I've been working on today ...