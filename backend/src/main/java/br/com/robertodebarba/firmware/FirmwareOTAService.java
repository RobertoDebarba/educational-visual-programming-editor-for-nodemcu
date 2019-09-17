package br.com.robertodebarba.firmware;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
class FirmwareOTAService {

    private static final String GLOBAL_INIT_CODE = "#include <ESP8266HTTPClient.h>\n" +
            "#include <ESP8266httpUpdate.h>\n\n" +
            "#include <ESP8266WiFi.h>\n" +
            "#include <ESP8266WiFiMulti.h>\n\n" +
            "ESP8266WiFiMulti WiFiMulti;\n\n" +
            "const int FW_VERSION = 0; //Unix Timestamp\n\n" +
            "const char *fwUrlBase = \"https://vpl-esp8266.s3.amazonaws.com/\";\n" +
            "const char *fingerprint = \"17:E0:A9:3E:58:AF:0A:06:8D:6C:2D:B6:C1:80:B3:E7:E3:52:D4:8E\";\n\n" +
            "const char *wifiSSID = \"Redmi\";\n" +
            "const char *wifiPassword = \"qwert12345\";\n\n" +
            "void checkForUpdates();";

    private static final String SETUP_CODE = "Serial.begin(115200);\n\n" +
            "  Serial.println();\n" +
            "  Serial.println();\n" +
            "  Serial.println();\n\n" +
            "  for (uint8_t t = 4; t > 0; t--)\n" +
            "  {\n" +
            "    Serial.println(\"Seting up...\");\n" +
            "    delay(1000);\n" +
            "  }\n\n" +
            "  WiFi.mode(WIFI_STA);\n" +
            "  WiFiMulti.addAP(wifiSSID, wifiPassword);";

    private static final String LOOP_CODE = "// wait for WiFi connection\n" +
            "  if ((WiFiMulti.run() == WL_CONNECTED))\n" +
            "  {\n" +
            "    checkForUpdates();\n" +
            "  }\n\n" +
            "  delay(5000);";

    private static final String GLOBAL_FUNCTIONS_CODE = "void checkForUpdates()\n" +
            "{\n" +
            "  String fwURL = String(fwUrlBase);\n" +
            "  fwURL.concat(\"firmware\");\n" +
            "  String fwVersionURL = String(fwUrlBase);\n" +
            "  fwVersionURL.concat(\"version.txt\");\n\n" +
            "  Serial.println(\"Checking for firmware updates...\");\n" +
            "  Serial.print(\"Firmware version URL: \");\n" +
            "  Serial.println(fwVersionURL);\n\n" +
            "  HTTPClient httpClient;\n" +
            "  httpClient.begin(fwVersionURL, fingerprint);\n" +
            "  int httpCode = httpClient.GET();\n" +
            "  if (httpCode == 200)\n" +
            "  {\n" +
            "    String newFWVersion = httpClient.getString();\n\n" +
            "    Serial.print(\"Current firmware version: \");\n" +
            "    Serial.println(FW_VERSION);\n" +
            "    Serial.print(\"Available firmware version: \");\n" +
            "    Serial.println(newFWVersion);\n\n" +
            "    int newVersion = newFWVersion.toInt();\n\n" +
            "    if (newVersion > FW_VERSION)\n" +
            "    {\n" +
            "      Serial.println(\"Preparing to update...\");\n\n" +
            "      String fwImageURL = fwURL;\n" +
            "      fwImageURL.concat(\".bin\");\n" +
            "      t_httpUpdate_return ret = ESPhttpUpdate.update(fwImageURL, \"\", fingerprint);\n\n" +
            "      switch (ret)\n" +
            "      {\n" +
            "      case HTTP_UPDATE_FAILED:\n" +
            "        Serial.printf(\"HTTP_UPDATE_FAILD Error (%d): %s\", ESPhttpUpdate.getLastError(), ESPhttpUpdate.getLastErrorString().c_str());\n" +
            "        break;\n\n" +
            "      case HTTP_UPDATE_NO_UPDATES:\n" +
            "        Serial.println(\"HTTP_UPDATE_NO_UPDATES\");\n" +
            "        break;\n\n" +
            "      case HTTP_UPDATE_OK:\n" +
            "        Serial.println(\"HTTP_UPDATE_OK\");\n" +
            "        break;\n" +
            "      }\n" +
            "    }\n" +
            "  }\n" +
            "  else\n" +
            "  {\n" +
            "    Serial.print(\"Firmware version check failed, got HTTP response code \");\n" +
            "    Serial.println(httpCode);\n" +
            "  }\n\n" +
            "  httpClient.end();\n" +
            "}";

    private static final String REPLACE_GLOBAL_INIT = "//@@REPLACE_GLOBAL_INIT@@";
    private static final String REPLACE_SETUP = "//@@REPLACE_SETUP@@";
    private static final String REPLACE_LOOP = "//@@REPLACE_LOOP@@";
    private static final String REPLACE_GLOBAL_FUNCTIONS = "//@@REPLACE_GLOBAL_FUNCTIONS@@";

    public String injectOTACode(String sourceCode) {
        String code = sourceCode.replace(REPLACE_GLOBAL_INIT, GLOBAL_INIT_CODE);
        code = code.replace(REPLACE_SETUP, SETUP_CODE);
        code = code.replace(REPLACE_LOOP, LOOP_CODE);
        return code.replace(REPLACE_GLOBAL_FUNCTIONS, GLOBAL_FUNCTIONS_CODE);
    }


}
