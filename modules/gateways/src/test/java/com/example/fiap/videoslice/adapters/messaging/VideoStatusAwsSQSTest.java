package com.example.fiap.videoslice.adapters.messaging;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
public class VideoStatusAwsSQSTest {

    private final AwsSQSApi awsSQSApi = Mockito.mock(AwsSQSApi.class);
    private VideoStatusAwsSQS videoStatusAwsSQS;

    @BeforeEach
    void setUp() {
        videoStatusAwsSQS = new VideoStatusAwsSQS(awsSQSApi);
    }

//    @Test
//    void notificarStatusPagamento() {
//
//        Integer id = 1;
//        String name = "fileName";
//        StatusVideo status = StatusVideo.TO_BE_PROCESSED;
//        String path = "/tmp/fileName.mp4";
//
//       Video video = Video.newVideo(id, name, StatusVideo.TO_BE_PROCESSED, path);
//
//        when(awsSQSApi.getVideoStatusQueueName()).thenReturn("video-processed");
//        when(awsSQSApi.getVideoStatusQueueUrl()).thenReturn("http://localhostxxx:8080");
//
//        videoStatusAwsSQS.updateStatusVideo(video);
//        System.out.println(video.getVideoJson().toString());
//        verify(awsSQSApi).sendMessage(any(), any(), eq(video.getVideoJson()));
//    }
}
