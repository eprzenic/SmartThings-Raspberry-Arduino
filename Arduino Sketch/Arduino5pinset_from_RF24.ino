#include <SPI.h>

#include <nRF24L01.h>
#include <printf.h>
#include <RF24.h>
#include <RF24_config.h>
#include <stdio.h>

RF24 radio(9,10);
 

void setup() {
  // put your setup code here, to run once:
  while(!Serial);
  Serial.begin(9600);

 radio.begin();
 radio.setPALevel(RF24_PA_MAX);
 radio.setChannel(0x76) ;

 const uint64_t pipe = 0xE8E8F0F0E1LL ;
 
 radio.openReadingPipe(1,pipe);
 radio.openWritingPipe(0xF0F0F0F0E1LL);
 radio.enableDynamicPayloads();
 radio.powerUp();
 Serial.println("Starting loop.Radio on");
 pinMode(2, OUTPUT);
 pinMode(3, OUTPUT);
 pinMode(4, OUTPUT);
 pinMode(5, OUTPUT);
 pinMode(6, OUTPUT);

 

 

}

void loop() {
  // put your main code here, to run repeatedly:
 radio.startListening();

 int iopin[] = {0,2,3,4,5,6};
 String stsstr[] = {"0","1"}; 
 char receivedMessage[64]={0};

 if (radio.available()){


  radio.read(receivedMessage, sizeof(receivedMessage));

  radio.stopListening();

  String stringMessage(receivedMessage) ;
  String iostr(receivedMessage[0]);
  String setstr(receivedMessage[2]);
  
  int iomsg = int(iostr.toInt());  //Convert Message to int
  int setmsg = int(setstr.toInt()); //Convert message to High/Low
  char text[32] = {0};  
       String newMsg = "";

     Serial.println(stringMessage);
   switch (iomsg) {
  
    case 9: 
     for (int x = 1;x < 6;x++) {
        
        int sts = digitalRead(iopin[x]);
        
        newMsg= newMsg + "("+String(x) + ":"+  stsstr[sts] + ")";
     }

      newMsg.toCharArray(text,64);
      radio.write(text,sizeof(text));

 
      break;
    
    case 0: 
     for (int x = 1;x < 6;x++) {
        digitalWrite(iopin[x],LOW);
        int sts = digitalRead(iopin[x]);
        
        newMsg= newMsg + "("+String(x) + ":0)";
     }

      newMsg.toCharArray(text,32);
      radio.write(text,sizeof(text));
       break;
    
   default: 
//    Serial.println(iopin[iomsg]);
            
    digitalWrite(iopin[iomsg], setmsg);   // turn the LED on (HIGH is the voltage level)
 
    stsstr[setmsg] = "("+String(iomsg) + ":" +  stsstr[setmsg] + ")";
    stsstr[setmsg].toCharArray(text,32);
    radio.write(text,sizeof(text));
 
     break;
   
  }



  }

}

