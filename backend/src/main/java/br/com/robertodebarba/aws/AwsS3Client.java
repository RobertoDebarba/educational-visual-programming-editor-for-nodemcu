package br.com.robertodebarba.aws;

import software.amazon.awssdk.services.s3.S3Client;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AwsS3Client {

    private S3Client s3Client = S3Client.create();

    public S3Client getClient() {
        return s3Client;
    }
}
