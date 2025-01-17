package com.example.fiap.videoslice.adapters.messaging;

import com.example.fiap.videoslice.domain.datagateway.VideoEventMessagingGateway;
import com.example.fiap.videoslice.domain.entities.Video;
import com.example.fiap.videoslice.domain.messaging.VideoStatusEventMessaging;


public class VideoEventMessagingGatewayImpl implements VideoEventMessagingGateway {

    private final VideoStatusEventMessaging videoStatusEventMessaging;

    public VideoEventMessagingGatewayImpl(VideoStatusEventMessaging videoStatusEventMessaging) {
        this.videoStatusEventMessaging = videoStatusEventMessaging;
    }

    @Override
    public void updateStatusVideo(Video video) {
        this.videoStatusEventMessaging.updateStatusVideo(video);
    }

    @Override
    public void notifyErrorProcessingTheVideo(String message){
        this.videoStatusEventMessaging.notifyErrorProcessingTheVideo(message);
    }
}
