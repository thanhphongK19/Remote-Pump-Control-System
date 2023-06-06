#include <WiFi.h>
#include <FirebaseESP32.h>
#include <ESP32Time.h>
#include <NTPClient.h>
#include <WiFiUdp.h>



/////////////////////////////////////////////////////////////////
#if 1/* Setup wifi,GPIO and firebase */

#define WIFI_SSID "Windpink"
#define WIFI_PASSWORD "11111111"

#define pumpOn HIGH
#define pumpOff LOW

#define FIREBASE_HOST "remotepumpbt-default-rtdb.firebaseio.com"
#define FIREBASE_AUTH "VzDvXA237afCFL7WY5geD2ZWfFEIcRhOQE0sN65y"



#endif
/////////////////////////////////////////////////////////////////
#if 1/* Global variable */
	  FirebaseData database;
	  FirebaseData toggleWifi;
	  
	  WiFiUDP ntpUDP;
	  NTPClient timeClient(ntpUDP);
	  
	  TaskHandle_t Task_wifi;
	  
	// Real time clock
	  ESP32Time rtc;

	// Data recieve from firebase
	  unsigned char dataSend[6];
	  
	/* Data firebase */
	// Data download
	  unsigned char Pump = 0;
	  unsigned char alarmPump = 0;
	  unsigned char startHour = 0;
	  unsigned char startMinute = 0;
	  unsigned char endHour = 0;
	  unsigned char endMinute = 0;
	  unsigned char wifi = 0;
	  
	/* Time for alarm */
	  struct timeAlarm {
		  unsigned char Hours;
		  unsigned char Minutes;
	  };
	  struct timeAlarm timeNow;
	  
	 /* Limited Time */
	  unsigned char sHour = 0 ;
	  unsigned char sMinute = 0;
	  unsigned char eHour = 0;
	  unsigned char eMinute = 0;
	 
	  
	 /* Time NTPClient */
	  struct timeHTTP {
		  unsigned char Hours;
		  unsigned char Minutes;
	  };
	  struct timeHTTP currentTime;
	  
	  String formattedDate;
	  
	  int splitT = formattedDate.indexOf("T");
	  int split1 = formattedDate.indexOf(":");
	 
	  
	// Data upload 
	  unsigned char statusPump = 0;
	  unsigned char statusAlarm = 0;
	  unsigned char statusWifi = 0;
	  
	/* Var global flag */
	  unsigned char glReadFireBaseFlag = 0;
	  unsigned char glUpLoadDataBase = 0;
	  unsigned char glResetTimerFlag = 0;
	  unsigned char glDisconnectWifiFlag = 0;
	  unsigned char glLimitedTimeFlag = 0;
	  
	/* variable timer loop 10ms */
	  hw_timer_t* timer = NULL;
	  portMUX_TYPE timerMux = portMUX_INITIALIZER_UNLOCKED;
	  unsigned char glFlagTimer10ms;

	/* Variable for millis */
	  unsigned long previousMillis = 0;
	  unsigned long currentMillis  = 0;
	  const long interval = 2000;  //2 second
	  
	  
	/* Time Interval for wait reconnecting wifi */
	  unsigned long currentConnectWifi = 0;
	  unsigned long previousConnectWifi = 0;
	  const long connectWifiInterval = 5000; // 5 second 
	  
	/* Time Interval for get time NTPClient */
	  unsigned char current = 0;
	  unsigned char previous = 0;
	  unsigned char intervalLoop = 1000;  //1 second
	
	/* Limit Time Pump */
	  unsigned char timePlay = 1;

	  
	  
  
#endif
/////////////////////////////////////////////////////////////////
#if 1/* Implement function */
		//void IRAM_ATTR onTimer();   // interupt timer for loop 10ms
		void setup();
		
	/* Task connecting wifi core 0*/
		void connectWifi(void *pvParameters);
		
		void loop();
		void operate();
		
	/* Store and Read EEPROM */
		void getTimeHTTP();
		

	/* write and read Data firebase */
		unsigned char readDataFireBase();
		void writeDataFirebase();
	  
	/* Set power Pump */
		void setPowerPump(unsigned char mode);
		
	/* Check data */
		void queryData();
		
	/* Alarm Pump */
	    void setTime(unsigned char hour, unsigned char minute);
		void getTime();
		unsigned char alarm(unsigned char hour, unsigned char minute);
	
		
	/* Limited time for Pumping */
		void setLimitedTime();
		
	/* Clear database */ 
	    void clearData();
		
	/* check connect esp32 to wifi */  
		unsigned char Wifi();
		unsigned char toggle(unsigned char a);


