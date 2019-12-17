
#include <Otto9.h>
Otto9 Otto;  //This is Otto!

//----------------------------------------------------------------------
//-- Make sure the servos are in the right pin
/*                --------
*               |  O  O  |
*               |--------|
*   RIGHT LEG 3 |        | LEFT LEG 2
*                --------
*                ||     ||
* RIGHT FOOT 5 |---     ---| LEFT FOOT 4
*/

#define PIN_LEFTLEG D2
#define PIN_RIGHTLEG D3
#define PIN_LEFTFOOT D0
#define PIN_RIGHTFOOT D1
#define PIN_NOISE_SENSOR 1 //TX - NOT USED
#define PIN_BUZZER 3
#define PIN_USTRIGGER D4
#define PIN_USECHO D5

/*SOUNDS******************
* S_connection  S_disconnection  S_buttonPushed S_mode1 S_mode2 S_mode3 S_surprise S_OhOoh  S_OhOoh2  S_cuddly
* S_sleeping  S_happy S_superHappy S_happy_short S_sad S_confused S_fart1 S_fart2  S_fart3
*/

/*MOVEMENTS LIST**************
* dir=1---> FORWARD/LEFT
* dir=-1---> BACKWARD/RIGTH
* T : amount of movement. HIGHER VALUE SLOWER MOVEMENT usually 1000 (from 600 to 1400)
* h: height of mov. around 20
*    jump(steps=1, int T = 2000);
*    walk(steps, T, dir);
*    turn(steps, T, dir);
*    bend (steps, T, dir); //usually steps =1, T=2000
*    shakeLeg (steps, T, dir);
*    updown(steps, T, HEIGHT);
*    swing(steps, T, HEIGHT);
*    tiptoeSwing(steps, T, HEIGHT);
*    jitter(steps, T, HEIGHT); (small T)
*    ascendingTurn(steps, T, HEIGHT);
*    moonwalker(steps, T, HEIGHT,dir);
*    crusaito(steps, T, HEIGHT,dir);
*    flapping(steps, T, HEIGHT,dir);
*/

/*GESTURES LIST***************
* OttoHappy OttoSuperHappy  OttoSad   OttoSleeping  OttoFart  OttoConfused OttoLove  OttoAngry
* OttoFretful OttoMagic  OttoWave  OttoVictory  OttoFail
*/

//DO NOT REMOVE!!!
#include <ESP8266WiFi.h>
#include <ESP8266WiFiMulti.h>
//---OTA--------------------------------------------------
#include <ESP8266httpUpdate.h>
//--------------------------------------------------------
//---WiFi Manager-----------------------------------------
#include <DNSServer.h>
#include <ESP8266WebServer.h>
#include <WiFiManager.h>
//--------------------------------------------------------

//Enter values in secrets.h
#include "secrets.h"

#define VERSION 1576024268 //Unix Timestamp

//@@REPLACE_DEFINE_WIFIMANAGER@@ //Use WiFi Manager or fixed SSID and password

WiFiManager wifiManager;

WiFiClientSecure net;
ESP8266WiFiMulti WiFiMulti;

void checkNewFirmwareAndUpdateIfNeeded();
void updateFirmware();

///////////////////////////////////////////////////////////////////
//-- Setup ------------------------------------------------------//
///////////////////////////////////////////////////////////////////
void setup() {
	//DO NOT REMOVE!!!
	 checkNewFirmwareAndUpdateIfNeeded();

	
	Otto.init(PIN_LEFTLEG,PIN_RIGHTLEG,PIN_LEFTFOOT,PIN_RIGHTFOOT,true,PIN_NOISE_SENSOR,PIN_BUZZER,PIN_USTRIGGER,PIN_USECHO); //Set the servo pins
	Otto.home(); //Otto at rest position
	Otto.putMouth(thunder, true);
	Otto.sing(S_connection);
	delay(50);
	
	//DO NOT REMOVE!!!
	Serial.begin(115200);
  delay(5000);
  Serial.println();
  Serial.println();

  wifiManager.autoConnect("Otto FURB");

}

///////////////////////////////////////////////////////////////////
//-- Principal Loop ---------------------------------------------//
///////////////////////////////////////////////////////////////////
void loop() {
	//DO NOT REMOVE!!!
	checkNewFirmwareAndUpdateIfNeeded();

}

//DO NOT REMOVE!!!
void checkNewFirmwareAndUpdateIfNeeded()
{
  String fwURL = String(S3_BUCKET);
  fwURL.concat("version.txt");
  Serial.println(fwURL);

  HTTPClient httpClient;
  httpClient.begin(fwURL, S3_FINGETPRINT);
  int httpCode = httpClient.GET();
  if (httpCode == 200)
  {
  String newFWVersion = httpClient.getString();

  Serial.print("Current firmware version: ");
  Serial.println(VERSION);
  Serial.print("Available firmware version: ");
  Serial.println(newFWVersion);

  int newVersion = newFWVersion.toInt();

  if (newVersion > VERSION)
  {
      updateFirmware();
    }
    }
    else
    {
      Serial.print("Firmware version check failed, got HTTP response code ");
      Serial.println(httpCode);
    }

    httpClient.end();
  }

  void updateFirmware()
  {
  Serial.println();
  Serial.println("Preparing to update firmware...");

  String fwURL = String(S3_BUCKET);
  fwURL.concat("firmware.bin");
  Serial.println(fwURL);

  Serial.println("Updating...");
  t_httpUpdate_return ret = ESPhttpUpdate.update(fwURL, "", S3_FINGETPRINT);
  switch (ret)
  {
  case HTTP_UPDATE_FAILED:
    Serial.printf("HTTP_UPDATE_FAILD Error (%d): %s", ESPhttpUpdate.getLastError(), ESPhttpUpdate.getLastErrorString().c_str());
    break;

  case HTTP_UPDATE_NO_UPDATES:
    Serial.println("HTTP_UPDATE_NO_UPDATES");
    break;

  case HTTP_UPDATE_OK:
    Serial.println("HTTP_UPDATE_OK");
    break;
  }
}


