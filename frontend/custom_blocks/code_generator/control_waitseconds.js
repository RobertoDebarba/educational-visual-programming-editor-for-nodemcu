Blockly.JavaScript['control_waitseconds'] = function (block) {
    let number_seconds = block.getFieldValue('seconds');

    return `delay(${number_seconds * 1000});\n`;
};