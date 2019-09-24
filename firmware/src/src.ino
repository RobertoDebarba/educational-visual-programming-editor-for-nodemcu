#include <ESP8266WiFi.h>
#include <WiFiClientSecure.h>
#include <PubSubClient.h>
#include <ArduinoJson.h>
#include <time.h>
#define emptyString String()

//Enter values in secrets.h
#include "secrets.h"

const int MQTT_PORT = 8883;
const char MQTT_TOPIC_GET_ACCEPTED[] = "$aws/things/" THINGNAME "/shadow/get/accepted";
const char MQTT_TOPIC_GET[] = "$aws/things/" THINGNAME "/shadow/get";
const char MQTT_TOPIC_UPDATE[] = "$aws/things/" THINGNAME "/shadow/update";

#ifdef USE_SUMMER_TIME_DST
uint8_t DST = 1;
#else
uint8_t DST = 0;
#endif

WiFiClientSecure net;

BearSSL::X509List cert(cacert);
BearSSL::X509List client_crt(client_cert);
BearSSL::PrivateKey key(privkey);

PubSubClient client(net);

unsigned long lastMillis = 0;
time_t now;
time_t nowish = 1510592825;

void NTPConnect();
void pubSubErr(int8_t MQTTErr);
void connectToMqtt();
void connectToWiFi(String init_str);
void checkWiFiThenMQTT();
void sendData(JsonObject root, char topic[]);
void checkNewFirmwareAndUpdateIfNeeded();
void sendShadowGet();
void sendShadowUpdate();
void messageReceived(char *topic, byte *payload, unsigned int length);

void setup()
{
  Serial.begin(115200);
  delay(5000);
  Serial.println();
  Serial.println();
  WiFi.hostname(THINGNAME);
  WiFi.mode(WIFI_STA);
  WiFi.begin(ssid, pass);
  connectToWiFi(String("Attempting to connect to SSID: ") + String(ssid));

  NTPConnect();

  net.setTrustAnchors(&cert);
  net.setClientRSACert(&client_crt, &key);

  client.setServer(MQTT_HOST, MQTT_PORT);
  client.setCallback(messageReceived);

  connectToMqtt();

  checkNewFirmwareAndUpdateIfNeeded();
}

void loop()
{
  if (!client.connected())
  {
    checkWiFiThenMQTT();
  }
  else
  {
    client.loop();
  }
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

void connectToWiFi(String init_str)
{
  if (init_str != emptyString)
    Serial.print(init_str);
  while (WiFi.status() != WL_CONNECTED)
  {
    Serial.print(".");
    delay(1000);
  }
  if (init_str != emptyString)
    Serial.println("ok!");
}

void checkWiFiThenMQTT()
{
  connectToWiFi("Checking WiFi");
  connectToMqtt();
}

void checkNewFirmwareAndUpdateIfNeeded()
{
  sendShadowGet();
}

void sendData(JsonObject root, const char topic[])
{
  Serial.printf("Sending  [%s]: ", topic);

  serializeJson(root, Serial);
  char shadow[measureJson(root) + 1];
  serializeJson(root, shadow, sizeof(shadow));
  if (!client.publish(topic, shadow, false))
    pubSubErr(client.state());
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
  Serial.println("Received [" + topicStr + "]");

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
      //TODO update firmware
      Serial.println("Update firmware");
      sendShadowUpdate(version);
    }
  }
}
