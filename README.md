# SmartThings-Raspberry-Arduino
Smarttings to Raspberry to Arduino RF24 project
Disclaimer: 
This is the first time I ever tried a public project like this. 
I have been struggling but having fun learning the Arduino, Smartthings, Raspberry plateform for the last few weeks 
and decided that I would post my code and start a conversation on different ways to do this. 

My idea is the hook the arduino wirelessly to 5 relays to power 5 sprinkler valves. I have 5 raised garden bed I want to 
wire automatically

Here is the basic setup
RaspberryPi wired to a NRF24l01 transeiver
ARduino Uno wired to a NRF24l01 transeiver 

Step 1
Wiring was done using this turorial on youtube. Note the there are two parts: 
Part 1 is getting the devices talking one direction
Part 2 is the two way communciation with the Ardunio waiting for commands from the Raspberry
https://www.youtube.com/watch?v=_68f-yp63ds

Step 2 invoved a python script (adcommand.py)  that accepts a single argument which is transmitted to the Arduino.
The Arduino  recognize the argument and sends back a response. 
Right now the argument is pretty simple
x:y 
where x is the led/relay # 1-5 and y is the on(1) or off (0) command 

or sending 0 turns all led/relay off  and 9 returns a status. 

Status comes back the same form (x:y) so it can be parsed by smartthings. 

Step 3
index.php script running on the Raspberry Apache2 server. This simple php script to accept post commands.
LedxOn or LedxOff
where x is 1-5
post command refresh will return a status of all pin/led/Relays

Step 4
smartthign HTTP device type modified to send post commands to the raspberry server and parse the return to. 
I have 5 of these device type configured in 



