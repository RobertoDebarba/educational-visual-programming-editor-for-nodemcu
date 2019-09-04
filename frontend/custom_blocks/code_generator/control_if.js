Blockly.JavaScript['control_if'] = function (block) {
    let value_if = Blockly.JavaScript.valueToCode(block, 'if', Blockly.JavaScript.ORDER_ATOMIC);
    let statements_then = Blockly.JavaScript.statementToCode(block, 'then');

    return `
    if (${value_if}) {
        ${statements_then}
    };
    \n`;
};
