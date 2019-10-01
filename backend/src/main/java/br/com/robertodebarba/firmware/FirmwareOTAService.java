package br.com.robertodebarba.firmware;

import javax.enterprise.context.ApplicationScoped;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Injeta no código recebido as instruções necessárias para:
 * <ul>
 *     <li>Configuração de WiFi</li>
 *     <li>Atualização via OTA</li>
 *     <li>Conexão MQTT via AWS IoT</li>
 *     <li>Versão do firmware</li>
 * </ul>
 */
@ApplicationScoped
class FirmwareOTAService {

    private static final String REPLACE_GLOBAL_INIT = "//@@REPLACE_GLOBAL_INIT@@";
    private static final String REPLACE_SETUP = "//@@REPLACE_SETUP@@";
    private static final String REPLACE_LOOP = "//@@REPLACE_LOOP@@";
    private static final String REPLACE_GLOBAL_FUNCTIONS = "//@@REPLACE_GLOBAL_FUNCTIONS@@";
    private static final String REPLACE_FIRMWARE_VERSION = "//@@REPLACE_FIRMWARE_VERSION@@";
    private static final String REPLACE_DEFINE_WIFIMANAGER = "//@@REPLACE_DEFINE_WIFIMANAGER@@";
    private static final String REPLACE_WIFI_SSID = "@@REPLACE_WIFI_SSID@@";
    private static final String REPLACE_WIFI_PASSWORD = "@@REPLACE_WIFI_PASSWORD@@";

    private static final String REGEX_CONFI_WIFI_BLOCK = "//@@REPLACE_WIFI=(.*);(.*)@@";

    private static final String GLOBAL_INIT_CODE = "#include <ESP8266WiFi.h>\n" +
            "#include <ESP8266WiFiMulti.h>\n" +
            "//---OTA--------------------------------------------------\n" +
            "#include <ESP8266httpUpdate.h>\n" +
            "//--------------------------------------------------------\n" +
            "//---AWS IoT----------------------------------------------\n" +
            "#include <WiFiClientSecure.h>\n" +
            "#include <PubSubClient.h>\n" +
            "#include <ArduinoJson.h>\n" +
            "#include <time.h>\n" +
            "#define emptyString String()\n" +
            "//--------------------------------------------------------\n" +
            "//---WiFi Manager-----------------------------------------\n" +
            "#include <DNSServer.h>\n" +
            "#include <ESP8266WebServer.h>\n" +
            "#include <WiFiManager.h>\n" +
            "//--------------------------------------------------------\n" +
            "\n" +
            "//Enter values in secrets.h\n" +
            "#include \"secrets.h\"\n" +
            "\n" +
            "#define VERSION " + REPLACE_FIRMWARE_VERSION + " //Unix Timestamp\n" +
            "\n" +
            "@@REPLACE_DEFINE_WIFIMANAGER@@ //Use WiFi Manager or fixed SSID and password\n" +
            "\n" +
            "const int MQTT_PORT = 8883;\n" +
            "const char MQTT_TOPIC_GET_ACCEPTED[] = \"$aws/things/\" THINGNAME \"/shadow/get/accepted\";\n" +
            "const char MQTT_TOPIC_GET[] = \"$aws/things/\" THINGNAME \"/shadow/get\";\n" +
            "const char MQTT_TOPIC_UPDATE[] = \"$aws/things/\" THINGNAME \"/shadow/update\";\n" +
            "const char MQTT_TOPIC_UPDATE_DELTA[] = \"$aws/things/\" THINGNAME \"/shadow/update/delta\";\n" +
            "\n" +
            "#ifdef USE_SUMMER_TIME_DST\n" +
            "uint8_t DST = 1;\n" +
            "#else\n" +
            "uint8_t DST = 0;\n" +
            "#endif\n" +
            "\n" +
            "#ifdef WIFIMANAGER\n" +
            "WiFiManager wifiManager;\n" +
            "#endif\n" +
            "\n" +
            "WiFiClientSecure net;\n" +
            "ESP8266WiFiMulti WiFiMulti;\n" +
            "\n" +
            "BearSSL::X509List cert(cacert);\n" +
            "BearSSL::X509List client_crt(client_cert);\n" +
            "BearSSL::PrivateKey key(privkey);\n" +
            "\n" +
            "PubSubClient client(net);\n" +
            "\n" +
            "unsigned long lastMillis = 0;\n" +
            "time_t now;\n" +
            "time_t nowish = 1510592825;\n" +
            "\n" +
            "void NTPConnect();\n" +
            "void connectToMqtt();\n" +
            "void checkNewFirmwareAndUpdateIfNeeded();\n" +
            "void messageReceived(char *topic, byte *payload, unsigned int length);\n" +
            "\n" +
            "void updateFirmware();";

