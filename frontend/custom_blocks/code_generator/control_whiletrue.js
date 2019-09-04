Blockly.JavaScript['control_whiletrue'] = function (block) {
    let statements_loop = Blockly.JavaScript.statementToCode(block, 'loop');

    return `
    while (true) {
        ${statements_loop}
    };
    \n`;
};