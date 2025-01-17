package com.example.fiap.videoslice.adapters.messaging;// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[s3.java.upload_object.complete]

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.File;
import java.io.IOException;
import java.net.URI;

public class UploadObject {

    public static void main(String[] args) throws IOException {
        String diretorioAtual = new File("d:\\tmp\\").getAbsolutePath();
        String videoUrl = diretorioAtual + File.separator + "video.mp4";
        String bucketName = "my-local-bucket";

        File file = new File(videoUrl);

//        S3Client s3Client = S3Client.builder().region(Region.US_EAST_1) // Change to your desired region
//                .credentialsProvider(DefaultCredentialsProvider.create()).build();

//        S3Client s3 = S3Client.builder()
//                .region(Region.of(Region.US_EAST_1.toString()))
//                .endpointOverride(URI.create("http://localhost:4566/my-local-bucket"))
//                .build();
//
//        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
//                .bucket(bucketName)
//                .key("video.mp4")
//                .build();
//
//        s3.putObject(putObjectRequest, file.toPath());


        S3Client s3Client = S3Client.builder()
                .region(Region.of(Region.US_EAST_1.toString()))
//                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create("teste", "teste")))
//                .endpointOverride(URI.create("http://localhost:4566"))
                .endpointOverride(URI.create("https://s3.localhost.localstack.cloud:4566"))
//                .httpClient(UrlConnectionHttpClient.builder().build())
                .build();

        try {
            PutObjectRequest putObjectRequest1 = PutObjectRequest.builder()
                    .bucket("my-local-bucket")
                    .key(file.getName())
                    .build();

            s3Client.putObject(putObjectRequest1, file.toPath());
            System.out.println("File uploaded successfully to bucket: " + "my-local-bucket");
        } catch (S3Exception e) {
            System.err.println("Failed to upload file: " + e.getMessage());
        } finally {
            s3Client.close();
        }




    }
}
