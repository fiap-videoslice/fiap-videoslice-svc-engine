package com.example.fiap.videoslice.adapters.storage;

import com.example.fiap.videoslice.domain.datasource.VideoFileStoreDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.File;
import java.net.URI;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class AwsS3Api implements VideoFileStoreDataSource {
    private static final Logger LOGGER = LoggerFactory.getLogger(AwsS3Api.class);

    private String s3Endpoint;
    private String bucketName;

    @Autowired
    public AwsS3Api(Environment environment) {
        this.s3Endpoint = environment.getProperty("videoslice.integration.sqs.s3Endpoint");
        this.bucketName = environment.getProperty("videoslice.integration.s3.bucketName");

        this.s3Endpoint = Objects.requireNonNull(s3Endpoint, "videoslice.integration.sqs.s3Endpoint not set");
        this.bucketName = Objects.requireNonNull(bucketName, "videoslice.integration.s3.bucketName not set");
    }

    @Override
    public String saveFile(File file) {

        S3Client s3 = S3Client.builder()
                .region(Region.of(Region.US_EAST_1.toString()))
                .endpointOverride(URI.create(s3Endpoint))
//                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKeyId, secretAccessKey)))
                .build();

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(file.getName())
                .build();

        s3.putObject(putObjectRequest, file.toPath());

//        Pattern pattern = Pattern.compile("(https://)");
//        Matcher matcher = pattern.matcher(s3Endpoint);

//        String framesFilePath;
//
//        if (matcher.find()) {
//            framesFilePath = matcher.replaceFirst("$1" + bucketName + ".");
//        } else {
//            framesFilePath = s3Endpoint;
//        }
//        framesFilePath = framesFilePath + "/" + file.getName();

        String framesFilePath = getBucketFullPath() + "/" + file.getName();
        return framesFilePath;
    }

    public String getBucketFullPath(){

        Pattern pattern = Pattern.compile("(https://)");
        Matcher matcher = pattern.matcher(s3Endpoint);

        String bucketFullPath;

        if (matcher.find()) {
            bucketFullPath = matcher.replaceFirst("$1" + bucketName + ".");
        } else {
            bucketFullPath = s3Endpoint;
        }

        return bucketFullPath;
    }
}
