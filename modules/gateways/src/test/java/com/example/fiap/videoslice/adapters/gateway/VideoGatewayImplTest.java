package com.example.fiap.videoslice.adapters.gateway;//import static org.junit.jupiter.api.Assertions.*;

//import com.example.fiap.videoslice.domain.datasource.VideoProcessor;
//import com.example.fiap.videoslice.domain.datasource.VideoFileStoreDataSource;
//import com.example.fiap.videoslice.domain.exception.ApplicationException;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.io.File;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//@ExtendWith(MockitoExtension.class)
//class VideoGatewayImplTest {
//
////    private VideoGateway videoGateway;
////    private final VideoProcessor videoProcessor = mock(VideoProcessor.class);
////    private final VideoFileStoreDataSource videoFileStoreDataSource = mock(VideoFileStoreDataSource.class);
////
////    @BeforeEach
////    void setUp() {
////        videoGateway = new VideoGatewayImpl(videoProcessor, videoFileStoreDataSource);
////    }
//
//
//}

import com.example.fiap.videoslice.domain.processor.VideoProcessor;
import com.example.fiap.videoslice.domain.processor.VideoFileStoreDataSource;
import com.example.fiap.videoslice.domain.entities.Video;
import com.example.fiap.videoslice.domain.exception.ApplicationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class VideoGatewayImplTest {

    @Mock
    private VideoProcessor videoProcessor;

    @Mock
    private VideoFileStoreDataSource videoFileStoreDataSource;

    @InjectMocks
    private VideoGatewayImpl videoGateway;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testVideoSlice() throws ApplicationException {
        Video video = mock(Video.class);
        File mockedFile = mock(File.class);

        when(videoProcessor.videoSlice(video, 10)).thenReturn(mockedFile);

        File result = videoGateway.videoSlice(video, 10);

        assertNotNull(result);
        assertEquals(mockedFile, result);
        verify(videoProcessor, times(1)).videoSlice(video, 10);
    }

    @Test
    void testVideoSliceThrowsApplicationException() throws ApplicationException {
        Video video = mock(Video.class);

        when(videoProcessor.videoSlice(video, 10)).thenThrow(ApplicationException.class);

        assertThrows(ApplicationException.class, () -> videoGateway.videoSlice(video, 10));
        verify(videoProcessor, times(1)).videoSlice(video, 10);
    }

    @Test
    void testSaveFile() throws ApplicationException {
        File file = mock(File.class);
        String expectedPath = "path/to/saved/file";

        when(videoFileStoreDataSource.saveFile(file)).thenReturn(expectedPath);

        String result = videoGateway.saveFile(file);

        assertNotNull(result);
        assertEquals(expectedPath, result);
        verify(videoFileStoreDataSource, times(1)).saveFile(file);
    }
}