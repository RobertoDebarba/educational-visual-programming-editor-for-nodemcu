Blockly.JavaScript['sensor_touchhead'] = function (block) {
    let code = 'isHeadTouched()';
    return [code, Blockly.JavaScript.ORDER_NONE];
};

class SensorTouchHeadBlock {

    static getSetupCode() {
        return `pinMode(PIN_TOUCH_SENSOR, INPUT);\n`;
    }

    static getGlobalInitCode() {
        return `
        #define PIN_TOUCH_SENSOR 3
        
        boolean isHeadTouched();\n`;
    }

    static getGlobalFunctionsCode() {
        return `
        boolean isHeadTouched() {
            return digitalRead(PIN_TOUCH_SENSOR) == HIGH;
        }\n`;
    }

}