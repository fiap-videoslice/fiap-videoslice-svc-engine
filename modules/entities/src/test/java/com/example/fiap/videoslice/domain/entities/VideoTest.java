package com.example.fiap.videoslice.domain.entities;

import com.example.fiap.videoslice.domain.valueobjects.StatusVideo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class VideoTest {

    private Video video;

    @BeforeEach
    void setUp() {
        video = Video.newVideo("21", StatusVideo.TO_BE_PROCESSED, "/tmp/myvideo/myvideo123.mp4");
    }

    @Test
    void checkAttributes() {
        assertThat(video.getId()).isEqualTo("21");
        assertThat(video.getStatus()).isEqualTo(StatusVideo.TO_BE_PROCESSED);
        assertThat(video.getPath()).isEqualTo("/tmp/myvideo/myvideo123.mp4");
    }
}