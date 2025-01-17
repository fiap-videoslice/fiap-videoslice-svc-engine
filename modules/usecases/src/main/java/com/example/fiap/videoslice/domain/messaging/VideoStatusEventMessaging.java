package com.example.fiap.videoslice.domain.messaging;

import com.example.fiap.videoslice.domain.entities.Video;

public interface VideoStatusEventMessaging {
    void updateStatusVideo(Video video);
    void notifyErrorProcessingTheVideo(String message);
}
