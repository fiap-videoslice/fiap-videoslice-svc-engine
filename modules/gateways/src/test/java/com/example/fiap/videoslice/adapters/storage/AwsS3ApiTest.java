package com.example.fiap.videoslice.adapters.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.env.Environment;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AwsS3ApiTest {

    private Environment environment;
    private S3Client s3Client;
    private AwsS3Api awsS3Api;

    @BeforeEach
    void setUp() {
        environment = mock(Environment.class);
        s3Client = mock(S3Client.class);
        awsS3Api = mock(AwsS3Api.class);
        when(environment.getProperty("videoslice.integration.s3.s3Endpoint")).thenReturn("https://s3.local");
        when(environment.getProperty("videoslice.integration.s3.bucketName")).thenReturn("my-bucket");

    }

//    @Test
//    void testSaveFile() {
//        // Arrange
//        File file = new File("test.txt");
//        when(s3Client.putObject(any(PutObjectRequest.class), eq(file.toPath()))).thenReturn(mock(PutObjectResponse.class));
//
//        // Act
//        String result = awsS3Api.saveFile(file);
//
//        // Assert
//        assertNotNull(result);
//        assertEquals("https://my-bucket.s3.local/test.txt", result);
//        verify(s3Client, times(1)).putObject(any(PutObjectRequest.class), eq(file.toPath()));
//    }

    @Test
    void testGetBucketFullPathWithHttps() {
        // Arrange
        awsS3Api = new AwsS3Api(environment);

        // Act
        String result = awsS3Api.getBucketFullPath();

        // Assert
        assertEquals("https://my-bucket.s3.local", result);
    }

    @Test
    void testGetBucketFullPathWithoutHttps() {
        // Arrange
        when(environment.getProperty("videoslice.integration.s3.s3Endpoint")).thenReturn("http://s3.local");
        awsS3Api = new AwsS3Api(environment);

        // Act
        String result = awsS3Api.getBucketFullPath();

        // Assert
        assertEquals("http://s3.local", result);
    }

    @Test
    void testConstructorWithNullS3Endpoint() {
        // Arrange
        when(environment.getProperty("videoslice.integration.s3.s3Endpoint")).thenReturn(null);

        // Act & Assert
        Exception exception = assertThrows(NullPointerException.class, () -> new AwsS3Api(environment));
        assertEquals("videoslice.integration.s3.s3Endpoint not set", exception.getMessage());
    }

    @Test
    void testConstructorWithNullBucketName() {
        // Arrange
        when(environment.getProperty("videoslice.integration.s3.bucketName")).thenReturn(null);

        // Act & Assert
        Exception exception = assertThrows(NullPointerException.class, () -> new AwsS3Api(environment));
        assertEquals("videoslice.integration.s3.bucketName not set", exception.getMessage());
    }
}