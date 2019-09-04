Blockly.JavaScript['control_dowhile'] = function (block) {
    let value_loopcondition = Blockly.JavaScript.valueToCode(block, 'loopcondition', Blockly.JavaScript.ORDER_ATOMIC);
    let statements_loop = Blockly.JavaScript.statementToCode(block, 'loop');

    return `
    while (${value_loopcondition}) {
        ${statements_loop}
    };
    \n`;
};