    private static final String SETUP_CODE = "Serial.begin(115200);\n" +
            "  delay(5000);\n" +
            "  Serial.println();\n" +
            "  Serial.println();\n" +
            "\n" +
            "#ifdef WIFIMANAGER\n" +
            "  wifiManager.autoConnect(\"Otto FURB\");\n" +
            "#else\n" +
            "  Serial.print(\"Connecting to WiFi...\");\n" +
            "  WiFi.mode(WIFI_STA);\n" +
            "  WiFiMulti.addAP(@@REPLACE_WIFI_SSID@@, @@REPLACE_WIFI_PASSWORD@@);\n" +
            "\n" +
            "  while ((WiFiMulti.run() != WL_CONNECTED))\n" +
            "  {\n" +
            "    Serial.print(\".\");\n" +
            "    delay(5000);\n" +
            "  }\n" +
            "  Serial.println(\" conected\");\n" +
            "#endif\n" +
            "\n" +
            "  NTPConnect();\n" +
            "\n" +
            "  net.setTrustAnchors(&cert);\n" +
            "  net.setClientRSACert(&client_crt, &key);\n" +
            "\n" +
            "  client.setServer(MQTT_HOST, MQTT_PORT);\n" +
            "  client.setCallback(messageReceived);\n" +
            "\n" +
            "  connectToMqtt();\n" +
            "\n" +
            "  sendShadowUpdate(VERSION);\n" +
            "  checkNewFirmwareAndUpdateIfNeeded();";

    private static final String LOOP_CODE = "#ifdef WIFIMANAGER\n" +
            "  if (!client.connected())\n" +
            "  {\n" +
            "    connectToMqtt();\n" +
            "  }\n" +
            "  else\n" +
            "  {\n" +
            "    client.loop();\n" +
            "  }\n" +
            "#else\n" +
            "  if ((WiFiMulti.run() == WL_CONNECTED))\n" +
            "  {\n" +
            "    if (!client.connected())\n" +
            "    {\n" +
            "      connectToMqtt();\n" +
            "    }\n" +
            "    else\n" +
            "    {\n" +
            "      client.loop();\n" +
            "    }\n" +
            "  }\n" +
            "#endif\n" +
            "\n" +
            "  delay(5000);";

