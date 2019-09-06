Blockly.JavaScript['motion_sing'] = function (block) {
    let dropdown_song = block.getFieldValue('song');

    if (dropdown_song === 'HAPPY_BIRTHDAY') {
        return 'playHappyBirthdaySong();\n';
    }
};

//TODO Super Mario song
//FIXME pegar o primeiro array não garante que a outra musica não exista

class MotionSingBlock {

    static getSetupCode() {
        let block = OttoCodeGenerator.getBLockIfOnWorkspace('motion_sing');
        let dropdown_song = block.getFieldValue('song');

        if (dropdown_song === 'HAPPY_BIRTHDAY') {
            return this.happyBirthdaySong.getSetupCode();
        }
    }

    static getGlobalInitCode() {
        let block = OttoCodeGenerator.getBLockIfOnWorkspace('motion_sing');
        let dropdown_song = block.getFieldValue('song');

        if (dropdown_song === 'HAPPY_BIRTHDAY') {
            return this.happyBirthdaySong.getGlobalInitCode();
        }
    }

    static getGlobalFunctionsCode() {
        let block = OttoCodeGenerator.getBLockIfOnWorkspace('motion_sing');
        let dropdown_song = block.getFieldValue('song');

        if (dropdown_song === 'HAPPY_BIRTHDAY') {
            return this.happyBirthdaySong.getGlobalFunctionsCode();
        }
    }

    static happyBirthdaySong = {

        getSetupCode() {
            return `
            ////////////////////////////////////////////////////////////////////////////////////////
            //////////////////////////////////////MOTION_DANCE//////////////////////////////////////
            ////////////////////////////////////////////////////////////////////////////////////////
            pinMode(speakerPin, OUTPUT);
            ////////////////////////////////////////////////////////////////////////////////////////\n`;
        },

        getGlobalInitCode() {
            return `
            ////////////////////////////////////////////////////////////////////////////////////////
            //////////////////////////////////////MOTION_DANCE//////////////////////////////////////
            ////////////////////////////////////////////////////////////////////////////////////////
            int speakerPin = 13;
            int musicLength = 28; // the number of notes
            char notes[] = "GGAGcB GGAGdc GGxecBA yyecdc";
            int beats[] = { 2, 2, 8, 8, 8, 16, 1, 2, 2, 8, 8,8, 16, 1, 2,2,8,8,8,8,16, 1,2,2,8,8,8,16 };
            int tempo = 150;
            
            void playTone(int tone, int duration);
            void playNote(char note, int duration);
            void playHappyBirthdaySong();
            ////////////////////////////////////////////////////////////////////////////////////////\n`;
        },

        getGlobalFunctionsCode() {
            return `
            ////////////////////////////////////////////////////////////////////////////////////////
            //////////////////////////////////////MOTION_DANCE//////////////////////////////////////
            ////////////////////////////////////////////////////////////////////////////////////////
            void playHappyBirthdaySong() {
                for (int i = 0; i < musicLength; i++) {
                   if (notes[i] == ' ') {
                     delay(beats[i] * tempo); // rest
                   } else {
                     playNote(notes[i], beats[i] * tempo);
                   }
                   // pause between notes
                   delay(tempo);
                }
            }
            
            void playTone(int tone, int duration) {
            
                for (long i = 0; i < duration * 1000L; i += tone * 2) {
                   digitalWrite(speakerPin, HIGH);
                   delayMicroseconds(tone);
                   digitalWrite(speakerPin, LOW);
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
    }
}

//https://github.com/OttoDIY/DIY/blob/master/Otto_MarioBros/Otto_MarioBros.ino
//https://github.com/OttoDIY/DIY/blob/master/Otto_happybirthday/Otto_happybirthday.ino