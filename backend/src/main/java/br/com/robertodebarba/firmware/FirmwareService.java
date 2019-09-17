package br.com.robertodebarba.firmware;

import br.com.robertodebarba.platformio.PlatformIOService;
import org.apache.commons.io.FileUtils;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

@ApplicationScoped
public class FirmwareService {

    private static final String PROJECT_HOME_PATH = System.getProperty("user.home") + File.separator + "educational-visual-programming-language-for-esp8266";
    private static final String FIRMWARE_SOURCE_PATH = "source";

    @Inject
    private PlatformIOService platformIOService;

    @Inject
    private FirmwareOTAService firmwareOTAService;

    public boolean compile(final String sourceCode) {
        final String processDirectory = this.createProcessDirectory();

        try {
            final String sourceCodeWithOTA = firmwareOTAService.injectOTACode(sourceCode);
            final boolean compileResult = this.platformIOService.compile(sourceCodeWithOTA, processDirectory);

            return compileResult;
            //TODO upload firmware
        } finally {
            this.deleteProcessDirectory(processDirectory);
        }
    }

    private String createProcessDirectory() {
        try {
            String firmwareSourcePath = PROJECT_HOME_PATH + File.separator + FIRMWARE_SOURCE_PATH;
            String firmwareTargetPath = PROJECT_HOME_PATH + File.separator + UUID.randomUUID().toString();

            FileUtils.copyDirectory(new File(firmwareSourcePath), new File(firmwareTargetPath));

            return firmwareTargetPath;
        } catch (IOException e) {
            throw new RuntimeException("Failed to create project directory", e);
        }
    }

    private void deleteProcessDirectory(String firmwarePath) {
        try {
            FileUtils.deleteDirectory(new File(firmwarePath));
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete project directory", e);
        }
    }

}
