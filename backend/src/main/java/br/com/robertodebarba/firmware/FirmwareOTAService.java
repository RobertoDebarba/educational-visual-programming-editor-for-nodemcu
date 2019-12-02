package br.com.robertodebarba.firmware;

import javax.enterprise.context.ApplicationScoped;

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

    private static final String GLOBAL_INIT_CODE = "#include <ESP8266WiFi.h>\n" +
            "#include <ESP8266WiFiMulti.h>\n" +
            "//---OTA--------------------------------------------------\n" +
            "#include <ESP8266httpUpdate.h>\n" +
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
            REPLACE_DEFINE_WIFIMANAGER + " //Use WiFi Manager or fixed SSID and password\n" +
            "\n" +
            "WiFiManager wifiManager;\n" +
            "\n" +
            "WiFiClientSecure net;\n" +
            "ESP8266WiFiMulti WiFiMulti;\n" +
            "\n" +
            "void checkNewFirmwareAndUpdateIfNeeded();\n" +
            "void updateFirmware();";

    private static final String SETUP_CODE = "Serial.begin(115200);\n" +
            "  delay(5000);\n" +
            "  Serial.println();\n" +
            "  Serial.println();\n" +
            "\n" +
            "  wifiManager.autoConnect(\"Otto FURB\");";

    private static final String LOOP_CODE = "  checkNewFirmwareAndUpdateIfNeeded();\n" +
            "  delay(5000);";

    private static final String GLOBAL_FUNCTIONS_CODE = "void checkNewFirmwareAndUpdateIfNeeded()\n" +
            "{\n" +
            "  String fwURL = String(S3_BUCKET);\n" +
            "  fwURL.concat(\"version.txt\");\n" +
            "  Serial.println(fwURL);\n" + "\n" +
            "  HTTPClient httpClient;\n" +
            "  httpClient.begin(fwURL, S3_FINGETPRINT);\n" +
            "  int httpCode = httpClient.GET();\n" +
            "  if (httpCode == 200)\n" +
            "  {\n" +
            "  String newFWVersion = httpClient.getString();\n" +
            "\n" +
            "  Serial.print(\"Current firmware version: \");\n" +
            "  Serial.println(VERSION);\n" +
            "  Serial.print(\"Available firmware version: \");\n" +
            "  Serial.println(newFWVersion);\n" +
            "\n" +
            "  int newVersion = newFWVersion.toInt();\n" +
            "\n" +
            "  if (newVersion > VERSION)\n" +
            "  {\n" +
            "      updateFirmware();\n" +
            "    }\n" +
            "    }\n" +
            "    else\n" +
            "    {\n" +
            "      Serial.print(\"Firmware version check failed, got HTTP response code \");\n" +
            "      Serial.println(httpCode);\n" +
            "    }\n" +
            "\n" +
            "    httpClient.end();\n" +
            "  }\n" +
            "\n" +
            "  void updateFirmware()\n" +
            "  {\n" +
            "  Serial.println();\n" +
            "  Serial.println(\"Preparing to update firmware...\");\n" +
            "\n" +
            "  String fwURL = String(S3_BUCKET);\n" +
            "  fwURL.concat(\"firmware.bin\");\n" +
            "  Serial.println(fwURL);\n" +
            "\n" +
            "  Serial.println(\"Updating...\");\n" +
            "  t_httpUpdate_return ret = ESPhttpUpdate.update(fwURL, \"\", S3_FINGETPRINT);\n" +
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
            "}\n";

    public String injectOTACode(String sourceCode, String firmwareVersion) {
        String code = sourceCode.replace(REPLACE_GLOBAL_INIT, GLOBAL_INIT_CODE);
        code = code.replace(REPLACE_SETUP, SETUP_CODE);
        code = code.replace(REPLACE_LOOP, LOOP_CODE);
        code = code.replace(REPLACE_GLOBAL_FUNCTIONS, GLOBAL_FUNCTIONS_CODE);
        code = code.replace(REPLACE_FIRMWARE_VERSION, firmwareVersion);
        return code;
    }

}
