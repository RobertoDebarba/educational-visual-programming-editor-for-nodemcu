Blockly.JavaScript['config_wifi'] = function (block) {
    var text_ssid = block.getFieldValue('ssid');
    var text_password = block.getFieldValue('password');
    return `
        //DO NOT REMOVE!!!
        //@@REPLACE_WIFI=${text_ssid};${text_password}@@
    `;
};