Blockly.JavaScript['motion_feeling'] = function (block) {
    let dropdown_feeling = block.getFieldValue('feeling');

    if (dropdown_feeling === 'HAPPY') {
        return 'Otto.putMouth(happyOpen, true);\n';
    } else if (dropdown_feeling === 'SAD') {
        return 'Otto.putMouth(sadOpen, true);\n';
    }
};

class MotionFeelingBlock {

    static getSetupCode() {
        return `
        Otto.initMATRIX(MATRX_DIN, MATRX_CS, MATRX_CLK, MATRIX_DIRECTION);
        Otto.matrixIntensity(1);\n`;
    }

    static getGlobalInitCode() {
        return `
        int MATRX_DIN = D8;
        int MATRX_CS =  D7;
        int MATRX_CLK = D6;
        int MATRIX_DIRECTION = 2;\n`;
    }

    static getGlobalFunctionsCode() {
        return '';
    }

}