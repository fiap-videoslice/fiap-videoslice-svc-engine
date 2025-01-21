package com.example.fiap.videoslice.adapters.processor;

import com.example.fiap.videoslice.domain.entities.Video;
import com.example.fiap.videoslice.domain.exception.ApplicationException;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.zip.ZipOutputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class VideoSliceProcessorImplTest {

    @InjectMocks
    private VideoSliceProcessorImpl videoSliceRepository;

    @Mock
    private FFmpegFrameGrabber frameGrabber;

    @Mock
    private Java2DFrameConverter converter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testVideoSlice() throws ApplicationException, IOException {
        // Arrange
        Video video = mock(Video.class);
        String classpathRoot = Paths.get("").toAbsolutePath().getParent().getParent().toString();

        String osName = System.getProperty("os.name");
        if (osName.startsWith("Windows")) {
            when(video.getPath()).thenReturn(classpathRoot + "\\addittional-test-files\\video.mp4");
        } else {
            when(video.getPath()).thenReturn(classpathRoot + "/addittional-test-files/video.mp4");
        }

        // Act
        File result = videoSliceRepository.videoSlice(video, 5);

        // Assert
        assertNotNull(result);
        assertTrue(result.exists());
    }

    @Test
    void testVideoSliceThrowsApplicationException() {
        // Arrange
        Video video = mock(Video.class);
        when(video.getPath()).thenReturn("path/to/video.mp4");

        // Act & Assert
        assertThrows(ApplicationException.class, () -> videoSliceRepository.videoSlice(video, 5));
    }

    @Test
    void testExtractFrames() throws ApplicationException, IOException {
        // Arrange
//        String videoFilePath = "d:\\tmp\\video1.mp4";
        String videoFilePath;

        String classpathRoot = Paths.get("").toAbsolutePath().getParent().getParent().toString();

        String osName = System.getProperty("os.name");
        if (osName.startsWith("Windows")) {
            videoFilePath = classpathRoot + "\\addittional-test-files\\video.mp4";
        } else {
            videoFilePath = classpathRoot + "/addittional-test-files/video.mp4";
        }

        int periodInSeconds = 5;
        ZipOutputStream zipOutputStream = mock(ZipOutputStream.class);
        when(frameGrabber.grabImage()).thenReturn(mock(Frame.class), (Frame) null);
        BufferedImage bufferedImage = mock(BufferedImage.class);
        when(converter.convert(any(Frame.class))).thenReturn(bufferedImage);

        // Act
        videoSliceRepository.extractFrames(videoFilePath, periodInSeconds, zipOutputStream);

        // Assert
        verify(zipOutputStream, atLeast(1)).putNextEntry(any());
        verify(zipOutputStream, atLeast(1)).closeEntry();
    }

    @Test
    void testExtractFramesThrowsApplicationException() {
        // Arrange
        String videoFilePath = "path/to/video.mp4";
        int periodInSeconds = 5;
        ZipOutputStream zipOutputStream = mock(ZipOutputStream.class);

        // Act & Assert
        assertThrows(ApplicationException.class, () -> videoSliceRepository.extractFrames(videoFilePath, periodInSeconds, zipOutputStream));
    }
}