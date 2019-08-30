# educational-visual-programming-editor-for-nodemcu
Educational visual programming editor for NodeMCU

## Frontend

Double click on index.html
Ready!

## Backend

Requires
* OpenJDK 11
* Maven 3.5.3+

Run
* ./mvnw compile quarkus:dev

Getting Started
https://quarkus.io/guides/getting-started-guide

## OTA

Requires
* Create Firewall Outbound Rule for any protocol on port 8266
* PlatformIO CLI
* Set upload_port for serial and ota on platformio.ini
* Build and upload firmware via serial port: platformio run -e serial -t upload


Build and upload via OTA:
platformio run -e ota -t upload