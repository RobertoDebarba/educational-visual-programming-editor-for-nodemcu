#include <ESP8266HTTPClient.h>
#include <ESP8266httpUpdate.h>

#include <ESP8266WiFi.h>
#include <ESP8266WiFiMulti.h>

ESP8266WiFiMulti WiFiMulti;

const int FW_VERSION = 0; //Unix Timestamp

const char *fwUrlBase = "https://vpl-esp8266.s3.amazonaws.com/";
const char *fingerprint = "17:E0:A9:3E:58:AF:0A:06:8D:6C:2D:B6:C1:80:B3:E7:E3:52:D4:8E";

void checkForUpdates()
{
  String fwURL = String(fwUrlBase);
  fwURL.concat("firmware");
  String fwVersionURL = String(fwUrlBase);
  fwVersionURL.concat("version.txt");

  Serial.println("Checking for firmware updates...");
  Serial.print("Firmware version URL: ");
  Serial.println(fwVersionURL);

  HTTPClient httpClient;
  httpClient.begin(fwVersionURL, fingerprint);
  int httpCode = httpClient.GET();
  if (httpCode == 200)
  {
    String newFWVersion = httpClient.getString();

    Serial.print("Current firmware version: ");
    Serial.println(FW_VERSION);
    Serial.print("Available firmware version: ");
    Serial.println(newFWVersion);

    int newVersion = newFWVersion.toInt();

    if (newVersion > FW_VERSION)
    {
      Serial.println("Preparing to update...");

      String fwImageURL = fwURL;
      fwImageURL.concat(".bin");
      t_httpUpdate_return ret = ESPhttpUpdate.update(fwImageURL, "", fingerprint);

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
  }
  else
  {
    Serial.print("Firmware version check failed, got HTTP response code ");
    Serial.println(httpCode);
  }

  httpClient.end();
}

void setup()
{
  Serial.begin(115200);

  Serial.println();
  Serial.println();
  Serial.println();

  for (uint8_t t = 4; t > 0; t--)
  {
    Serial.println("Seting up...");
    delay(1000);
  }

  WiFi.mode(WIFI_STA);
  WiFiMulti.addAP("Redmi", "qwert12345");
}

void loop()
{
  // wait for WiFi connection
  if ((WiFiMulti.run() == WL_CONNECTED))
  {
    checkForUpdates();
  }

  delay(5000);
}
