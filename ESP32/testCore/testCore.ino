TaskHandle_t Task1;
TaskHandle_t Task2;
#include <ESP32Time.h>

// LED pins
ESP32Time rtc;

void setup() {
  Serial.begin(9600); 
  pinMode(GPIO_NUM_21, OUTPUT);  //task core 0
  pinMode(GPIO_NUM_23, OUTPUT);  //task core 1

  rtc.setTime(0, 59, 23, 8,11, 2001);

  //create a task that will be executed in the Task1code() function, with priority 1 and executed on core 0
  xTaskCreatePinnedToCore(
                    Task1code,   /* Task function. */
                    "Task1",     /* name of task. */
                    10000,       /* Stack size of task */
                    NULL,        /* parameter of the task */
                    1,           /* priority of the task */
                    &Task1,      /* Task handle to keep track of created task */
                    0);          /* pin task to core 0 */                  
  delay(500); 
  disableCore0WDT();
  //create a task that will be executed in the Task2code() function, with priority 1 and executed on core 1
//  xTaskCreatePinnedToCore(
//                    Task2code,   /* Task function. */
//                    "Task2",     /* name of task. */
//                    10000,       /* Stack size of task */
//                    NULL,        /* parameter of the task */
//                    1,           /* priority of the task */
//                    &Task2,      /* Task handle to keep track of created task */
//                    1);          /* pin task to core 1 */
//    delay(500); 
}

//Task1code: blinks an LED every 1000 ms
void Task1code( void * pvParameters ){
 

  for(;;){
    Serial.print("Task1 running on core ");
    Serial.println(xPortGetCoreID());
    digitalWrite(GPIO_NUM_21, HIGH);
    delay(2000);
    digitalWrite(GPIO_NUM_21, LOW);
    delay(2000);
  } 
}


void loop() {
    //Serial.print("Task2 running on core ");
    //Serial.println(xPortGetCoreID());

    Serial.print(rtc.getHour(true));
    Serial.print(":");
    Serial.print(rtc.getMinute());
    Serial.print(":");
    Serial.println(rtc.getSecond());
    delay(1000);
}
