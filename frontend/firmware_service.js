'use strict';

class FirmwareService {

    constructor() {
    }

    compile(code) {
        return axios.post(serverUrl + '/firmware', code, {
            headers: {
                'Content-Type': 'text/plain'
            }
        });
    }

}