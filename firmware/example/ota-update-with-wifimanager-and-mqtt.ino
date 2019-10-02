#include <ESP8266WiFi.h>
#include <ESP8266WiFiMulti.h>
//---OTA--------------------------------------------------
#include <ESP8266httpUpdate.h>
//--------------------------------------------------------
//---AWS IoT----------------------------------------------
#include <WiFiClientSecure.h>
#include <PubSubClient.h>
#include <ArduinoJson.h>
#include <time.h>
#define emptyString String()
//--------------------------------------------------------
//---WiFi Manager-----------------------------------------
#include <DNSServer.h>
#include <ESP8266WebServer.h>
#include <WiFiManager.h>
//--------------------------------------------------------

//Enter values in secrets.h
#include "secrets.h"

#define VERSION 1569544301 //Unix Timestamp

#define WIFIMANAGER true //Use WiFi Manager or fixed SSID and password

const int MQTT_PORT = 8883;
const char MQTT_TOPIC_GET_ACCEPTED[] = "$aws/things/" THINGNAME "/shadow/get/accepted";
const char MQTT_TOPIC_GET[] = "$aws/things/" THINGNAME "/shadow/get";
const char MQTT_TOPIC_UPDATE[] = "$aws/things/" THINGNAME "/shadow/update";
const char MQTT_TOPIC_UPDATE_DELTA[] = "$aws/things/" THINGNAME "/shadow/update/delta";

#ifdef USE_SUMMER_TIME_DST
uint8_t DST = 1;
#else
uint8_t DST = 0;
#endif

#ifdef WIFIMANAGER
WiFiManager wifiManager;
#endif

WiFiClientSecure net;
ESP8266WiFiMulti WiFiMulti;

BearSSL::X509List cert(cacert);
BearSSL::X509List client_crt(client_cert);
BearSSL::PrivateKey key(privkey);

PubSubClient client(net);

unsigned long lastMillis = 0;
time_t now;
time_t nowish = 1510592825;

void NTPConnect();
void connectToMqtt();
void checkNewFirmwareAndUpdateIfNeeded();
void messageReceived(char *topic, byte *payload, unsigned int length);

void sendShadowGet();
void sendShadowUpdate(int firmwareVersion);
void updateFirmware();

void setup()
{
  Serial.begin(115200);
  delay(5000);
  Serial.println();
  Serial.println();

#ifdef WIFIMANAGER
  wifiManager.autoConnect("Otto FURB");
#else
  Serial.print("Connecting to WiFi...");
  WiFi.mode(WIFI_STA);
  WiFiMulti.addAP(ssid, pass);

  while ((WiFiMulti.run() != WL_CONNECTED))
  {
    Serial.print(".");
    delay(5000);
  }
  Serial.println(" conected");
#endif

  NTPConnect();

  net.setTrustAnchors(&cert);
  net.setClientRSACert(&client_crt, &key);

  client.setServer(MQTT_HOST, MQTT_PORT);
  client.setCallback(messageReceived);

  connectToMqtt();

  sendShadowUpdate(VERSION);
  checkNewFirmwareAndUpdateIfNeeded();
}

void loop()
{
#ifdef WIFIMANAGER
  if (!client.connected())
  {
    connectToMqtt();
  }
  else
  {
    client.loop();
  }
#else
  if ((WiFiMulti.run() == WL_CONNECTED))
  {
    if (!client.connected())
    {
      connectToMqtt();
    }
    else
    {
      client.loop();
    }
  }
#endif

  delay(5000);
}

void NTPConnect()
{
  Serial.print("Setting time using SNTP");
  configTime(TIME_ZONE * 3600, DST * 3600, "pool.ntp.org", "time.nist.gov");
  now = time(nullptr);
  while (now < nowish)
  {
    delay(500);
    Serial.print(".");
    now = time(nullptr);
  }
  Serial.println("done!");
  struct tm timeinfo;
  gmtime_r(&now, &timeinfo);
  Serial.print("Current time: ");
  Serial.print(asctime(&timeinfo));
}

