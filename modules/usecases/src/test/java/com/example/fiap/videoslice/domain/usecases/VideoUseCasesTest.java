package com.example.fiap.videoslice.domain.usecases;

import com.example.fiap.videoslice.domain.entities.Video;
import com.example.fiap.videoslice.domain.exception.ApplicationException;
import com.example.fiap.videoslice.domain.gateway.VideoEventMessagingGateway;
import com.example.fiap.videoslice.domain.gateway.VideoGateway;
import com.example.fiap.videoslice.domain.valueobjects.StatusVideo;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.io.File;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

public class VideoUseCasesTest {

    private VideoGateway videoGateway;
    private VideoEventMessagingGateway videoEventMessagingGateway;
    private VideoUseCases videoUseCases;
    private String videoFilePath;

    @BeforeEach
    public void setUp() {
        videoGateway = mock(VideoGateway.class);
        videoEventMessagingGateway = mock(VideoEventMessagingGateway.class);
        videoUseCases = new VideoUseCases(videoGateway, videoEventMessagingGateway);


        String classpathRoot = Paths.get("").toAbsolutePath().getParent().getParent().toString();

        String osName = System.getProperty("os.name");
        if (osName.startsWith("Windows")) {
            videoFilePath = classpathRoot + "\\addittional-test-files\\video.mp4";
        } else {
            videoFilePath = classpathRoot + "/addittional-test-files/video.mp4";
        }

    }

    @Test
    public void testExecuteVideoSliceSuccess() throws ApplicationException {
        Video video = Video.newVideo("test_id", null, videoFilePath);
        File file = new File("framesFile.zip");

        when(videoGateway.videoSlice(any(Video.class), anyInt())).thenReturn(file);
        when(videoGateway.saveFile(any(File.class))).thenReturn("uploaded/path/test.mp4");

        String result = videoUseCases.executeVideoSlice(video, 10);

        assertEquals("success", result);
        verify(videoEventMessagingGateway, times(2)).updateStatusVideo(any(Video.class));
    }

    @Test
    public void testExecuteVideoSliceError() throws ApplicationException {
        Video video = Video.newVideo("test_id", null, videoFilePath);

        when(videoGateway.videoSlice(any(Video.class), anyInt())).thenThrow(new ApplicationException("Error processing video"));

        String result = videoUseCases.executeVideoSlice(video, 10);

        assertEquals("error", result);
        verify(videoEventMessagingGateway, times(1)).updateStatusVideo(any(Video.class));
        verify(videoEventMessagingGateway, times(1)).notifyErrorProcessingTheVideo(anyString());
    }

    @Test
    public void testNotifyErrorProcessingTheVideo() {
        String message = "{\"id\": \"12341-1233-UUAAS\",\"path\": \"video.mp4\",\"timeFrame\": 60}";

        videoUseCases.notifyErrorProcessingTheVideo(message);

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(videoEventMessagingGateway, times(1)).notifyErrorProcessingTheVideo(captor.capture());

        JSONObject json = new JSONObject(captor.getValue());
        assertEquals(StatusVideo.PROCESSED_ERROR.toString(), json.getString("status"));

    }

    @Test
    public void testUpdateStatusVideo() throws ApplicationException {
        Video video = Video.newVideo("test_id", null, videoFilePath);

        String result = videoUseCases.updateStatusVideo(video);

        assertEquals("ok", result);
        verify(videoEventMessagingGateway, times(1)).updateStatusVideo(video);
    }



}