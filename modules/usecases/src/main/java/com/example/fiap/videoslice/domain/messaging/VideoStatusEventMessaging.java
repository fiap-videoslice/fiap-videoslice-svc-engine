package com.example.fiap.videoslice.domain.messaging;

import com.example.fiap.videoslice.domain.entities.Video;
import com.example.fiap.videoslice.domain.exception.ApplicationException;

public interface VideoStatusEventMessaging {
    void updateStatusVideo(Video video) throws ApplicationException;
    void notifyErrorProcessingTheVideo(String message);
}
