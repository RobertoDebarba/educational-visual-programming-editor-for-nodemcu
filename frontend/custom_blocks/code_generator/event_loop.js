Blockly.JavaScript['event_loop'] = function (block) {
    let statements_loop = Blockly.JavaScript.statementToCode(block, 'loop');
    return `
    ///////////////////////////////////////////////////////////////////
    //-- Principal Loop ---------------------------------------------//
    ///////////////////////////////////////////////////////////////////
    void loop() {
        //DO NOT REMOVE!!!
        //@@REPLACE_LOOP@@
    
        ${statements_loop}
    }\n`;
};