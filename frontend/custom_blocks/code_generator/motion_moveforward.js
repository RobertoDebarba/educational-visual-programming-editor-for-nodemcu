Blockly.JavaScript['motion_moveforward'] = function (block) {
    let number_steps = block.getFieldValue('steps');

    return `Otto.walk(${number_steps},1000,1); //${number_steps} steps, "TIME". IF HIGHER THE VALUE THEN SLOWER (from 600 to 1400), 1 FORWARD\n`;
};