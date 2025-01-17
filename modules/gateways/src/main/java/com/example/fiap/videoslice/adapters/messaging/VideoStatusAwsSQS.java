package com.example.fiap.videoslice.adapters.messaging;

import com.example.fiap.videoslice.domain.entities.Video;
import com.example.fiap.videoslice.domain.messaging.VideoStatusEventMessaging;
import org.json.JSONObject;
import org.springframework.stereotype.Repository;

/**
 * Implementation of the Repository based on AWS SQS service
 */
@Repository
public class VideoStatusAwsSQS implements VideoStatusEventMessaging {

    private final AwsSQSApi awsSQSApi;

    public VideoStatusAwsSQS(AwsSQSApi awsSQSApi) {
        this.awsSQSApi = awsSQSApi;
    }

    @Override
    public void updateStatusVideo(Video video) {
        awsSQSApi.sendMessage(awsSQSApi.getVideoStatusQueueName(), awsSQSApi.getVideoStatusQueueUrl(), video.getVideoJson());
    }

    @Override
    public void notifyErrorProcessingTheVideo(String message){
        awsSQSApi.sendMessage(awsSQSApi.getVideoStatusQueueName(), awsSQSApi.getVideoStatusQueueUrl(), message);
    }
}
