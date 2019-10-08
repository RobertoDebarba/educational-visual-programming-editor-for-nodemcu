Blockly.JavaScript['motion_turndirection'] = function (block) {
    let angle_angle = block.getFieldValue('angle');
    let angle_direction = block.getFieldValue('direction');

    let angle = angle_angle / 6;

    return `Otto.turn(${angle},1000,${angle_direction});\n`;
};