package br.com.robertodebarba.firmware;

import br.com.robertodebarba.aws.AwsIotClient;
import br.com.robertodebarba.aws.AwsS3Client;
import br.com.robertodebarba.platformio.PlatformIOService;
import org.apache.commons.io.FileUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

@ApplicationScoped
public class FirmwareService {

    private static final String PROJECT_HOME_PATH = System.getProperty("user.home") + File.separator + "educational-visual-programming-language-for-esp8266";
    private static final String FIRMWARE_SOURCE_PATH = "source";
    private static final String FIRMWARE_OBJECT_NAME = "firmware.bin";

    @ConfigProperty(name = "aws.s3.bucketname")
    String firmwareBucketName;

    @Inject
    PlatformIOService platformIOService;

    @Inject
    FirmwareOTAService firmwareOTAService;

    @Inject
    AwsS3Client awsS3Client;

    @Inject
    AwsIotClient awsIotClient;

    public boolean compile(final String sourceCode) {
        final String processDirectory = this.createProcessDirectory();

        try {
            final String newFirmwareVersion = getUnixEpochTimeAsString();

            final String sourceCodeWithOTA = firmwareOTAService.injectOTACode(sourceCode, newFirmwareVersion);
            final boolean compileResult = this.platformIOService.compile(sourceCodeWithOTA, processDirectory);

            this.uploadFirmwareToS3(processDirectory);
            awsIotClient.updateFirmwareVersion(newFirmwareVersion);

            return compileResult;
        } finally {
            this.deleteProcessDirectory(processDirectory);
        }
    }

    private void uploadFirmwareToS3(String processDirectory) {
        try {
            final String firmwareBuildPath = processDirectory + File.separator + ".pioenvs" + File.separator + "serial" + File.separator + "firmware.bin";
            final byte[] firmware = FileUtils.readFileToByteArray(new File(firmwareBuildPath));

            awsS3Client.getClient().
                    putObject(PutObjectRequest.builder().bucket(firmwareBucketName).key(FIRMWARE_OBJECT_NAME).build(),
                            RequestBody.fromBytes(firmware));
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload firmware build", e);
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

    private String getUnixEpochTimeAsString() {
        return Long.toString(System.currentTimeMillis() / 1000L);
    }

}
