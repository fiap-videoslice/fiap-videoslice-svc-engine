package com.example.fiap.videoslice.adapters.messaging;

import com.example.fiap.videoslice.domain.entities.Video;
import com.example.fiap.videoslice.domain.exception.ApplicationException;
import com.example.fiap.videoslice.domain.messaging.VideoStatusEventMessaging;
import com.example.fiap.videoslice.domain.valueobjects.StatusVideo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class VideoEventMessagingGatewayImplTest {

    private final VideoStatusEventMessaging videoEventMessaging = mock(VideoStatusEventMessaging.class);
    private VideoEventMessagingGatewayImpl videoEventMessagingGateway;

    @BeforeEach
    void setUp() {
        videoEventMessagingGateway = new VideoEventMessagingGatewayImpl(videoEventMessaging);
    }


    @Test
    void notificarStatusVideo() throws ApplicationException {

        Video video = Video.newVideo("21", StatusVideo.TO_BE_PROCESSED, "/tmp/myvideo/myvideo123.mp4");

        videoEventMessagingGateway.updateStatusVideo(video);
        verify(videoEventMessaging).updateStatusVideo(any());

    }

    @Test
    void testNotifyErrorProcessingTheVideo() throws ApplicationException {
        // Arrange
        String errorMessage = "Error processing video";

        // Act
        videoEventMessagingGateway.notifyErrorProcessingTheVideo(errorMessage);

        // Assert
        verify(videoEventMessaging, times(1)).notifyErrorProcessingTheVideo(errorMessage);
    }
}
