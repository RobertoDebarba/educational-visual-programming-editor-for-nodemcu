Blockly.JavaScript['event_loop'] = function (block) {
    let statements_loop = Blockly.JavaScript.statementToCode(block, 'loop');
    return `
    ///////////////////////////////////////////////////////////////////
    //-- Principal Loop ---------------------------------------------//
    ///////////////////////////////////////////////////////////////////
    void loop() {
        ${statements_loop}
    }\n`;
};