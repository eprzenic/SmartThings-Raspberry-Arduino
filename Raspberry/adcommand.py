import RPi.GPIO as GPIO
from lib_nrf24 import NRF24
import time
import spidev
from sys import argv

script, first   = argv


#GPIO.cleanup()
GPIO.setwarnings(False)
GPIO.setmode(GPIO.BCM)



pipes = [[0xE8, 0XE8, 0xF0, 0xF0, 0xE1], [0xF0, 0xF0, 0xF0, 0xF0, 0xE1]]

radio = NRF24(GPIO, spidev.SpiDev())
radio.begin(0,22)

radio.setPayloadSize(32)
radio.setChannel (0x76)
radio.setDataRate(NRF24.BR_1MBPS)
radio.setPALevel(NRF24.PA_MAX)

radio.setAutoAck(True)
radio.enableDynamicPayloads()
radio.enableAckPayload()

radio.openWritingPipe(pipes[0])
radio.openReadingPipe(1,pipes[1])
#radio.printDetails()
#radio.startListening()


message = list(first)

while len(message) < 32:
    message.append(0)
 
while True:
    start = time.time()
    radio.write(message)
#    print(message)
    radio.startListening()
    

    while not radio.available(0):
        time.sleep(1/100)
        if time.time() - start > 2:
            print("Time Out.")
            break

    receivedMessage = []
    radio.read(receivedMessage, radio.getDynamicPayloadSize())
    string = ""

    for n in receivedMessage:
          if (n>=32 and n <=126) :
              string += chr(n)   
    print (format(string))

    radio.stopListening()
    break    

              
