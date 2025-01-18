package com.example.fiap.videoslice.adapters.messaging;

import com.example.fiap.videoslice.domain.entities.Video;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class VideoStatusAwsSQSTest {

    @Mock
    private AwsSQSApi awsSQSApi;

    @InjectMocks
    private VideoStatusAwsSQS videoStatusAwsSQS;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testUpdateStatusVideo() {
        // Arrange
        Video video = mock(Video.class);
        when(video.getVideoJson()).thenReturn("{\"id\":\"1\",\"status\":\"PROCESSED_OK\"}");
        when(awsSQSApi.getVideoStatusQueueName()).thenReturn("videoStatusQueue");
        when(awsSQSApi.getVideoStatusQueueUrl()).thenReturn("http://localhost:4566/000000000000/videoStatusQueue");

        // Act
        videoStatusAwsSQS.updateStatusVideo(video);

        // Assert
        verify(awsSQSApi, times(1)).sendMessage(anyString(), anyString(), eq("{\"id\":\"1\",\"status\":\"PROCESSED_OK\"}"));
    }

    @Test
    void testNotifyErrorProcessingTheVideo() {
        // Arrange
        String errorMessage = "Error processing video";
        when(awsSQSApi.getVideoStatusQueueName()).thenReturn("videoStatusQueue");
        when(awsSQSApi.getVideoStatusQueueUrl()).thenReturn("http://localhost:4566/000000000000/videoStatusQueue");

        // Act
        videoStatusAwsSQS.notifyErrorProcessingTheVideo(errorMessage);

        // Assert
        verify(awsSQSApi, times(1)).sendMessage(anyString(), anyString(), eq(errorMessage));
    }
}