    private static final String GLOBAL_FUNCTIONS_CODE = "void NTPConnect()\n" +
            "{\n" +
            "  Serial.print(\"Setting time using SNTP\");\n" +
            "  configTime(TIME_ZONE * 3600, DST * 3600, \"pool.ntp.org\", \"time.nist.gov\");\n" +
            "  now = time(nullptr);\n" +
            "  while (now < nowish)\n" +
            "  {\n" +
            "    delay(500);\n" +
            "    Serial.print(\".\");\n" +
            "    now = time(nullptr);\n" +
            "  }\n" +
            "  Serial.println(\"done!\");\n" +
            "  struct tm timeinfo;\n" +
            "  gmtime_r(&now, &timeinfo);\n" +
            "  Serial.print(\"Current time: \");\n" +
            "  Serial.print(asctime(&timeinfo));\n" +
            "}\n" +
            "\n" +
            "void pubSubErr(int8_t MQTTErr)\n" +
            "{\n" +
            "  if (MQTTErr == MQTT_CONNECTION_TIMEOUT)\n" +
            "    Serial.print(\"Connection tiemout\");\n" +
            "  else if (MQTTErr == MQTT_CONNECTION_LOST)\n" +
            "    Serial.print(\"Connection lost\");\n" +
            "  else if (MQTTErr == MQTT_CONNECT_FAILED)\n" +
            "    Serial.print(\"Connect failed\");\n" +
            "  else if (MQTTErr == MQTT_DISCONNECTED)\n" +
            "    Serial.print(\"Disconnected\");\n" +
            "  else if (MQTTErr == MQTT_CONNECTED)\n" +
            "    Serial.print(\"Connected\");\n" +
            "  else if (MQTTErr == MQTT_CONNECT_BAD_PROTOCOL)\n" +
            "    Serial.print(\"Connect bad protocol\");\n" +
            "  else if (MQTTErr == MQTT_CONNECT_BAD_CLIENT_ID)\n" +
            "    Serial.print(\"Connect bad Client-ID\");\n" +
            "  else if (MQTTErr == MQTT_CONNECT_UNAVAILABLE)\n" +
            "    Serial.print(\"Connect unavailable\");\n" +
            "  else if (MQTTErr == MQTT_CONNECT_BAD_CREDENTIALS)\n" +
            "    Serial.print(\"Connect bad credentials\");\n" +
            "  else if (MQTTErr == MQTT_CONNECT_UNAUTHORIZED)\n" +
            "    Serial.print(\"Connect unauthorized\");\n" +
            "}\n" +
            "\n" +
            "void connectToMqtt()\n" +
            "{\n" +
            "  Serial.print(\"MQTT connecting \");\n" +
            "  while (!client.connected())\n" +
            "  {\n" +
            "    if (client.connect(THINGNAME))\n" +
            "    {\n" +
            "      Serial.println(\"connected!\");\n" +
            "      if (!client.subscribe(MQTT_TOPIC_GET_ACCEPTED))\n" +
            "        pubSubErr(client.state());\n" +
            "      if (!client.subscribe(MQTT_TOPIC_UPDATE_DELTA))\n" +
            "        pubSubErr(client.state());\n" +
            "    }\n" +
            "    else\n" +
            "    {\n" +
            "      Serial.print(\"failed, reason -> \");\n" +
            "      pubSubErr(client.state());\n" +
            "      Serial.println(\" < try again in 5 seconds\");\n" +
            "      delay(5000);\n" +
            "    }\n" +
            "  }\n" +
            "}\n" +
            "\n" +
            "void checkNewFirmwareAndUpdateIfNeeded()\n" +
            "{\n" +
            "  sendShadowGet();\n" +
            "}\n" +
            "\n" +
            "void configureWifiForFirmwareUpdate()\n" +
            "{\n" +
            "  Serial.print(\"Reconnecting WiFi...\");\n" +
            "  while (!WiFi.reconnect())\n" +
            "  {\n" +
            "    Serial.print(\".\");\n" +
            "    delay(5000);\n" +
            "  }\n" +
            "  Serial.println(\" success\");\n" +
            "\n" +
            "  Serial.print(\"Validating certificate... \");\n" +
            "  if (net.verify(S3_FINGETPRINT, \"s3.amazonaws.com\"))\n" +
            "  {\n" +
            "    Serial.println(\"certificate matches\");\n" +
            "  }\n" +
            "  else\n" +
            "  {\n" +
            "    Serial.println(\"certificate doesn't match\");\n" +
            "    return;\n" +
            "  }\n" +
            "\n" +
            "  net.setFingerprint(S3_FINGETPRINT);\n" +
            "}\n" +
            "\n" +
            "void updateFirmware()\n" +
            "{\n" +
            "  Serial.println();\n" +
            "  Serial.println(\"Preparing to update firmware...\");\n" +
            "\n" +
            "  String fwURL = String(S3_BUCKET);\n" +
            "  fwURL.concat(\"firmware.bin\");\n" +
            "  Serial.println(fwURL);\n" +
            "\n" +
            "  configureWifiForFirmwareUpdate();\n" +
            "\n" +
            "  Serial.println(\"Updating...\");\n" +
            "  t_httpUpdate_return ret = ESPhttpUpdate.update(net, fwURL, \"\");\n" +
            "  switch (ret)\n" +
            "  {\n" +
            "  case HTTP_UPDATE_FAILED:\n" +
            "    Serial.printf(\"HTTP_UPDATE_FAILD Error (%d): %s\", ESPhttpUpdate.getLastError(), ESPhttpUpdate.getLastErrorString().c_str());\n" +
            "    break;\n" +
            "\n" +
            "  case HTTP_UPDATE_NO_UPDATES:\n" +
            "    Serial.println(\"HTTP_UPDATE_NO_UPDATES\");\n" +
            "    break;\n" +
            "\n" +
            "  case HTTP_UPDATE_OK:\n" +
            "    Serial.println(\"HTTP_UPDATE_OK\");\n" +
            "    break;\n" +
            "  }\n" +
            "}\n" +
            "\n" +
            "void sendData(JsonObject root, const char topic[])\n" +
            "{\n" +
            "  Serial.printf(\"--> Sending  [%s] \", topic);\n" +
            "\n" +
            "  serializeJson(root, Serial);\n" +
            "  char shadow[measureJson(root) + 1];\n" +
            "  serializeJson(root, shadow, sizeof(shadow));\n" +
            "  if (!client.publish(topic, shadow, false))\n" +
            "    pubSubErr(client.state());\n" +
            "\n" +
            "  Serial.println();\n" +
            "}\n" +
            "\n" +
            "void sendShadowGet()\n" +
            "{\n" +
            "  DynamicJsonDocument jsonBuffer(JSON_OBJECT_SIZE(3) + 100);\n" +
            "  JsonObject root = jsonBuffer.to<JsonObject>();\n" +
            "\n" +
            "  sendData(root, MQTT_TOPIC_GET);\n" +
            "}\n" +
            "\n" +
            "void sendShadowUpdate(int firmwareVersion)\n" +
            "{\n" +
            "  DynamicJsonDocument jsonBuffer(JSON_OBJECT_SIZE(3) + 100);\n" +
            "  JsonObject root = jsonBuffer.to<JsonObject>();\n" +
            "  JsonObject state = root.createNestedObject(\"state\");\n" +
            "  JsonObject state_reported = state.createNestedObject(\"reported\");\n" +
            "  state_reported[\"firmware\"] = firmwareVersion;\n" +
            "\n" +
            "  sendData(root, MQTT_TOPIC_UPDATE);\n" +
            "}\n" +
            "\n" +
            "void messageReceived(char *topic, byte *payload, unsigned int length)\n" +
            "{\n" +
            "  String topicStr = String(topic);\n" +
            "  Serial.print(\"--> Received [\" + topicStr + \"] \");\n" +
            "\n" +
            "  String payloadStr = String((char *)payload);\n" +
            "  Serial.println(payloadStr);\n" +
            "\n" +
            "  if (topicStr == MQTT_TOPIC_GET_ACCEPTED)\n" +
            "  {\n" +
            "    DynamicJsonDocument doc(2048);\n" +
            "    deserializeJson(doc, payloadStr);\n" +
            "    JsonObject obj = doc.as<JsonObject>();\n" +
            "    JsonObject state = obj[\"state\"];\n" +
            "    JsonObject delta = state[\"delta\"];\n" +
            "    int version = delta[\"firmware\"].as<int>();\n" +
            "\n" +
            "    if (version != 0)\n" +
            "    {\n" +
            "      Serial.println(\"New firmware version: \" + String(version));\n" +
            "      updateFirmware();\n" +
            "    }\n" +
            "  }\n" +
            "  else if (topicStr == MQTT_TOPIC_UPDATE_DELTA)\n" +
            "  {\n" +
            "    DynamicJsonDocument doc(2048);\n" +
            "    deserializeJson(doc, payloadStr);\n" +
            "    JsonObject obj = doc.as<JsonObject>();\n" +
            "    JsonObject state = obj[\"state\"];\n" +
            "    int version = state[\"firmware\"].as<int>();\n" +
            "\n" +
            "    if (version != 0)\n" +
            "    {\n" +
            "      Serial.println(\"New firmware version: \" + String(version));\n" +
            "      updateFirmware();\n" +
            "    }\n" +
            "  }\n" +
            "}";

