---
date: 2012-12-20
title: "Testing out my Papilio FPGA!"
draft: false
---

Recently, I got hold of a [Papilio One](http://papilio.cc/index.php?n=Papilio.Hardware) (which you can think of as the Arduino of FPGAs).  The Papilio board has a {{<wikip page="Xilinx#Spartan_family">}}Xilinx Spartan 3{{</wikip>}} on board, which is plenty enough to get started learning about FPGAs.  Here's what the board looks like:

{{<img class="text-center" width="400px" src="/images/2012/Papillo.jpg">}}

Now, it might look big above ... but it's not!  You can see the Spartan FPGA in the middle, the micro USB port in the bottom left and the three banks of I/O pins (two in the top left, one in the top right).

Now, the question is: <em>what do you do with this thing?</em> Well, that's what I've been figuring out.  The first thing you need is the [Xilinx IDE](http://www.xilinx.com/products/design-tools/ise-design-suite/ise-webpack.htm) which you can download and use for free.  Then, you need to learn {{<wikip page="VHDL">}}VHDL{{</wikip>}} or {{<wikip page="Verilog">}}Verilog{{</wikip>}}. I went for VHDL and have been reading "Free Range VHDL" which has a [freely available electronic version](http://www.freerangefactory.org/site/pmwiki.php/Main/Books).  Then, I got hold of a bread board and some LEDs and connected it all up like this:

{{<img class="text-center" width="400px" src="/images/2012/Papillo_connected.jpg">}}

Here, we've got two LEDs connected to the output pins of the Papilio.  Then, I wrote a VHDL program which implements a two bit counter clocked by one of the inputs (i.e. so the counter increments whenever we see a rising edge on the input):

```VHDL
entity Counter is
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
signal counter : STD_LOGIC_VECTOR(1 downto 0) := (others => '0');
begin
process(W1A(0))
begin
 if rising_edge(W1A(0)) then
  counter <= counter +1;
 end if;
end process;
 W2C(1 downto 0) <= counter;
 W2C(15 downto 2) <= "00000000000000";
 W1B <= "0000000000000000";
end Behavioral;
```

You can see here that pin <code>0</code> from the A bank of inputs (i.e. <code>W1A</code>) is being used to clock the <code>counter</code>.  This is then written to the first 2 pins of the C bank (i.e. <code>W2C</code>), with all other output pins pulled to <code>0</code>.  Anyhow, you can see the whole in action here:

{{<youtube id="mrcnzZ8uPrc">}}

Anyway, that's a bit of fun.  But, the question is: <em>why am I doing this?</em> Well, the answer is that: <em>I want to compile Whiley programs to run on an FPGA</em>.  Obviously, this is going to take a while to get going, but these are the first steps.  The plan is use the Papilio to drive my tracked robot (which is currently controlled by an Arduino):

{{<youtube id="pk1uAHJIMXg">}}

Anyhow, that's what I've been working on today ...