void pubSubErr(int8_t MQTTErr)
{
  if (MQTTErr == MQTT_CONNECTION_TIMEOUT)
    Serial.print("Connection tiemout");
  else if (MQTTErr == MQTT_CONNECTION_LOST)
    Serial.print("Connection lost");
  else if (MQTTErr == MQTT_CONNECT_FAILED)
    Serial.print("Connect failed");
  else if (MQTTErr == MQTT_DISCONNECTED)
    Serial.print("Disconnected");
  else if (MQTTErr == MQTT_CONNECTED)
    Serial.print("Connected");
  else if (MQTTErr == MQTT_CONNECT_BAD_PROTOCOL)
    Serial.print("Connect bad protocol");
  else if (MQTTErr == MQTT_CONNECT_BAD_CLIENT_ID)
    Serial.print("Connect bad Client-ID");
  else if (MQTTErr == MQTT_CONNECT_UNAVAILABLE)
    Serial.print("Connect unavailable");
  else if (MQTTErr == MQTT_CONNECT_BAD_CREDENTIALS)
    Serial.print("Connect bad credentials");
  else if (MQTTErr == MQTT_CONNECT_UNAUTHORIZED)
    Serial.print("Connect unauthorized");
}

void connectToMqtt()
{
  Serial.print("MQTT connecting ");
  while (!client.connected())
  {
    if (client.connect(THINGNAME))
    {
      Serial.println("connected!");
      if (!client.subscribe(MQTT_TOPIC_GET_ACCEPTED))
        pubSubErr(client.state());
      if (!client.subscribe(MQTT_TOPIC_UPDATE_DELTA))
        pubSubErr(client.state());
    }
    else
    {
      Serial.print("failed, reason -> ");
      pubSubErr(client.state());
      Serial.println(" < try again in 5 seconds");
      delay(5000);
    }
  }
}

void checkNewFirmwareAndUpdateIfNeeded()
{
  sendShadowGet();
}

void configureWifiForFirmwareUpdate()
{
  Serial.print("Reconnecting WiFi...");
  while (!WiFi.reconnect())
  {
    Serial.print(".");
    delay(5000);
  }
  Serial.println(" success");

  Serial.print("Validating certificate... ");
  if (net.verify(S3_FINGETPRINT, "s3.amazonaws.com"))
  {
    Serial.println("certificate matches");
  }
  else
  {
    Serial.println("certificate doesn't match");
    return;
  }

  net.setFingerprint(S3_FINGETPRINT);
}

void updateFirmware()
{
  Serial.println();
  Serial.println("Preparing to update firmware...");

  String fwURL = String(S3_BUCKET);
  fwURL.concat("firmware.bin");
  Serial.println(fwURL);

  configureWifiForFirmwareUpdate();

  Serial.println("Updating...");
  t_httpUpdate_return ret = ESPhttpUpdate.update(net, fwURL, "");
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

void sendData(JsonObject root, const char topic[])
{
  Serial.printf("--> Sending  [%s] ", topic);

  serializeJson(root, Serial);
  char shadow[measureJson(root) + 1];
  serializeJson(root, shadow, sizeof(shadow));
  if (!client.publish(topic, shadow, false))
    pubSubErr(client.state());

  Serial.println();
}

void sendShadowGet()
{
  DynamicJsonDocument jsonBuffer(JSON_OBJECT_SIZE(3) + 100);
  JsonObject root = jsonBuffer.to<JsonObject>();

  sendData(root, MQTT_TOPIC_GET);
}

void sendShadowUpdate(int firmwareVersion)
{
  DynamicJsonDocument jsonBuffer(JSON_OBJECT_SIZE(3) + 100);
  JsonObject root = jsonBuffer.to<JsonObject>();
  JsonObject state = root.createNestedObject("state");
  JsonObject state_reported = state.createNestedObject("reported");
  state_reported["firmware"] = firmwareVersion;

  sendData(root, MQTT_TOPIC_UPDATE);
}

void messageReceived(char *topic, byte *payload, unsigned int length)
{
  String topicStr = String(topic);
  Serial.print("--> Received [" + topicStr + "] ");

  String payloadStr = String((char *)payload);
  Serial.println(payloadStr);

  if (topicStr == MQTT_TOPIC_GET_ACCEPTED)
  {
    DynamicJsonDocument doc(2048);
    deserializeJson(doc, payloadStr);
    JsonObject obj = doc.as<JsonObject>();
    JsonObject state = obj["state"];
    JsonObject delta = state["delta"];
    int version = delta["firmware"].as<int>();

    if (version != 0)
    {
      Serial.println("New firmware version: " + String(version));
      updateFirmware();
    }
  }
  else if (topicStr == MQTT_TOPIC_UPDATE_DELTA)
  {
    DynamicJsonDocument doc(2048);
    deserializeJson(doc, payloadStr);
    JsonObject obj = doc.as<JsonObject>();
    JsonObject state = obj["state"];
    int version = state["firmware"].as<int>();

    if (version != 0)
    {
      Serial.println("New firmware version: " + String(version));
      updateFirmware();
    }
  }
}