    private static final String DEFINE_WIFIMANAGER = "#define WIFIMANAGER true //Use WiFi Manager or fixed SSID and password";

    public String injectOTACode(String sourceCode, String firmwareVersion) {
        String code = this.configureWifi(sourceCode);
        code = code.replace(REPLACE_GLOBAL_INIT, GLOBAL_INIT_CODE);
        code = code.replace(REPLACE_SETUP, SETUP_CODE);
        code = code.replace(REPLACE_LOOP, LOOP_CODE);
        code = code.replace(REPLACE_GLOBAL_FUNCTIONS, GLOBAL_FUNCTIONS_CODE);
        code = code.replace(REPLACE_FIRMWARE_VERSION, firmwareVersion);
        return code;
    }

    /**
     * Busca pelo placeholder REGEX_CONFI_WIFI_BLOCK no código.
     * <ul>
     * <li>Se existir significa que o usuário configurou manualmente o WiFi. Nesse caso obtém a senha e remove a definição
     * do WiFi Manager.</li>
     * <li>Se não exisir significa que o usuário não configurou manualmente o WiFi. Nesse caso define o Wifi Manager.</li>
     * </ul>
     */
    private String configureWifi(String sourceCode) {
        final Matcher matcher = Pattern.compile(REGEX_CONFI_WIFI_BLOCK).matcher(sourceCode);
        if (!matcher.matches()) {
            return sourceCode.replace(REPLACE_DEFINE_WIFIMANAGER, DEFINE_WIFIMANAGER);
        }

        final String ssid = matcher.group(1);
        final String password = matcher.group(2);

        String code = sourceCode.replaceFirst(REGEX_CONFI_WIFI_BLOCK, "");
        code = code.replace(REPLACE_DEFINE_WIFIMANAGER, "");
        code = code.replace(REPLACE_WIFI_SSID, ssid);
        return code.replace(REPLACE_WIFI_PASSWORD, password);
    }

}
