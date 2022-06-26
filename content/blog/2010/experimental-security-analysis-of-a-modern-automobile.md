---
date: 2010-06-15
title: "Experimental Security Analysis of a Modern Automobile"
draft: true
---

Another good paper I found recently was the following:

"Experimental Security Analysis of a Modern Automobile", Hoscher, Czeskis, et al. In *Proceedings of IEEE Symposium on Security and Privacy*, 2010.[ [PDF]](http://www.autosec.org/pubs/cars-oakland2010.pdf)

What I found particularly interesting was the discussion of the computing setup on a modern car.  Here's some info:
   * Most modern cars feature multiple network buses, typically either [Controller Area Network (CAN)](http://en.wikipedia.org/wiki/Controller_area_network) or [FlexRay](http://en.wikipedia.org/wiki/FlexRay).  For example, a high-speed bus may connect power-train components that generate real-time telemetry, whilst a low speed bus might control simple actuators found in doors and lights.  Furthermore, these buses are typically bridged ... meaning networks are not isolated from one another.  This should be concerning, since we have safety-critical components (e.g. car alarm, engine control module) alongside simple third-party components (e.g. CD player).

   * When communicating over CAN,  packets are always broadcast to every node.  Individual components must decide whether or not to do something in response to a packet.  Furthermore, there are no real identifier fields, meaning any component can send anonymously.  Because of this, CAN is particularly vulnerable to [denial-of-service](http://en.wikipedia.org/wiki/Denial-of-service_attack) attacks.  But, it's also vulnerable to more complex forms of attack using e.g. [packet injection](http://en.wikipedia.org/wiki/Packet_injection).

   * Each component will typically have a variety of actions that require authorisation.  For example, upgrading the firmware is a common and sensitive operation, another is the generation of diagnostic information --- all of which are useful for service technicians.  The car studied in the paper used a [challenge-response protocol](http://en.wikipedia.org/wiki/Challenge-response_authentication) for authorising operations on individual components.  Unfortunately, the necessary algorithms for generating the required keys are well known and in wide circulation.  Furthermore, the keys were only 16bits long and the authors managed to recover one using a brute-force approach is less than two days.


I think we're probably starting to get the picture now.  The essence of the paper is that author's took a fairly standard car and attacked in a variety of ways to determine what was possible.  They managed to do things like upgrading the firmware of the Engine Control Module whilst the car was running --- at which point the engine stops running immediately.  Other interesting things include the ability to pop the trunk whilst at speed, deactivating the brakes at speed, or instantly putting them on!  It's pretty scary stuff when you think about it; particularly, as you might install a seemingly innocent new CD-player .... which then decides to randomly take over your car when you least expect it.

Anyway, worth a read ...

http://www.google.co.nz/url?sa=t&source=web&cd=1&ved=0CB0QFjAA&url=http%3A%2F%2Fwww.autosec.org%2Fpubs%2Fcars-oakland2010.pdf&rct=j&q=experimental+security+analysis+of+modern+automobile&ei=4EUMTKLXL5qmNdew0dwB&usg=AFQjCNEy3Jxasa_TrcM_qWXmvPXfZaaxWA