Blockly.JavaScript['event_setup'] = function (block) {
    let statements_loop = Blockly.JavaScript.statementToCode(block, 'loop');
    return `
    ///////////////////////////////////////////////////////////////////
    //-- Setup ------------------------------------------------------//
    ///////////////////////////////////////////////////////////////////
    void setup(){
        Otto.init(PIN_LEFTLEG,PIN_RIGHTLEG,PIN_LEFTFOOT,PIN_RIGHTFOOT,true); //Set the servo pins
        Otto.sing(S_connection); //Otto wake up!
        Otto.home(); //Otto at rest position
        delay(50);
        //End of initialization
        
        ${statements_loop}
    }\n`;
};