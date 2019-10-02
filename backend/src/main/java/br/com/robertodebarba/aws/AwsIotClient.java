package br.com.robertodebarba.aws;

import com.amazonaws.services.iot.client.AWSIotDevice;
import com.amazonaws.services.iot.client.AWSIotException;
import com.amazonaws.services.iot.client.AWSIotMqttClient;
import com.amazonaws.services.iot.client.AWSIotTimeoutException;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import java.util.UUID;

@ApplicationScoped
public class AwsIotClient {

    private Logger logger = LoggerFactory.getLogger(AwsIotClient.class);

    @ConfigProperty(name = "aws.iot.clientendpoint")
    String clientEndpoint;

    @ConfigProperty(name = "aws.iot.thingname")
    String thingName;

    private AWSIotDevice device;

    @PostConstruct
    private void postConstruct() {
        logger.info("Connecting to AWS IoT device shadow");

        final String clientId = UUID.randomUUID().toString();
        final String certificateFile = "aws-iot-certificate.pem.crt";
        final String privateKeyFile = "aws-iot-private.pem.key";

        final SampleUtil.KeyStorePasswordPair pair = SampleUtil.getKeyStorePasswordPair(certificateFile, privateKeyFile);
        final AWSIotMqttClient client = new AWSIotMqttClient(clientEndpoint, clientId, pair.keyStore, pair.keyPassword);

        try {
            device = new AWSIotDevice(thingName);

            client.attach(device);
            client.connect();

            logger.info("Device shadow connected successfully");
        } catch (AWSIotException e) {
            throw new RuntimeException("Failed to connect to device shadow", e);
        }
    }

    public void updateFirmwareVersion(String version) {
        if (device != null) {
            try {
                logger.info("Updating device shadow...");
                device.update("{\"state\":{\"desired\":{\"firmware\":" + version + "}}}", 3000L);
            } catch (AWSIotException | AWSIotTimeoutException e) {
                throw new RuntimeException("Failed to update device shadow", e);
            }
        }
    }

}
