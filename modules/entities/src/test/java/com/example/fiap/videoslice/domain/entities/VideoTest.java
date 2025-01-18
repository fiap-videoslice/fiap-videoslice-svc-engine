package com.example.fiap.videoslice.domain.entities;

import com.example.fiap.videoslice.domain.valueobjects.StatusVideo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.fiap.videoslice.domain.entities.Video;
import com.example.fiap.videoslice.domain.exception.DomainArgumentException;
import com.example.fiap.videoslice.domain.valueobjects.StatusVideo;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VideoTest {

    @Test
    void testNewVideo() {
        Video video = Video.newVideo("1", null, "path/to/video.mp4");
        assertNotNull(video);
        assertEquals("1", video.getId());
        assertEquals(StatusVideo.TO_BE_PROCESSED, video.getStatus());
        assertEquals("path/to/video.mp4", video.getPath());
    }

    @Test
    void testNewVideoNullId() {
        Exception exception = assertThrows(DomainArgumentException.class, () -> {
            Video.newVideo(null, null, "path/to/video.mp4");
        });
        assertEquals("Video should contain id", exception.getMessage());
    }

    @Test
    void testNewVideoNullPath() {
        Exception exception = assertThrows(DomainArgumentException.class, () -> {
            Video.newVideo("1", null, null);
        });
        assertEquals("Video should contain path", exception.getMessage());
    }

    @Test
    void testIsExtensionValid() {
        Video video = Video.newVideo("1", null, "path/to/video.mp4");
        assertDoesNotThrow(video::isExtensionValid);
    }

    @Test
    void testIsExtensionInvalid() {
        Exception exception = assertThrows(DomainArgumentException.class, () -> {
            Video.newVideo("1", null, "path/to/video.mkv");
        });
        assertEquals("Video format is invalid. It should be mp4.", exception.getMessage());
    }

    @Test
    void testSetVideoStatusToProcessedSuccess() {
        Video video = Video.newVideo("1", null, "path/to/video.mp4");
        Video processedVideo = video.setVideoStatusToProcessedSuccess();
        assertEquals(StatusVideo.PROCESSED_OK, processedVideo.getStatus());
    }

    @Test
    void testSetVideoStatusToProcessedError() {
        Video video = Video.newVideo("1", null, "path/to/video.mp4");
        Video errorVideo = video.setVideoStatusToProcessedError();
        assertEquals(StatusVideo.PROCESSED_ERROR, errorVideo.getStatus());
    }

    @Test
    void testSetVideoStatusToInProcess() {
        Video video = Video.newVideo("1", null, "path/to/video.mp4");
        Video inProcessVideo = video.setVideoStatusToInProcess();
        assertEquals(StatusVideo.IN_PROCESS, inProcessVideo.getStatus());
    }

    @Test
    void testSetVideoFrameFilePath() {
        Video video = Video.newVideo("1", null, "path/to/video.mp4");
        Video updatedVideo = video.setVideoFrameFilePath("path/to/frames");
        assertEquals("path/to/frames", updatedVideo.getFramesFilePath());
    }

    @Test
    void testSetVideoFrameFilePathNull() {
        Video video = Video.newVideo("1", null, "path/to/video.mp4");
        Exception exception = assertThrows(DomainArgumentException.class, () -> {
            video.setVideoFrameFilePath(null);
        });
        assertEquals("Video should contain framesFilePath", exception.getMessage());
    }

    @Test
    void testToString() {
        Video video = Video.newVideo("1", null, "path/to/video.mp4");
        String expectedString = "Video[id=1, status=TO_BE_PROCESSED, path=path/to/video.mp4]";
        assertEquals(expectedString, video.toString());
    }

    @Test
    void testGetVideoJson() {
        Video video = Video.newVideo("1", null, "path/to/video.mp4");
        JSONObject expectedJson = new JSONObject();
        expectedJson.put("id", "1");
        expectedJson.put("status", "TO_BE_PROCESSED");
        expectedJson.put("path", "path/to/video.mp4");
        expectedJson.put("framesFilePath", "");

        assertEquals(expectedJson.toString(), video.getVideoJson());
    }
}