#endif
/////////////////////////////////////////////////////////////////
  

/////////////////////////////////////////////////////////////////
void setup() 
{
	  // put your setup code here, to run once:
	  // set up baurate UART
	  Serial.begin(9600);


	
/* Config Wifi */

#if 0	  
	  /* connecting wifi */
	  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
	  Serial.print("Connecting to Wifi");
	  while(WiFi.status() != WL_CONNECTED){
		  Serial.print(".");
		  delay(300);
	  }
	  Serial.println();
	  Serial.print("Connected with IP: ");
	  Serial.println(WiFi.localIP());
	  Serial.println();
#endif

	

#if 0 /* Smart Config Wifi */	    
	  WiFi.mode(WIFI_AP_STA);
	  /* start SmartConfig */
	  WiFi.beginSmartConfig();
	  /* Wait for SmartConfig packet from mobile */
	  Serial.println("Waiting for SmartConfig.");
      while (!WiFi.smartConfigDone()) {
         delay(500);
         Serial.print(".");
      }
      Serial.println("");
      Serial.println("SmartConfig done.");
 
      /* Wait for WiFi to connect to AP */
      Serial.println("Waiting for WiFi");
      while (WiFi.status() != WL_CONNECTED) {
         delay(500);
         Serial.print(".");
      }
      Serial.println("WiFi Connected.");
      Serial.print("IP Address: ");
      Serial.println(WiFi.localIP());
#endif
	  
	xTaskCreatePinnedToCore(
                    connectWifi, /* Task function. */
                    "Task_wifi",     /* name of task. */
                    100000,       /* Stack size of task */
                    NULL,        /* parameter of the task */
                    0,           /* priority of the task */
                    &Task_wifi,      /* Task handle to keep track of created task */
                    0);         /* pin task to core 0 */                  
	delay(500);
	
	//disableCore0WDT();

	 
	  
	  /* set interupt timer */
	  /*timer = timerBegin(0, 80, true);
	  timerAttachInterrupt(timer, &onTimer, true);
	  timerAlarmWrite(timer, 100000, true); //10ms
	  timerAlarmEnable(timer);*/
	  
	  
	  /* Config GPIO */
	  // define Pump  = GPIO_NUM_23  control Relay
	  pinMode(GPIO_NUM_23, OUTPUT);
	  
	  
}
/////////////////////////////////////////////////////////////////


/* Connecting wifi */
// Task core 0
void connectWifi(void *pvParameters)
{
	/* connecting wifi */
	WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
	//Serial.print("Connecting to Wifi");
    while(WiFi.status() != WL_CONNECTED){
		//Serial.print(".");
		delay(300);
	}
	//Serial.println();
	//Serial.print("Connected with IP: ");
	//Serial.println(WiFi.localIP());
	//Serial.println();
	
	/* connecting Firebase */
	Firebase.begin(FIREBASE_HOST,FIREBASE_AUTH);
	  
	Firebase.setReadTimeout(database,1000*60);
	Firebase.setwriteSizeLimit(database,"tiny");
	  
	Firebase.setReadTimeout(toggleWifi,1000*60);
	Firebase.setwriteSizeLimit(toggleWifi,"tiny");
	
	/* Time NTPClient */
	timeClient.begin();
	timeClient.setTimeOffset(+7*60*60);
	
	while(1){
		delay(1000);
		getTimeHTTP();
		
	}
}
/////////////////////////////////////////////////////////////////


 /* Loop main */ 
void loop()
{
	//loop 10ms
	//while(glFlagTimer10ms == 0);
	//glFlagTimer10ms = 0;
	//Serial.println(glFlagTimer10ms);
	  
  	operate();
	
	
	/* Reconnecting Wifi */
	if(WiFi.status() != WL_CONNECTED){
		WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
			
		/* Loop 5s wait reconnecting Wifi */
		currentConnectWifi = millis();
		previousConnectWifi = currentConnectWifi;
		while((currentConnectWifi - previousConnectWifi < connectWifiInterval)&&(WiFi.status() != WL_CONNECTED)){
			currentConnectWifi = millis();
		}	
	}
	
	
	/* Loop 2s */
	currentMillis  = millis();
	if(currentMillis - previousMillis >= interval && Wifi() == 1 ){
		previousMillis = currentMillis;
		
		// read data from Firebase
		readDataFireBase();
		glReadFireBaseFlag = 1;        //glReadFireBaseFlag = 1 just here
		
	}
	
	/* query Database */
	if(glReadFireBaseFlag == 1){
		/* query Data */
		queryData();
		
     	glUpLoadDataBase = 1;        //glUpLoadDataBase = 1 just here
		
		// Delete ReadFirebase Flag
		glReadFireBaseFlag = 0;
	}
	
}


