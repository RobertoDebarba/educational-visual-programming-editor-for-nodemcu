Blockly.JavaScript['control_for'] = function (block) {
    let number_loopcount = block.getFieldValue('loopcount');
    let statements_loop = Blockly.JavaScript.statementToCode(block, 'loop');

    return `
    for (int i = 0; i < ${number_loopcount}; i++) {
        ${statements_loop}
    };
    \n`;
};