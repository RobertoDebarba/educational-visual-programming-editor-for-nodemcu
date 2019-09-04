Blockly.JavaScript['motion_turndirection'] = function (block) {
    let angle_angle = block.getFieldValue('angle');
    let angle_direction = block.getFieldValue('direction');

    let angle = angle_angle / 20; //TODO calcular quantos graus por passo
    let direction = angle_direction === 'RIGHT' ? 1 : 0;

    return `Otto.turn(${angle},1000,${direction});\n`;
};