package com.example.fiap.videoslice.adapters.dto;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VideoDtoTest {

    private VideoDto videoDto;

    @BeforeEach
    void setUp() {
        videoDto = new VideoDto("1", "PROCESSING", "path/to/video.mp4", 10);
        videoDto.setMessage("Processing video");
    }

    @Test
    void testGetJson() {
        // Arrange
        String expectedJson = new JSONObject()
                .put("id", "1")
                .put("status", "PROCESSING")
                .put("path", "path/to/video.mp4")
                .put("message", "Processing video")
                .toString();

        // Act
        String actualJson = videoDto.getJson();

        // Assert
        assertEquals(expectedJson, actualJson);
    }

    @Test
    void testGetId() {
        // Act
        String id = videoDto.getId();

        // Assert
        assertEquals("1", id);
    }

    @Test
    void testSetId() {
        // Act
        videoDto.setId("2");

        // Assert
        assertEquals("2", videoDto.getId());
    }

    @Test
    void testGetStatus() {
        // Act
        String status = videoDto.getStatus();

        // Assert
        assertEquals("PROCESSING", status);
    }

    @Test
    void testSetStatus() {
        // Act
        videoDto.setStatus("COMPLETED");

        // Assert
        assertEquals("COMPLETED", videoDto.getStatus());
    }

    @Test
    void testGetPath() {
        // Act
        String path = videoDto.getPath();

        // Assert
        assertEquals("path/to/video.mp4", path);
    }

    @Test
    void testSetPath() {
        // Act
        videoDto.setPath("new/path/to/video.mp4");

        // Assert
        assertEquals("new/path/to/video.mp4", videoDto.getPath());
    }

    @Test
    void testGetTimeFrame() {
        // Act
        Integer timeFrame = videoDto.getTimeFrame();

        // Assert
        assertEquals(10, timeFrame);
    }

    @Test
    void testSetTimeFrame() {
        // Act
        videoDto.setTimeFrame(20);

        // Assert
        assertEquals(20, videoDto.getTimeFrame());
    }

    @Test
    void testGetMessage() {
        // Act
        String message = videoDto.getMessage();

        // Assert
        assertEquals("Processing video", message);
    }

    @Test
    void testSetMessage() {
        // Act
        videoDto.setMessage("Video processed successfully");

        // Assert
        assertEquals("Video processed successfully", videoDto.getMessage());
    }

    @Test
    void testDefaultConstructor() {
        // Act
        VideoDto defaultVideoDto = new VideoDto();

        // Assert
        assertNotNull(defaultVideoDto);
        assertNull(defaultVideoDto.getId());
        assertNull(defaultVideoDto.getStatus());
        assertNull(defaultVideoDto.getPath());
        assertNull(defaultVideoDto.getTimeFrame());
        assertNull(defaultVideoDto.getMessage());
    }
}