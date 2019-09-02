# educational-visual-programming-editor-for-nodemcu
Educational visual programming editor for NodeMCU

## Frontend

Double click on index.html
Ready!

## Backend

Requirements
* OpenJDK 11
* Maven 3.5.3+

Run
* ./mvnw compile quarkus:dev

Getting Started
https://quarkus.io/guides/getting-started-guide

## Firmware

### Serial

Requirements
* PlatformIO CLI
* Set upload_port on platformio.ini

Run
* platformio run -t upload

### OTA

Requirements
* PlatformIO CLI
* Set SSL fingerprint of web storage (AWS S3) on main.cpp
* Prepare device via serial upload

Run
* Build firmware bin: platformio run
* Upload the bin output (./.pioenvs/serial/firmware.bin) to web storage (AWS S3)