Blockly.JavaScript['event_setup'] = function (block) {
    let statements_loop = Blockly.JavaScript.statementToCode(block, 'loop');
    let containsMotionDanceBlock = OttoCodeGenerator.isBLockOnWorkspace('motion_dance');

    let motionDanceSetupCode = containsMotionDanceBlock ? MotionDanceBlock.getSetupCode() : '';

    return `
    ///////////////////////////////////////////////////////////////////
    //-- Setup ------------------------------------------------------//
    ///////////////////////////////////////////////////////////////////
    void setup(){
        Otto.init(PIN_LEFTLEG,PIN_RIGHTLEG,PIN_LEFTFOOT,PIN_RIGHTFOOT,true); //Set the servo pins
        Otto.sing(S_connection); //Otto wake up!
        Otto.home(); //Otto at rest position
        delay(50);
        
        ${motionDanceSetupCode}
        
        ${statements_loop}
    }\n`;
};