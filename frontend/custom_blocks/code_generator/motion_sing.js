Blockly.JavaScript['motion_sing'] = function (block) {
    let dropdown_song = block.getFieldValue('song');

    let code = "";
    if (dropdown_song === 'HAPPY') {
        code += 'Otto.sing(S_happy);\n';
    }
    if (dropdown_song === 'SAD') {
        code += 'Otto.sing(S_sad);\n';
    }
    if (dropdown_song === 'SURPRISE') {
        code += 'Otto.sing(S_surprise);\n';
    }
    if (dropdown_song === 'HAPPY_BIRTHDAY') {
        code += 'playHappyBirthdaySong();\n';
    }

    return code;
};

class MotionSingBlock {

    static getSetupCode() {
        let code = "";

        if (this._hasBlockWithSong('HAPPY_BIRTHDAY')) {
            code += this._happyBirthdaySong.getSetupCode();
        }

        return code;
    }

    static getGlobalInitCode() {
        let code = "";

        if (this._hasBlockWithSong('HAPPY_BIRTHDAY')) {
            code += this._happyBirthdaySong.getGlobalInitCode();
        }

        return code;
    }

    static getGlobalFunctionsCode() {
        let code = "";

        if (this._hasBlockWithSong('HAPPY_BIRTHDAY')) {
            code += this._happyBirthdaySong.getGlobalFunctionsCode();
        }

        return code;
    }

    static _hasBlockWithSong(song) {
        return OttoCodeGenerator.getAllBLocksOnWorkspace('motion_sing')
            .some(b => b.getFieldValue('song') === song);
    }

    static _happyBirthdaySong = {

        getSetupCode() {
            return `
            ////////////////////////////////////////////////////////////////////////////////////////
            //////////////////////////////MOTION_DANCE HAPPY BIRTHDAY///////////////////////////////
            ////////////////////////////////////////////////////////////////////////////////////////
            pinMode(PIN_BUZZER, OUTPUT);
            ////////////////////////////////////////////////////////////////////////////////////////\n`;
        },

        getGlobalInitCode() {
            return `
            ////////////////////////////////////////////////////////////////////////////////////////
            //////////////////////////////MOTION_DANCE HAPPY BIRTHDAY///////////////////////////////
            ////////////////////////////////////////////////////////////////////////////////////////
            int happyBirthdayMusicLength = 28; // the number of notes
            char happyBirthdayNotes[] = "GGAGcB GGAGdc GGxecBA yyecdc";
            int happyBirthdayBeats[] = { 2, 2, 8, 8, 8, 16, 1, 2, 2, 8, 8,8, 16, 1, 2,2,8,8,8,8,16, 1,2,2,8,8,8,16 };
            int happyBirthdayTempo = 150;
            
            void playTone(int tone, int duration);
            void playNote(char note, int duration);
            void playHappyBirthdaySong();
            ////////////////////////////////////////////////////////////////////////////////////////\n`;
        },

        getGlobalFunctionsCode() {
            return `
            ////////////////////////////////////////////////////////////////////////////////////////
            //////////////////////////////MOTION_DANCE HAPPY BIRTHDAY///////////////////////////////
            ////////////////////////////////////////////////////////////////////////////////////////
            void playHappyBirthdaySong() {
                for (int i = 0; i < happyBirthdayMusicLength; i++) {
                   if (happyBirthdayNotes[i] == ' ') {
                     delay(happyBirthdayBeats[i] * happyBirthdayTempo); // rest
                   } else {
                     playNote(happyBirthdayNotes[i], happyBirthdayBeats[i] * happyBirthdayTempo);
                   }
                   // pause between happyBirthdayNotes
                   delay(happyBirthdayTempo);
                }
            }
            
            void playTone(int tone, int duration) {
            
                for (long i = 0; i < duration * 1000L; i += tone * 2) {
                   digitalWrite(PIN_BUZZER, HIGH);
                   delayMicroseconds(tone);
                   digitalWrite(PIN_BUZZER, LOW);
                   delayMicroseconds(tone);
                }
            }
            
            void playNote(char note, int duration) {
            
                char names[] = {'C', 'D', 'E', 'F', 'G', 'A', 'B',           
                
                                 'c', 'd', 'e', 'f', 'g', 'a', 'b',
                
                                 'x', 'y' };
                
                int tones[] = { 1915, 1700, 1519, 1432, 1275, 1136, 1014,
                
                                 956,  834,  765,  593,  468,  346,  224,
                
                                 655 , 715 };
                
                int SPEE = 5;
                // play the tone corresponding to the note name
                
                for (int i = 0; i < 17; i++) {
                   if (names[i] == note) {
                    int newduration = duration/SPEE;
                     playTone(tones[i], newduration);
                   }
                }
            }
            ////////////////////////////////////////////////////////////////////////////////////////\n`;
        }
    };
}
