package com.example.fiap.videoslice.adapters.jobs;

import com.example.fiap.videoslice.adapters.dto.VideoDto;
import com.example.fiap.videoslice.adapters.messaging.AwsSQSApi;
import com.example.fiap.videoslice.adapters.storage.AwsS3Api;
import com.example.fiap.videoslice.domain.entities.Video;
import com.example.fiap.videoslice.domain.exception.DomainArgumentException;
import com.example.fiap.videoslice.domain.usecases.VideoUseCases;
import com.example.fiap.videoslice.jobs.VideosToBeProcessedQueueListener;
import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import software.amazon.awssdk.services.sqs.model.Message;
import uk.org.lidalia.slf4jtest.LoggingEvent;
import uk.org.lidalia.slf4jtest.TestLogger;
import uk.org.lidalia.slf4jtest.TestLoggerFactory;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class VideosToBeProcessedQueueListenerTest {

    @Mock
    private AwsSQSApi awsSQSApi;

    @Mock
    private AwsS3Api awsS3Api;

    @Mock
    private VideoDto videoDto;

    @Mock
    private VideoUseCases videoUseCases;

    @InjectMocks
    private VideosToBeProcessedQueueListener videosToBeProcessedQueueListener;

    private final TestLogger logger = TestLoggerFactory.getTestLogger(VideosToBeProcessedQueueListener.class);

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
//        when(awsS3Api.getBucketFullPath()).thenReturn("https://s3.amazonaws.com/mybucket");
        logger.clear();
    }

    @Test
    public void testProcessVideoQueueMessages() {
        String bucketName = "mybucket";
        when(awsS3Api.getBucketFullPath()).thenReturn(bucketName);
        when(awsSQSApi.getVideosToBeProcessedQueueName()).thenReturn("queueName");
        when(awsSQSApi.getVideosToBeProcessedQueueUrl()).thenReturn("queueUrl");
        when(videoUseCases.executeVideoSlice(any(), anyInt())).thenReturn("success");

        Message message = mock(Message.class);
        when(message.body()).thenReturn("{\"id\":\"1\",\"path\":\"input/test.mp4\",\"timeFrame\":\"50\"}");
        List<Message> messages = Collections.singletonList(message);
        when(awsSQSApi.receiveMessages(anyString())).thenReturn(messages);

        videosToBeProcessedQueueListener.processVideoQueueMessages();

        verify(awsSQSApi, times(1)).receiveMessages(anyString());
        verify(awsS3Api, times(1)).getBucketFullPath();
        verify(awsSQSApi, times(1)).deleteMessageFromQueue("queueName", message);
    }

    @Test
    public void testProcessVideoQueueMessagesLocalPath() {
        String bucketName = "mybucket";
        when(awsS3Api.getBucketFullPath()).thenReturn(bucketName);
        when(awsSQSApi.getVideosToBeProcessedQueueName()).thenReturn("queueName");
        when(awsSQSApi.getVideosToBeProcessedQueueUrl()).thenReturn("queueUrl");
        when(videoUseCases.executeVideoSlice(any(), anyInt())).thenReturn("success");

        Message message = mock(Message.class);
        when(message.body()).thenReturn("{\"id\":\"1\",\"path\":\"test.mp4\",\"timeFrame\":\"50\"}");
        List<Message> messages = Collections.singletonList(message);
        when(awsSQSApi.receiveMessages(anyString())).thenReturn(messages);

        videosToBeProcessedQueueListener.processVideoQueueMessages();

        verify(awsSQSApi, times(1)).receiveMessages(anyString());
        verify(awsS3Api, times(1)).getBucketFullPath();
        verify(awsSQSApi, times(1)).deleteMessageFromQueue("queueName", message);
    }

    @Test
    public void testProcessVideoQueueMessagesError() {
        String bucketName = "mybucket";
        when(awsS3Api.getBucketFullPath()).thenReturn(bucketName);
        when(awsSQSApi.getVideosToBeProcessedQueueName()).thenReturn("queueName");
        when(awsSQSApi.getVideosToBeProcessedQueueUrl()).thenReturn("queueUrl");
        when(videoUseCases.executeVideoSlice(any(), anyInt())).thenReturn("error");

        Message message = mock(Message.class);
        when(message.body()).thenReturn("{\"id\":\"1\",\"path\":\"input/test.mp4\",\"timeFrame\":\"50\"}");
        List<Message> messages = Collections.singletonList(message);
        when(awsSQSApi.receiveMessages(anyString())).thenReturn(messages);

        videosToBeProcessedQueueListener.processVideoQueueMessages();

        verify(awsSQSApi, times(1)).receiveMessages(anyString());
        verify(awsS3Api, times(1)).getBucketFullPath();
        verify(awsSQSApi, times(1)).deleteMessageFromQueue("queueName", message);
    }

    @Test
    public void testProcessVideoQueueMessagesDomainError() {
        String bucketName = "mybucket";
        when(awsS3Api.getBucketFullPath()).thenReturn(bucketName);
        when(awsSQSApi.getVideosToBeProcessedQueueName()).thenReturn("queueName");
        when(awsSQSApi.getVideosToBeProcessedQueueUrl()).thenReturn("queueUrl");

        Message message = mock(Message.class);
        when(message.body()).thenReturn("{\"id\":\"1\",\"path\":\"input/test.mp3\",\"timeFrame\":\"50\"}");
        List<Message> messages = Collections.singletonList(message);
        when(awsSQSApi.receiveMessages(anyString())).thenReturn(messages);

        videosToBeProcessedQueueListener.processVideoQueueMessages();

        verify(videoUseCases, times(1)).notifyErrorProcessingTheVideo(any());
        verify(awsSQSApi, times(1)).deleteMessageFromQueue("queueName", message);

    }
}
