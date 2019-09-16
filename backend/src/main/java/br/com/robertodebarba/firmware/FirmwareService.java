package br.com.robertodebarba.firmware;

import br.com.robertodebarba.platformio.PlatformIOService;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class FirmwareService {

    @Inject
    private PlatformIOService platformIOService;

    public boolean compile(String sourceCode) {
        return this.platformIOService.compile(sourceCode);

        //TODO upload firmware
    }

}
