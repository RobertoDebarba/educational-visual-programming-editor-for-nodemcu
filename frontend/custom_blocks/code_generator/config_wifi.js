Blockly.JavaScript['config_wifi'] = function (block) {
    var text_ssid = block.getFieldValue('ssid');
    var text_password = block.getFieldValue('password');
    return `
        //DO NOT REMOVE!!!
        //@@REPLACE_DEFINE_WIFI_SSID_AND_PASSWORD=${text_ssid};${text_password}@@
    `;
};