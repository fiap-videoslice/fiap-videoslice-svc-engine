package com.example.fiap.videoslice.jobs;

import com.example.fiap.videoslice.adapters.dto.VideoDto;
import com.example.fiap.videoslice.adapters.messaging.AwsSQSApi;
import com.example.fiap.videoslice.domain.entities.Video;
import com.example.fiap.videoslice.domain.exception.DomainArgumentException;
import com.example.fiap.videoslice.domain.usecases.VideoUseCases;
import com.example.fiap.videoslice.domain.valueobjects.StatusVideo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.model.Message;

import java.util.List;

@Service
public class VideoToBeProcessedListenerAwsSQS {
    private static final Logger LOGGER = LoggerFactory.getLogger(VideoToBeProcessedListenerAwsSQS.class);
    private final AwsSQSApi awsSQSApi;
    private VideoUseCases videoUseCases;

    public VideoToBeProcessedListenerAwsSQS(AwsSQSApi awsSQSApi, VideoUseCases videoUseCases) {

        this.awsSQSApi = awsSQSApi;
        this.videoUseCases = videoUseCases;
    }

    @Scheduled(fixedRate = 10000, initialDelay = 10000)
    public void processVideoQueueMessages() {
        LOGGER.debug("VideoToBeProcessedListenerAwsSQS - processVideoQueueMessages");


//        try {
        List<Message> messages = awsSQSApi.receiveMessages(awsSQSApi.getVideosToBeProcessedQueueUrl());


        for (Message message : messages) {
            String messageBody = message.body();
            LOGGER.debug("Received message: " + messageBody);


            try {
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
                objectMapper.registerModule(new JavaTimeModule());

                // Deserializar o JSON para o objeto Pedido
                VideoDto videoDto = objectMapper.readValue(messageBody, VideoDto.class);

                Video videoEntity = Video.newVideo(videoDto.getId(), StatusVideo.TO_BE_PROCESSED, videoDto.getPath());

                String confirmExecution = videoUseCases.executeVideoSlice(videoEntity, videoDto.getTimeFrame());

                if ((confirmExecution.equals("success")) || (confirmExecution.equals("error")))
                    awsSQSApi.deleteMessageFromQueue(awsSQSApi.getVideosToBeProcessedQueueName(), message);

            } catch (JsonProcessingException e) {
                JSONObject messageJson = new JSONObject(messageBody);
                messageJson.put("message", e.getMessage());
                videoUseCases.notifyErrorProcessingTheVideo(messageJson.toString());
                awsSQSApi.deleteMessageFromQueue(awsSQSApi.getVideosToBeProcessedQueueName(), message);
                LOGGER.error(e.getMessage());

            } catch (DomainArgumentException er) {
                JSONObject messageJson = new JSONObject(messageBody);
                messageJson.put("message", er.getMessage());
                videoUseCases.notifyErrorProcessingTheVideo(messageJson.toString());
                awsSQSApi.deleteMessageFromQueue(awsSQSApi.getVideosToBeProcessedQueueName(), message);
                LOGGER.error(er.getMessage());
            }
        }

    }
}