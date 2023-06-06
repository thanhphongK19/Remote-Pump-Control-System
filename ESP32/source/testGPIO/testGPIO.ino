#include <ESP32Time.h>

ESP32Time rtc;
void setup() {
  // put your setup code here, to run once:
  pinMode(GPIO_NUM_21, OUTPUT);
  
  Serial.begin(115200);
  rtc.setTime(0, 0, 15, 8,11, 2001); 
}

void loop() {
  // put your main code here, to run repeatedly:
    /*digitalWrite(GPIO_NUM_21, HIGH);
    delay(5000);
    digitalWrite(GPIO_NUM_21, LOW);
    delay(5000);*/

    Serial.print(rtc.getHour(true));
    Serial.print(":");
    Serial.print(rtc.getMinute());
    Serial.print(":");
    Serial.println(rtc.getSecond());
    delay(1000);
}
