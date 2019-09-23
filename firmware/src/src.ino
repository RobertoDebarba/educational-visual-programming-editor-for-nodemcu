#include <ESP8266WiFi.h>
#include <WiFiClientSecure.h>
#include <PubSubClient.h>
#include <ArduinoJson.h>
#include <time.h>

//Enter values in secrets.h
#include "secrets.h"

const int MQTT_PORT = 8883;
char *MQTT_TOPIC_GET = "$aws/things/" THINGNAME "/shadow/get";
char *MQTT_TOPIC_GET_ACCEPTED = "$aws/things/" THINGNAME "/shadow/get/accepted";
char *MQTT_TOPIC_UPDATE = "$aws/things/" THINGNAME "/shadow/update";
char *MQTT_TOPIC_UPDATE_ACCEPTED = "$aws/things/" THINGNAME "/shadow/update/accepted";

#ifdef USE_SUMMER_TIME_DST
uint8_t DST = 1;
#else
uint8_t DST = 0;
#endif

WiFiClientSecure net;

BearSSL::X509List cert(cacert);
BearSSL::X509List client_crt(client_cert);
BearSSL::PrivateKey key(privkey);

PubSubClient pubSubCLient(net);

unsigned long lastMillis = 0;
time_t now;
time_t nowish = 1510592825;

unsigned long previousMillis = 0;
const long interval = 5000;

void NTPConnect();
void messageReceived(char *topic, byte *payload, unsigned int length);
void pubSubErr(int8_t MQTTErr);
void connectToMqtt();
void connectToWiFi(String init_str);
void checkWiFiThenMQTT();
void sendData(JsonObject message, char *topic);

void getVersion();
void onVersion();
void onNewVersion();

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

  pubSubCLient.setServer(MQTT_HOST, MQTT_PORT);
  pubSubCLient.setCallback(messageReceived);

  connectToMqtt();
}

void loop()
{
  now = time(nullptr);
  if (!pubSubCLient.connected())
  {
    checkWiFiThenMQTT();
  }
  else
  {
    pubSubCLient.loop();
    getVersion();
    delay(3000);
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
  while (!pubSubCLient.connected())
  {
    if (pubSubCLient.connect(THINGNAME))
    {
      Serial.println("connected!");
      if (!pubSubCLient.subscribe(MQTT_TOPIC_GET_ACCEPTED))
        pubSubErr(pubSubCLient.state());
      // if (!pubSubCLient.subscribe(MQTT_TOPIC_UPDATE_ACCEPTED))
      //   pubSubErr(pubSubCLient.state());
    }
    else
    {
      Serial.print("failed, reason -> ");
      pubSubErr(pubSubCLient.state());
      Serial.println(" < try again in 5 seconds");
      delay(5000);
    }
  }
}

void connectToWiFi(String init_str)
{
  if (init_str != String())
    Serial.print(init_str);
  while (WiFi.status() != WL_CONNECTED)
  {
    Serial.print(".");
    delay(1000);
  }
  if (init_str != String())
    Serial.println("ok!");
}

void checkWiFiThenMQTT()
{
  connectToWiFi("Checking WiFi");
  connectToMqtt();
}

void sendData(JsonObject messageRoot, char *topic)
{
  serializeJson(messageRoot, Serial);
  Serial.println();
  char shadow[measureJson(messageRoot) + 1];
  serializeJson(messageRoot, shadow, sizeof(shadow));

  Serial.println("Mandando");

  if (!pubSubCLient.publish(topic, shadow, false))
    pubSubErr(pubSubCLient.state());
}

void messageReceived(char *topic, byte *payload, unsigned int length)
{
  Serial.println("Recebeu");

  String topicStr = String(topic);
  String payloadStr = String((char *)payload);

  Serial.println("Received [" + topicStr + "]");
  Serial.println("Payload: " + payloadStr);
  Serial.println();

  if (topicStr == MQTT_TOPIC_GET_ACCEPTED)
  {
    DynamicJsonDocument doc(1024);
    deserializeJson(doc, payloadStr);
    JsonObject obj = doc.as<JsonObject>();
    JsonObject state = obj["state"];
    JsonObject delta = obj["delta"];
    int version = delta["version"].as<int>();

    onNewVersion(version);
  }
}

void getVersion()
{
  DynamicJsonDocument jsonBuffer(JSON_OBJECT_SIZE(3) + 100);
  JsonObject root = jsonBuffer.to<JsonObject>();
  sendData(root, MQTT_TOPIC_GET);
  //Receive shadow on "$aws/things/otto/shadow/get/accepted"
}

void onNewVersion(int version)
{
  Serial.println("Version=" + version);
  //TODO update firmware

  // Update State
  DynamicJsonDocument jsonBuffer(JSON_OBJECT_SIZE(3) + 100);
  JsonObject root = jsonBuffer.to<JsonObject>();
  JsonObject state = root.createNestedObject("state");
  JsonObject reported = state.createNestedObject("reported");
  JsonObject firmware = reported.createNestedObject("firmware");
  firmware["value"] = version;

  sendData(root, MQTT_TOPIC_UPDATE);
}