void operate()
{
	/* Alarm Pump */
    if(statusAlarm){
		
		if(glLimitedTimeFlag == 1){
			statusAlarm = alarm(eHour,eMinute);
		}
		else{
			statusAlarm = alarm(endHour,endMinute);
		}
		
		if(statusAlarm == 0){
			// turn Off Led
			setPowerPump(pumpOff);
			statusPump = 0;
			glLimitedTimeFlag = 0;
			
			Serial.println("finnish alarm");
	
			// Second Update 
			glResetTimerFlag = 1;
		}
		
		// continute alarm-------->
    }
	
	// Update data Firebase
	if(glUpLoadDataBase == 1){
		
		/* Toggle logic wifi */
		statusWifi = toggle(statusWifi);

		/* Update data -> Firebase */ 
		writeDataFirebase();
		
		
		// clear flag 
		glUpLoadDataBase = 0;
	}
	
	
}
////////////////////////////////////////////////////////////////



////////////////////////////////////////////////////////////////
void getTimeHTTP()
{	
		while(!timeClient.update()) {
			timeClient.forceUpdate();
		}
		
		formattedDate = timeClient.getFormattedDate();
		
		splitT = formattedDate.indexOf("T");
	    split1 = formattedDate.indexOf(":");
		
		
		String dayStamp = formattedDate.substring(0, splitT);
		String hour = formattedDate.substring(splitT+1,split1);
		String minute = formattedDate.substring(split1+1, formattedDate.length()-4);
			
		/* get real time Web */
		
		currentTime.Hours = hour.toInt();
		currentTime.Minutes = minute.toInt();
		
		

}
////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////
/* write and read Data firebase */
unsigned char readDataFireBase()
{
	  //unsigned char flag;
	 
	 
	  /* Download database */
	  
	  if(Firebase.getInt(database,"/dataApptoMCU/Pump") != true){
		goto END;
	  }
	  Pump = database.to<int>();
	  
	  if(Firebase.getInt(database,"/dataApptoMCU/alarmPump") != true){
		goto END;
	  }
	  alarmPump = database.to<int>();
	  
	  if(Firebase.getInt(database,"/dataApptoMCU/startHour") != true){
		goto END;
	  }
	  startHour = database.to<int>();
	  
	  if(Firebase.getInt(database,"/dataApptoMCU/startMinute") != true){
		goto END;
	  }
	  startMinute = database.to<int>();
	  
	  if(Firebase.getInt(database,"/dataApptoMCU/endHour") != true){
		goto END;
	  }
	  endHour = database.to<int>();
	  
	  if(Firebase.getInt(database,"/dataApptoMCU/endMinute") != true){
		goto END;
	  }
	  endMinute = database.to<int>();
	  
	  return 1;
  
END: return 0;    
}

void writeDataFirebase()
{
	
	/* Upload data firebase */ 
	Firebase.setInt(database, "/dataMCUtoApp/statusPump",statusPump);

    /* Update Status Wifi */
	Firebase.setInt(database, "dataMCUtoApp/statusWifi",statusWifi);
	
	
	/* Reset Timer ALarm Pump*/
	if(glResetTimerFlag == 1 ){
		Firebase.setInt(database, "/dataApptoMCU/Pump",0);
		Firebase.setInt(database, "/dataApptoMCU/alarmPump",0);
		Firebase.setInt(database, "/dataApptoMCU/startHour",0);
		Firebase.setInt(database, "/dataApptoMCU/startMinute",0);
		Firebase.setInt(database, "/dataApptoMCU/endHour",0);
		Firebase.setInt(database, "/dataApptoMCU/endMinute",0);
		
		glResetTimerFlag = 0;
		
	}

	     
}
////////////////////////////////////////////////////////////////



