'use strict';

class CustomCodeGenerator {

    constructor(generator) {
        this.generator = generator;
    }

    generateCode(workspace) {
        let code = `
            #include <Servo.h>
            #include <Oscillator.h>
            #include <Otto.h>
            Otto Otto;  //This is Otto!
            //----------------------------------------------------------------------
            //-- Make sure the servos are in the right pin
            /*             -------- 
                          |  O  O  |
                          |--------|
              RIGHT LEG 3 |        | LEFT LEG 2
                           -------- 
                           ||     ||
            RIGHT FOOT 5 |---     ---| LEFT FOOT 4     
            */
              #define PIN_LEFTLEG 2 //servo[2]
              #define PIN_RIGHTLEG 3 //servo[3]
              #define PIN_LEFTFOOT 4 //servo[4]
              #define PIN_RIGHTFOOT 5 //servo[5]
              
              ${this.generator.workspaceToCode(workspace)}\n`;

        return this.indent(code);
    }

    indent(code) {
        return code.split('\n').map(line => line.trim()).join('\n');
    }
}