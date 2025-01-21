package com.example.fiap.videoslice.domain.gateway;

import com.example.fiap.videoslice.domain.entities.Video;
import com.example.fiap.videoslice.domain.exception.ApplicationException;

public interface VideoEventMessagingGateway {
    void updateStatusVideo(Video video) throws ApplicationException;

    void notifyErrorProcessingTheVideo(String message);
}
