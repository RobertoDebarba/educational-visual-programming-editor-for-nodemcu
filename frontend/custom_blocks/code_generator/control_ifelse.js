Blockly.JavaScript['control_ifelse'] = function (block) {
    let value_if = Blockly.JavaScript.valueToCode(block, 'if', Blockly.JavaScript.ORDER_ATOMIC);
    let statements_then = Blockly.JavaScript.statementToCode(block, 'then');
    let statements_else = Blockly.JavaScript.statementToCode(block, 'else');

    return `
    if (${value_if}) {
        ${statements_then}
    } else {
        ${statements_else}
    };
    \n`;
};