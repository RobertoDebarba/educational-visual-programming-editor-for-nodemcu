# educational-visual-programming-editor-for-nodemcu
Educational visual programming editor for NodeMCU

## Frontend

* Double click on index.html
* Ready!

## Backend

Requirements
* OpenJDK 11
* Maven 3.5.3+
* PlatformIO CLI
* Set enviroment variables: 
    * AWS_REGION=us-east-1
    * AWS_ACCESS_KEY_ID=?
    * AWS_SECRET_ACCESS_KEY=?
* Set on application.properties:
    * aws.s3.bucketname
    * aws.iot.clientendpoint 
    * aws.iot.thingname
* Copy AWS IoT Thing certificates to:
    * resources/br/com/robertodebarba/aws/aws-iot-certificate.pem.crt
    * resources/br/com/robertodebarba/aws/aws-iot-private.pem.key
* Copy firmware folder to ~/educational-visual-programming-language-for-esp8266/source

Run
* ./mvnw compile quarkus:dev

Getting Started
https://quarkus.io/guides/getting-started-guide

## Firmware

### Dependencies

Some libraries in lib folder were copy from Otto DIY project because it was not versioned =(
I created a patch with the changes I made to run on Esp8266: ./firmware/lib/ottodiylibs.patch

### Run

Requirements
* PlatformIO CLI
* Set on platformio.ini:
    * upload_port 
* Set on secrets.h:
    * THINGNAME
    * TIME_ZONE
    * USE_SUMMER_TIME_DST
    * MQTT_HOST
    * S3_BUCKET
    * S3_FINGETPRINT
    * cacert
    * client_cert
    * privkey

Run
* platformio run -t upload
