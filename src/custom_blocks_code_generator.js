Blockly.JavaScript['motion_moveforward'] = function(block) {
  let number_steps = block.getFieldValue('steps');
  return `Otto.walk(${number_steps},1000,1); //${number_steps} steps, "TIME". IF HIGHER THE VALUE THEN SLOWER (from 600 to 1400), 1 FORWARD\n`;
};

Blockly.JavaScript['motion_moveback'] = function(block) {
  let number_steps = block.getFieldValue('steps');
  return `Otto.walk(${number_steps},1000,-1); //${number_steps} steps, T, -1 BACKWARD\n`;
};

Blockly.JavaScript['motion_turndirection'] = function(block) {
  //TODO let angle_angle = block.getFieldValue('angle');
  let direction = direction === 'RIGHT' ? 1 : 0;
  return `Otto.turn(2,1000,${direction});\n`;
};

Blockly.JavaScript['motion_dance'] = function(block) {
  // TODO: Assemble JavaScript into code variable.
  let code = '...;\n';
  return code;
};

Blockly.JavaScript['motion_feeling'] = function(block) {
  let dropdown_feeling = block.getFieldValue('feeling');
  // TODO: Assemble JavaScript into code variable.
  let code = '...;\n';
  return code;
};

Blockly.JavaScript['motion_sing'] = function(block) {
  let dropdown_song = block.getFieldValue('song');
  // TODO: Assemble JavaScript into code variable.
  let code = '...;\n';
  return code;
};

Blockly.JavaScript['control_waitseconds'] = function(block) {
  let number_seconds = block.getFieldValue('seconds');
  return `delay(${number_seconds * 1000});\n`;
};

Blockly.JavaScript['control_whiletrue'] = function(block) {
  let statements_loop = Blockly.JavaScript.statementToCode(block, 'loop');
  return `
    while (true) {
      ${statements_loop}
    };\n`;
};

Blockly.JavaScript['control_dowhile'] = function(block) {
  let value_loopcondition = Blockly.JavaScript.valueToCode(block, 'loopcondition', Blockly.JavaScript.ORDER_ATOMIC);
  let statements_loop = Blockly.JavaScript.statementToCode(block, 'loop');
  // TODO: Assemble JavaScript into code variable.
  let code = '...;\n';
  return code;
};

Blockly.JavaScript['control_for'] = function(block) {
  let number_loopcount = block.getFieldValue('loopcount');
  let statements_loop = Blockly.JavaScript.statementToCode(block, 'loop');
  // TODO: Assemble JavaScript into code variable.
  let code = '...;\n';
  return code;
};

Blockly.JavaScript['control_if'] = function(block) {
  let value_if = Blockly.JavaScript.valueToCode(block, 'if', Blockly.JavaScript.ORDER_ATOMIC);
  let statements_then = Blockly.JavaScript.statementToCode(block, 'then');
  // TODO: Assemble JavaScript into code variable.
  let code = '...;\n';
  return code;
};

Blockly.JavaScript['control_ifelse'] = function(block) {
  let value_if = Blockly.JavaScript.valueToCode(block, 'if', Blockly.JavaScript.ORDER_ATOMIC);
  let statements_then = Blockly.JavaScript.statementToCode(block, 'then');
  let statements_else = Blockly.JavaScript.statementToCode(block, 'else');
  // TODO: Assemble JavaScript into code variable.
  let code = '...;\n';
  return code;
};

Blockly.JavaScript['sensor_hasobstruction'] = function(block) {
  // TODO: Assemble JavaScript into code variable.
  let code = '...';
  // TODO: Change ORDER_NONE to the correct strength.
  return [code, Blockly.JavaScript.ORDER_NONE];
};

Blockly.JavaScript['sensor_touchhead'] = function(block) {
  // TODO: Assemble JavaScript into code variable.
  let code = '...';
  // TODO: Change ORDER_NONE to the correct strength.
  return [code, Blockly.JavaScript.ORDER_NONE];
};

Blockly.JavaScript['event_setup'] = function(block) {
  let statements_loop = Blockly.JavaScript.statementToCode(block, 'loop');
  return `
    ///////////////////////////////////////////////////////////////////
    //-- Setup ------------------------------------------------------//
    ///////////////////////////////////////////////////////////////////
    void setup(){
      Otto.init(PIN_LEFTLEG,PIN_RIGHTLEG,PIN_LEFTFOOT,PIN_RIGHTFOOT,true); //Set the servo pins
      Otto.sing(S_connection); //Otto wake up!
      Otto.home();
      delay(50);
      ${statements_loop}
    }\n`;
};

Blockly.JavaScript['event_loop'] = function(block) {
  let statements_loop = Blockly.JavaScript.statementToCode(block, 'loop');
  return `
    ///////////////////////////////////////////////////////////////////
    //-- Principal Loop ---------------------------------------------//
    ///////////////////////////////////////////////////////////////////
    void loop() {
    ${statements_loop}
    }\n`;
};