package com.example.fiap.videoslice.domain.datagateway;

import com.example.fiap.videoslice.domain.entities.Video;

public interface VideoEventMessagingGateway {
    void updateStatusVideo(Video video);

    void notifyErrorProcessingTheVideo(String message);
}
