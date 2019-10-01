Blockly.Blocks['motion_moveforward'] = {
  init: function() {
    this.appendDummyInput()
        .appendField("Andar")
        .appendField(new Blockly.FieldNumber(0, 0, Infinity, 1), "steps")
        .appendField("passos para frente");
    this.setPreviousStatement(true, null);
    this.setNextStatement(true, null);
    this.setColour(230);
 this.setTooltip("");
 this.setHelpUrl("");
  }
};

Blockly.Blocks['motion_moveback'] = {
  init: function() {
    this.appendDummyInput()
        .appendField("Andar")
        .appendField(new Blockly.FieldNumber(0, 0, Infinity, 1), "steps")
        .appendField("passos para trás");
    this.setPreviousStatement(true, null);
    this.setNextStatement(true, null);
    this.setColour(230);
 this.setTooltip("");
 this.setHelpUrl("");
  }
};

Blockly.Blocks['motion_turndirection'] = {
  init: function() {
    this.appendDummyInput()
        .appendField("Virar")
        .appendField(new Blockly.FieldAngle(0), "angle")
        .appendField("para a")
        .appendField(new Blockly.FieldDropdown([["direita ↻","RIGHT"], ["esquerda ↺","LEFT"]]), "direction");
    this.setPreviousStatement(true, null);
    this.setNextStatement(true, null);
    this.setColour(230);
 this.setTooltip("");
 this.setHelpUrl("");
  }
};

Blockly.Blocks['motion_dance'] = {
  init: function() {
    this.appendDummyInput()
        .appendField("Dançar");
    this.setPreviousStatement(true, null);
    this.setNextStatement(true, null);
    this.setColour(230);
 this.setTooltip("");
 this.setHelpUrl("");
  }
};

Blockly.Blocks['motion_feeling'] = {
  init: function() {
    this.appendDummyInput()
        .appendField("Mostrar sentimento")
        .appendField(new Blockly.FieldDropdown([["😊","HAPPY"], ["😢","SAD"]]), "feeling");
    this.setPreviousStatement(true, null);
    this.setNextStatement(true, null);
    this.setColour(230);
 this.setTooltip("");
 this.setHelpUrl("");
  }
};

Blockly.Blocks['motion_sing'] = {
  init: function() {
    this.appendDummyInput()
        .appendField("Cantar")
        .appendField(new Blockly.FieldDropdown([["Super Mario","SUPER_MARIO"], ["Feliz aniversário","HAPPY_BIRTHDAY"]]), "song");
    this.setPreviousStatement(true, null);
    this.setNextStatement(true, null);
    this.setColour(230);
 this.setTooltip("");
 this.setHelpUrl("");
  }
};

Blockly.Blocks['control_waitseconds'] = {
  init: function() {
    this.appendDummyInput()
        .appendField("Esperar")
        .appendField(new Blockly.FieldNumber(0, 0, Infinity, 1), "seconds")
        .appendField("segundos");
    this.setPreviousStatement(true, null);
    this.setNextStatement(true, null);
    this.setColour(140);
 this.setTooltip("");
 this.setHelpUrl("");
  }
};

Blockly.Blocks['control_whiletrue'] = {
  init: function() {
    this.appendStatementInput("loop")
        .setCheck(null)
        .appendField("Repita para sempre");
    this.setPreviousStatement(true, null);
    this.setNextStatement(true, null);
    this.setColour(140);
 this.setTooltip("");
 this.setHelpUrl("");
  }
};

Blockly.Blocks['control_dowhile'] = {
  init: function() {
    this.appendValueInput("loopcondition")
        .setCheck(null)
        .appendField("Repita até");
    this.appendStatementInput("loop")
        .setCheck(null);
    this.setPreviousStatement(true, null);
    this.setNextStatement(true, null);
    this.setColour(140);
 this.setTooltip("");
 this.setHelpUrl("");
  }
};

Blockly.Blocks['control_for'] = {
  init: function() {
    this.appendStatementInput("loop")
        .setCheck(null)
        .appendField("Repita")
        .appendField(new Blockly.FieldNumber(0, 0, Infinity, 1), "loopcount")
        .appendField("vezes");
    this.setPreviousStatement(true, null);
    this.setNextStatement(true, null);
    this.setColour(140);
 this.setTooltip("");
 this.setHelpUrl("");
  }
};

Blockly.Blocks['control_if'] = {
  init: function() {
    this.appendValueInput("if")
        .setCheck(null)
        .appendField("Se");
    this.appendStatementInput("then")
        .setCheck(null)
        .appendField("então");
    this.setPreviousStatement(true, null);
    this.setNextStatement(true, null);
    this.setColour(140);
 this.setTooltip("");
 this.setHelpUrl("");
  }
};

Blockly.Blocks['control_ifelse'] = {
  init: function() {
    this.appendValueInput("if")
        .setCheck(null)
        .appendField("Se");
    this.appendStatementInput("then")
        .setCheck(null)
        .appendField("então");
    this.appendStatementInput("else")
        .setCheck(null)
        .appendField("senão");
    this.setPreviousStatement(true, null);
    this.setNextStatement(true, null);
    this.setColour(140);
 this.setTooltip("");
 this.setHelpUrl("");
  }
};

Blockly.Blocks['sensor_hasobstruction'] = {
    init: function() {
        this.appendDummyInput()
            .appendField("Tem obstáculo");
        this.setOutput(true, "Boolean");
        this.setColour(290);
        this.setTooltip("");
        this.setHelpUrl("");
    }
};

Blockly.Blocks['sensor_touchhead'] = {
    init: function() {
        this.appendDummyInput()
            .appendField("Tocou na cabeça");
        this.setOutput(true, "Boolean");
        this.setColour(290);
        this.setTooltip("");
        this.setHelpUrl("");
    }
};

Blockly.Blocks['event_setup'] = {
    init: function () {
        this.appendStatementInput("loop")
            .setCheck(null)
            .appendField("Configurar");
        this.setColour(50);
        this.setTooltip("");
        this.setHelpUrl("");
        this.setDeletable(false);
    }
};

Blockly.Blocks['event_loop'] = {
    init: function() {
        this.appendStatementInput("loop")
            .setCheck(null)
            .appendField("Executar para sempre");
        this.setColour(50);
        this.setTooltip("");
        this.setHelpUrl("");
        this.setDeletable(false);
        this.moveBy(200, 0);
    }
};

Blockly.Blocks['config_wifi'] = {
    init: function() {
        this.appendDummyInput()
            .appendField("Configurar Wi-Fi")
            .appendField(new Blockly.FieldTextInput(""), "ssid")
            .appendField("com a senha")
            .appendField(new Blockly.FieldTextInput(""), "password");
        this.setPreviousStatement(true, null);
        this.setNextStatement(true, null);
        this.setColour(180);
        this.setTooltip("");
        this.setHelpUrl("");
    }
};