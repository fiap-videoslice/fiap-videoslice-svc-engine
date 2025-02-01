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

import java.io.File;
import java.net.URI;
import java.nio.file.Path;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class AwsS3Api implements VideoFileStoreDataSource {
    private static final Logger LOGGER = LoggerFactory.getLogger(AwsS3Api.class);

    private String s3Endpoint;
    private String bucketName;
    private S3Client s3Client;

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
    }

    @Override
    public String saveFile(File file) throws ApplicationException {
        String framesFilePath;

        try {
            System.out.println("AwsS3Api - saveFile");
            System.out.println("AwsS3Api bucketName - " + bucketName);
            System.out.println("AwsS3Api file.getName() - " + file.getName());
            System.out.println("AwsS3Api file.toPath() - " + file.toPath());

            Path path = file.toPath();

            System.out.println("AwsS3Api - pre request body");
            RequestBody requestBody = RequestBody.fromFile(path);

            LOGGER.info("Saving file {} with {} bytes to bucket {}");

            System.out.println("AwsS3Api - pre putObjectRequest");
            
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(file.getName())
                    .build();
            System.out.println("AwsS3Api - pre putObject");
            
            PutObjectResponse response = s3Client.putObject(putObjectRequest, requestBody);
            
            
//            s3Client.putObject(
//                    PutObjectRequest.builder()
//                            .bucket(bucketName)
//                            .key(file.getName())
//                            .build(), file.toPath());

            System.out.println("AwsS3Api - getBucketFullPath() - " + getBucketFullPath());
            framesFilePath = getBucketFullPath() + "/" + file.getName();
            System.out.println("AwsS3Api - framesFilePath - " + framesFilePath);

        } catch (S3Exception e) {
            System.out.println("AwsS3Api - erro - " + e.toString());
            throw new ApplicationException("Error uploading the file to the S3 bucket");
        }

        return framesFilePath;
    }

    public String getBucketFullPath(){

        Pattern patternHttps = Pattern.compile("(https://)");
        Matcher matcherHttps = patternHttps.matcher(s3Endpoint);

        Pattern patternHttp = Pattern.compile("(http://)");
        Matcher matcherHttp = patternHttp.matcher(s3Endpoint);

        String bucketFullPath;

        if (matcherHttps.find()) {
            bucketFullPath = matcherHttps.replaceFirst("$1" + bucketName + ".");
        } else if(matcherHttp.find()){
            bucketFullPath = s3Endpoint + "/" + bucketName;
        }else {
            bucketFullPath = s3Endpoint;
        }

        return bucketFullPath;
    }

    @Override
    public void deleteFile(String filePath) {
        S3Client s3 = S3Client.builder()
                .region(Region.of(Region.US_EAST_1.toString()))
                .endpointOverride(URI.create(s3Endpoint))
//                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKeyId, secretAccessKey)))
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
