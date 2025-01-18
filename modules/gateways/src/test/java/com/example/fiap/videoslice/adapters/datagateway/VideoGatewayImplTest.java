package com.example.fiap.videoslice.adapters.datagateway;//import static org.junit.jupiter.api.Assertions.*;

//import com.example.fiap.videoslice.domain.datasource.VideoDataSource;
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
////    private final VideoDataSource videoDataSource = mock(VideoDataSource.class);
////    private final VideoFileStoreDataSource videoFileStoreDataSource = mock(VideoFileStoreDataSource.class);
////
////    @BeforeEach
////    void setUp() {
////        videoGateway = new VideoGatewayImpl(videoDataSource, videoFileStoreDataSource);
////    }
//
//
//}

import com.example.fiap.videoslice.domain.datasource.VideoDataSource;
import com.example.fiap.videoslice.domain.datasource.VideoFileStoreDataSource;
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
    private VideoDataSource videoDataSource;

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

        when(videoDataSource.videoSlice(video, 10)).thenReturn(mockedFile);

        File result = videoGateway.videoSlice(video, 10);

        assertNotNull(result);
        assertEquals(mockedFile, result);
        verify(videoDataSource, times(1)).videoSlice(video, 10);
    }

    @Test
    void testVideoSliceThrowsApplicationException() throws ApplicationException {
        Video video = mock(Video.class);

        when(videoDataSource.videoSlice(video, 10)).thenThrow(ApplicationException.class);

        assertThrows(ApplicationException.class, () -> videoGateway.videoSlice(video, 10));
        verify(videoDataSource, times(1)).videoSlice(video, 10);
    }

    @Test
    void testSaveFile() {
        File file = mock(File.class);
        String expectedPath = "path/to/saved/file";

        when(videoFileStoreDataSource.saveFile(file)).thenReturn(expectedPath);

        String result = videoGateway.saveFile(file);

        assertNotNull(result);
        assertEquals(expectedPath, result);
        verify(videoFileStoreDataSource, times(1)).saveFile(file);
    }
}