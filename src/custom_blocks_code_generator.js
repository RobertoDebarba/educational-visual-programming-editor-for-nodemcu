Blockly.JavaScript['motion_moveforward'] = function(block) {
  var number_steps = block.getFieldValue('steps');
  // TODO: Assemble JavaScript into code variable.
  var code = '...;\n';
  return code;
};

Blockly.JavaScript['motion_moveback'] = function(block) {
  var number_steps = block.getFieldValue('steps');
  // TODO: Assemble JavaScript into code variable.
  var code = '...;\n';
  return code;
};

Blockly.JavaScript['motion_turndirection'] = function(block) {
  var angle_angle = block.getFieldValue('angle');
  var dropdown_direction = block.getFieldValue('direction');
  // TODO: Assemble JavaScript into code variable.
  var code = '...;\n';
  return code;
};

Blockly.JavaScript['motion_dance'] = function(block) {
  // TODO: Assemble JavaScript into code variable.
  var code = '...;\n';
  return code;
};

Blockly.JavaScript['motion_feeling'] = function(block) {
  var dropdown_feeling = block.getFieldValue('feeling');
  // TODO: Assemble JavaScript into code variable.
  var code = '...;\n';
  return code;
};

Blockly.JavaScript['motion_sing'] = function(block) {
  var dropdown_song = block.getFieldValue('song');
  // TODO: Assemble JavaScript into code variable.
  var code = '...;\n';
  return code;
};

Blockly.JavaScript['operator_hasobstruction'] = function(block) {
  // TODO: Assemble JavaScript into code variable.
  var code = '...';
  // TODO: Change ORDER_NONE to the correct strength.
  return [code, Blockly.JavaScript.ORDER_NONE];
};

Blockly.JavaScript['event_start'] = function(block) {
  // TODO: Assemble JavaScript into code variable.
  var code = '...;\n';
  return code;
};

Blockly.JavaScript['event_touchhead'] = function(block) {
  // TODO: Assemble JavaScript into code variable.
  var code = '...;\n';
  return code;
};

Blockly.JavaScript['control_waitseconds'] = function(block) {
  var number_seconds = block.getFieldValue('seconds');
  // TODO: Assemble JavaScript into code variable.
  var code = '...;\n';
  return code;
};

Blockly.JavaScript['control_whiletrue'] = function(block) {
  var statements_loop = Blockly.JavaScript.statementToCode(block, 'loop');
  // TODO: Assemble JavaScript into code variable.
  var code = '...;\n';
  return code;
};

Blockly.JavaScript['control_dowhile'] = function(block) {
  var value_loopcondition = Blockly.JavaScript.valueToCode(block, 'loopcondition', Blockly.JavaScript.ORDER_ATOMIC);
  var statements_loop = Blockly.JavaScript.statementToCode(block, 'loop');
  // TODO: Assemble JavaScript into code variable.
  var code = '...;\n';
  return code;
};

Blockly.JavaScript['control_for'] = function(block) {
  var number_loopcount = block.getFieldValue('loopcount');
  var statements_loop = Blockly.JavaScript.statementToCode(block, 'loop');
  // TODO: Assemble JavaScript into code variable.
  var code = '...;\n';
  return code;
};

Blockly.JavaScript['control_if'] = function(block) {
  var value_if = Blockly.JavaScript.valueToCode(block, 'if', Blockly.JavaScript.ORDER_ATOMIC);
  var statements_then = Blockly.JavaScript.statementToCode(block, 'then');
  // TODO: Assemble JavaScript into code variable.
  var code = '...;\n';
  return code;
};

Blockly.JavaScript['control_ifelse'] = function(block) {
  var value_if = Blockly.JavaScript.valueToCode(block, 'if', Blockly.JavaScript.ORDER_ATOMIC);
  var statements_then = Blockly.JavaScript.statementToCode(block, 'then');
  var statements_else = Blockly.JavaScript.statementToCode(block, 'else');
  // TODO: Assemble JavaScript into code variable.
  var code = '...;\n';
  return code;
};