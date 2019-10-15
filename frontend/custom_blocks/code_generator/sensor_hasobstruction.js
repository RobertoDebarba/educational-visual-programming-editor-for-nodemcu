Blockly.JavaScript['sensor_hasobstruction'] = function (block) {
    let code = 'Otto.getDistance() < 10';
    return [code, Blockly.JavaScript.ORDER_NONE];
};