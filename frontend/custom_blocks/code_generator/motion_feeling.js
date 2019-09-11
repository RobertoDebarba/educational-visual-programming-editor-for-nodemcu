Blockly.JavaScript['motion_feeling'] = function (block) {
    let dropdown_feeling = block.getFieldValue('feeling');

    if (dropdown_feeling === 'HAPPY') {
        return 'feelHappy();\n';
    } else if (dropdown_feeling === 'SAD') {
        return 'feelSad();\n';
    }
};

class MotionFeelingBlock {

    static getSetupCode() {
        return `
        lc.shutdown(0,false);       //The MAX72XX is in power-saving mode on startup
        lc.setIntensity(0,15);      // Set the brightness to maximum value
        lc.clearDisplay(0);         // and clear the display\n`;
    }

    static getGlobalInitCode() {
        return `
        #include <LedControl.h>
        int MATRX_DIN = 12;
        int MATRX_CS =  11;
        int MATRX_CLK = 10;
        
        LedControl lc=LedControl(MATRX_DIN,MATRX_CLK,MATRX_CS,0);
        
        void feelSad();
        void feelHappy();\n`;
    }

    static getGlobalFunctionsCode() {
        return `
        void printByte(byte character []) {
            int i = 0;
            for(i=0;i<8;i++) {
                lc.setRow(0,i,character[i]);
            }
        }
        
        void feelSad() {
            byte sadFeeling[8] = {0x0, 0x0, 0x0, 0x3c, 0x7e, 0xc3, 0x81, 0x81};
            printByte(sadFeeling);
        }
        
        void feelHappy() {
            byte happyFeeling[8] = {0x0, 0x0, 0x0, 0xff, 0x81, 0x81, 0x42, 0x3c};
            printByte(happyFeeling);
        }\n`;
    }

}