////////////////////////////////////////////////////////////////
/* set Power Pump */
void setPowerPump(unsigned char mode)
{
	digitalWrite(GPIO_NUM_23,mode);
}
////////////////////////////////////////////////////////////////




///////////////////////////////////////////////////////////////
/* Check Data */
void queryData()
{
	/* off Pump */
	if(Pump == 0 && alarmPump == 0){
		setPowerPump(pumpOff);
		statusPump = 0;
		statusAlarm = 0;
		glResetTimerFlag = 0;
    }
	
	/* alarm */
	else if(Pump == 0 && alarmPump == 1 && statusAlarm == 0){
		//getTimeHTTP();
		if(startHour == currentTime.Hours){
			if(((startMinute - currentTime.Minutes)<10)||((currentTime.Minutes - startMinute))<10){
				setPowerPump(pumpOn);
				statusPump = 1;
				glResetTimerFlag = 0;

				// Start alarm
				setTime(startHour,startMinute);
				statusAlarm = 1;
			}
			else{
				statusPump = 0;
				glResetTimerFlag = 1;
			}
			
		}
		else if(((startHour - currentTime.Hours)>=1)||((currentTime.Hours - startHour))>=1){
			statusPump = 0;
			glResetTimerFlag = 1;
		}
		else if(((startHour - currentTime.Hours)<1)||((currentTime.Hours - startHour))<1){
			if((startMinute == 59)||(currentTime.Minutes == 59)){
				setPowerPump(pumpOn);
				statusPump = 1;
				glResetTimerFlag = 0;

				// Start alarm
				setTime(startHour,startMinute);
				statusAlarm = 1;
			}
		}
		
	}
	
	/* free turn on pump */
	else if(Pump == 1 && alarmPump == 0 && statusAlarm == 0){
		setPowerPump(pumpOn);
		statusPump = 1;
		glResetTimerFlag = 0;
		
		// Limit time Pump = 20 minute 
		setLimitedTime();
		setTime(sHour,sMinute);
		statusAlarm = 1;
		glLimitedTimeFlag = 1;
	}
	
	/* alarm Pump and free Pump */
	else if(Pump == 1 && alarmPump == 1 && statusAlarm == 0){
		setPowerPump(pumpOn);
		statusPump = 1;
		glResetTimerFlag = 0;

		// Start alarm
		setTime(startHour,startMinute);
		statusAlarm = 1;
	}
		
}
////////////////////////////////////////////////////////////



////////////////////////////////////////////////////////////
/* Alarm Pump */
void setTime(unsigned char hour, unsigned char minute)
{
	rtc.setTime(0, minute, hour, 8, 11, 2001);
}


void getTime()
{
	// get time now 
	timeNow.Hours = rtc.getHour((true));
	timeNow.Minutes = rtc.getMinute();
	
}


unsigned char alarm(unsigned char hour, unsigned char minute)
{
	unsigned char flag;

	//Read Time from RTC
	getTime(); 

	if(timeNow.Hours == hour){
		if(timeNow.Minutes < minute){
			flag = 1;     // continnue alarm
			Serial.print(timeNow.Hours);
			Serial.println(timeNow.Minutes);
		}
		else{
			flag = 0;     // stop alarm
		}
	}
	else{
		flag = 1;         // continnue alarm
	}
	return flag;
}
////////////////////////////////////////////////////////////


////////////////////////////////////////////////////////////
/* Limited time for Pumping */
void setLimitedTime()
{
	
	sHour = currentTime.Hours;
	sMinute = currentTime.Minutes;
	
	if((sMinute + timePlay) < 60){
		eMinute = sMinute + timePlay;
        eHour = sHour;
    } 
	else{
        eMinute = (sMinute + timePlay) - 60;
        eHour = sHour + 1;
    }

}

////////////////////////////////////////////////////////////


/* clear buffer database */
void clearData()
{
	
	Pump = 0;
	alarmPump = 0;
	startHour = 0;
	startMinute = 0;
	endHour = 0;
	endMinute = 0;
	
}


/* check connect wifi */
unsigned char Wifi()
{
	unsigned char flag;
	
	if(WiFi.status() != WL_CONNECTED){
		flag = 0;
	}
	else{
		flag = 1;
	}
	
	return flag;
}

unsigned char toggle(unsigned char a)
{
	if(a == 0){
		a = 1;
	}
	else{
		a = 0;
	}
	return a;
}
