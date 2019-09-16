'use strict';

class OttoCodeGenerator {

    static generateCode(generator, workspace) {
        let containsMotionDanceBlock = OttoCodeGenerator.isBLockOnWorkspace('motion_dance');
        let motionDanceGlobalInitCode = containsMotionDanceBlock ? MotionDanceBlock.getGlobalInitCode() : '';
        let motionDanceGlobalFunctionsCode = containsMotionDanceBlock ? MotionDanceBlock.getGlobalFunctionsCode() : '';

        let containsMotionSingBlock = OttoCodeGenerator.isBLockOnWorkspace('motion_sing');
        let motionSingGlobalInitCode = containsMotionSingBlock ? MotionSingBlock.getGlobalInitCode() : '';
        let motionSingGlobalFunctionsCode = containsMotionSingBlock ? MotionSingBlock.getGlobalFunctionsCode() : '';

        let containsMotionFeelingBlock = OttoCodeGenerator.isBLockOnWorkspace('motion_feeling');
        let motionFeelingGlobalInitCode = containsMotionFeelingBlock ? MotionFeelingBlock.getGlobalInitCode() : '';
        let motionFeelingGlobalFunctionsCode = containsMotionFeelingBlock ? MotionFeelingBlock.getGlobalFunctionsCode() : '';

        let containsSensorTouchHeadBlock = OttoCodeGenerator.isBLockOnWorkspace('sensor_touchhead');
        let sensorTouchHeadInitCode = containsSensorTouchHeadBlock ? SensorTouchHeadBlock.getGlobalInitCode() : '';
        let sensorTouchHeadGlobalFunctionsCode = containsSensorTouchHeadBlock ? SensorTouchHeadBlock.getGlobalFunctionsCode() : '';

        let code = `
            #include <Servo.h>
            #include <Oscillator.h>
            #include <Otto.h>
            Otto Otto;  //This is Otto!
            
            //----------------------------------------------------------------------
            //-- Make sure the servos are in the right pin
            /*                -------- 
             *               |  O  O  |
             *               |--------|
             *   RIGHT LEG 3 |        | LEFT LEG 2
             *                -------- 
             *                ||     ||
             * RIGHT FOOT 5 |---     ---| LEFT FOOT 4     
             */
            
            #define PIN_LEFTLEG 2 //servo[2]
            #define PIN_RIGHTLEG 3 //servo[3]
            #define PIN_LEFTFOOT 4 //servo[4]
            #define PIN_RIGHTFOOT 5 //servo[5]
            
            /*SOUNDS******************
             * S_connection  S_disconnection  S_buttonPushed S_mode1 S_mode2 S_mode3 S_surprise S_OhOoh  S_OhOoh2  S_cuddly 
             * S_sleeping  S_happy S_superHappy S_happy_short S_sad S_confused S_fart1 S_fart2  S_fart3 
             */
             
            /*MOVEMENTS LIST**************
             * dir=1---> FORWARD/LEFT
             * dir=-1---> BACKWARD/RIGTH
             * T : amount of movement. HIGHER VALUE SLOWER MOVEMENT usually 1000 (from 600 to 1400)
             * h: height of mov. around 20
             *    jump(steps=1, int T = 2000);
             *    walk(steps, T, dir);
             *    turn(steps, T, dir);
             *    bend (steps, T, dir); //usually steps =1, T=2000
             *    shakeLeg (steps, T, dir);
             *    updown(steps, T, HEIGHT);
             *    swing(steps, T, HEIGHT);
             *    tiptoeSwing(steps, T, HEIGHT);
             *    jitter(steps, T, HEIGHT); (small T)
             *    ascendingTurn(steps, T, HEIGHT);
             *    moonwalker(steps, T, HEIGHT,dir);
             *    crusaito(steps, T, HEIGHT,dir);
             *    flapping(steps, T, HEIGHT,dir);
             */
             
            /*GESTURES LIST***************
             * OttoHappy OttoSuperHappy  OttoSad   OttoSleeping  OttoFart  OttoConfused OttoLove  OttoAngry   
             * OttoFretful OttoMagic  OttoWave  OttoVictory  OttoFail
             */
             
            ${motionDanceGlobalInitCode}
            ${motionSingGlobalInitCode}
            ${motionFeelingGlobalInitCode}
            ${sensorTouchHeadInitCode}
              
            ${generator.workspaceToCode(workspace)}

            ${motionDanceGlobalFunctionsCode}
            ${motionSingGlobalFunctionsCode}
            ${motionFeelingGlobalFunctionsCode}
            ${sensorTouchHeadGlobalFunctionsCode}
        `;

        return OttoCodeGenerator._format(code);
    }

    static isBLockOnWorkspace(blockType) {
        return Blockly.getMainWorkspace().getAllBlocks().some(b => b.type === blockType);
    }

    static getAllBLocksOnWorkspace(blockType) {
        return Blockly.getMainWorkspace().getAllBlocks().filter(b => b.type === blockType);
    }

    static _format(code) {
        let indentedCode = OttoCodeGenerator._indent(code);
        return OttoCodeGenerator._removeMultipleBlackLines(indentedCode);
    }

    static _indent(code) {
        let deepCount = 0;

        return code.split('\n').map(line => {
            let openBracketsCount = line.split('{').length - 1;
            let closeBracketsCount = line.split('}').length - 1;

            // Verifica a diferença para não aplcar em casos onde as chaves são abertas e fechadas na mesma linha
            let bracketsDiff = openBracketsCount;
            bracketsDiff -= closeBracketsCount;

            if (bracketsDiff < 0) {
                deepCount -= closeBracketsCount;
            }

            let lineIndented = line.trim();
            for (let i = 0; i < deepCount; i++) {
                lineIndented = '\t' + lineIndented;
            }

            if (bracketsDiff > 0) {
                deepCount += openBracketsCount;
            }

            return lineIndented;
        }).join('\n');
    }

    static _removeMultipleBlackLines(code) {
        return code.replace(/^(?:[\t ]*(?:\r?\n|\r)){2,}/gm, '\n');
    }
}