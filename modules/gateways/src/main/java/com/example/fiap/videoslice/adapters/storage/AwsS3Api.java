package com.example.fiap.videoslice.adapters.storage;

import com.example.fiap.videoslice.adapters.messaging.AwsClientUtils;
import com.example.fiap.videoslice.domain.exception.ApplicationException;
import com.example.fiap.videoslice.domain.processor.VideoFileStoreDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class AwsS3Api implements VideoFileStoreDataSource {
    private static final Logger LOGGER = LoggerFactory.getLogger(AwsS3Api.class);

    private String s3Endpoint;
    private String bucketName;
    private S3Client s3Client;
    private final S3Presigner s3Presigner;

    @Autowired
    public AwsS3Api(Environment environment) {
        this.s3Endpoint = environment.getProperty("videoslice.integration.s3.s3Endpoint");
        this.bucketName = environment.getProperty("videoslice.integration.s3.bucketName");

        this.s3Endpoint = Objects.requireNonNull(s3Endpoint, "videoslice.integration.s3.s3Endpoint not set");
        this.bucketName = Objects.requireNonNull(bucketName, "videoslice.integration.s3.bucketName not set");


        S3ClientBuilder builder = S3Client.builder()
                .region(Region.US_EAST_1);

        builder = AwsClientUtils.maybeOverrideEndpoint(builder, environment);

        s3Client = builder.build();

        this.s3Presigner = S3Presigner.builder()
                .region(Region.US_EAST_1)
                .build();
    }


    @Override
    public String saveFile(File file) throws ApplicationException {
        String framesFilePath;

        try {
            Path path = file.toPath();
            RequestBody requestBody = RequestBody.fromFile(path);

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(file.getName())
                    .contentType("application/zip")
                    .contentLength(file.length())
                    .build();


            // Gerar a URL pré-assinada
            PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(r -> r
                    .signatureDuration(Duration.ofMinutes(120))
                    .putObjectRequest(putObjectRequest));

            // Obter a URL pré-assinada
            String presignedUrl = presignedRequest.url().toString();

            // Fazer o upload do arquivo usando a URL pré-assinada
            uploadFileUsingPresignedUrl(presignedUrl, file);

            framesFilePath = getBucketFullPath() + "/" + file.getName();

            LOGGER.info("File uploaded to {}", framesFilePath);
            return framesFilePath;

        } catch (IOException e) {
            throw new ApplicationException("Error uploading the file to the S3 bucket");

        } catch (S3Exception e) {
            throw new ApplicationException("Error uploading the file to the S3 bucket");
        }
    }


    private void uploadFileUsingPresignedUrl(String presignedUrl, File file) throws IOException {

        java.net.URL url = new java.net.URL(presignedUrl);
        java.net.HttpURLConnection connection = (java.net.HttpURLConnection) url.openConnection();
        connection.setDoOutput(true);
        connection.setRequestMethod("PUT");
        connection.setRequestProperty("Content-Type", "application/zip");

        try (java.io.OutputStream outputStream = connection.getOutputStream()) {
            Files.copy(file.toPath(), outputStream);
        }

        int responseCode = connection.getResponseCode();
        if (responseCode != 200) {
            throw new IOException("Failed to upload file: HTTP response code " + responseCode);
        }
    }


    public String getBucketFullPath() {

        Pattern patternHttps = Pattern.compile("(https://)");
        Matcher matcherHttps = patternHttps.matcher(s3Endpoint);

        Pattern patternHttp = Pattern.compile("(http://)");
        Matcher matcherHttp = patternHttp.matcher(s3Endpoint);

        String bucketFullPath;

        if (matcherHttps.find()) {
            bucketFullPath = matcherHttps.replaceFirst("$1" + bucketName + ".");
        } else if (matcherHttp.find()) {
            bucketFullPath = s3Endpoint + "/" + bucketName;
        } else {
            bucketFullPath = s3Endpoint;
        }

        return bucketFullPath;
    }

    @Override
    public void deleteFile(String filePath) {
        S3Client s3 = S3Client.builder()
                .region(Region.of(Region.US_EAST_1.toString()))
                .endpointOverride(URI.create(s3Endpoint))
                .build();

        String fileName = extractFileNameFromPath(filePath);

        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();

        s3.deleteObject(deleteObjectRequest);

        LOGGER.info("File {} deleted from bucket {}", fileName, bucketName);
    }

    private String extractFileNameFromPath(String filePath) {
        return filePath.substring(filePath.lastIndexOf('/') + 1);
    }
}
