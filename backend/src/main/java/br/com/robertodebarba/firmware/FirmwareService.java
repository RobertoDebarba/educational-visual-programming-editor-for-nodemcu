package br.com.robertodebarba.firmware;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.commons.io.FileUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import br.com.robertodebarba.aws.AwsIotClient;
import br.com.robertodebarba.aws.AwsS3Client;
import br.com.robertodebarba.platformio.PlatformIOService;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@ApplicationScoped
public class FirmwareService {

    private static final String PROJECT_HOME_PATH = System.getProperty("user.home") + File.separator + "educational-visual-programming-language-for-esp8266";
    private static final String FIRMWARE_SOURCE_PATH = "source";
    private static final String FIRMWARE_OBJECT_NAME = "firmware.bin";
    private static final String FIRMWARE_VERSION_OBJECT_NAME = "version.txt";

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
            final String newFirmwareVersion = this.getUnixEpochTimeAsString();

            final String sourceCodeWithOTA = firmwareOTAService.injectOTACode(sourceCode, newFirmwareVersion);
            final boolean compileResult = this.platformIOService.compile(sourceCodeWithOTA, processDirectory);

            if (compileResult) {
                this.uploadFirmwareToS3(processDirectory);
                this.uploadFirmwareVersionToS3(processDirectory, newFirmwareVersion);
                //                awsIotClient.updateFirmwareVersion(newFirmwareVersion);
            }

            return compileResult;
        } finally {
            this.deleteProcessDirectory(processDirectory);
        }
    }

    private void uploadFirmwareToS3(String processDirectory) {
        try {
            final String firmwareBuildPath = processDirectory + File.separator + ".pio" + File.separator + "build" + File.separator + "serial" + File.separator + "firmware.bin";
            final String firmwareBuildPath2ndTry = processDirectory + File.separator + ".pioenvs" + File.separator + "serial" + File.separator + "firmware.bin";
            byte[] firmware;
            try {
                firmware = FileUtils.readFileToByteArray(new File(firmwareBuildPath));
            } catch (IOException e) {
                firmware = FileUtils.readFileToByteArray(new File(firmwareBuildPath2ndTry));
            }

            awsS3Client.getClient().
                    putObject(PutObjectRequest.builder().bucket(firmwareBucketName).key(FIRMWARE_OBJECT_NAME).build(), //
                              RequestBody.fromBytes(firmware));
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload firmware build", e);
        }
    }

    private void uploadFirmwareVersionToS3(String processDirectory, String firmwareVersion) {
        try {
            final List<String> lines = Collections.singletonList(firmwareVersion);
            final Path path = Paths.get(processDirectory + File.separator + "version.txt");
            Files.write(path, lines, StandardCharsets.UTF_8);

            byte[] versionFileContent = FileUtils.readFileToByteArray(path.toFile());

            awsS3Client.getClient().
                    putObject(PutObjectRequest.builder().bucket(firmwareBucketName).key(FIRMWARE_VERSION_OBJECT_NAME).contentType("text/plain").build(), //
                              RequestBody.fromBytes(versionFileContent));
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload firmware version", e);
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
