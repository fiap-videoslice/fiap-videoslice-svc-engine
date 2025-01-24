package com.example.fiap.videoslice.adapters.messaging;

import com.example.fiap.videoslice.domain.entities.Video;
import com.example.fiap.videoslice.domain.exception.ApplicationException;
import com.example.fiap.videoslice.domain.messaging.VideoStatusEventMessaging;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

/**
 * Implementation of the Repository based on AWS SQS service
 */
@Repository
public class VideoStatusAwsSQS implements VideoStatusEventMessaging {

    private static final Logger LOGGER = LoggerFactory.getLogger(VideoStatusAwsSQS.class);
    private final AwsSQSApi awsSQSApi;

    public VideoStatusAwsSQS(AwsSQSApi awsSQSApi) {
        this.awsSQSApi = awsSQSApi;
    }

    @Override
    public void updateStatusVideo(Video video) throws ApplicationException {
        awsSQSApi.sendMessage(awsSQSApi.getVideoStatusQueueName(), awsSQSApi.getVideoStatusQueueUrl(), video.getVideoJson());
    }

    @Override
    public void notifyErrorProcessingTheVideo(String message) {
        try {
            awsSQSApi.sendMessage(awsSQSApi.getVideoStatusQueueName(), awsSQSApi.getVideoStatusQueueUrl(), message);
        }catch(ApplicationException e) {
            LOGGER.error(e.getMessage());
        }
    }
}
