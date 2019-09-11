Blockly.JavaScript['sensor_hasobstruction'] = function (block) {
    let code = 'Otto.getDistance() < 15';
    return [code, Blockly.JavaScript.ORDER_NONE];
};