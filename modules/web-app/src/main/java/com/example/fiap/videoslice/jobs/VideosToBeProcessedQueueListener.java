package com.example.fiap.videoslice.jobs;

import com.example.fiap.videoslice.adapters.dto.VideoDto;
import com.example.fiap.videoslice.adapters.messaging.AwsSQSApi;
import com.example.fiap.videoslice.adapters.storage.AwsS3Api;
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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class VideosToBeProcessedQueueListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(VideosToBeProcessedQueueListener.class);
    private final AwsSQSApi awsSQSApi;
    private final AwsS3Api awsS3Api;
    private VideoUseCases videoUseCases;

    public VideosToBeProcessedQueueListener(AwsSQSApi awsSQSApi, AwsS3Api awsS3Api, VideoUseCases videoUseCases) {
        this.awsSQSApi = awsSQSApi;
        this.awsS3Api = awsS3Api;
        this.videoUseCases = videoUseCases;
    }

    @Scheduled(fixedRate = 10000, initialDelay = 10000)
    public void processVideoQueueMessages() {
        LOGGER.debug("VideosToBeProcessedQueueListener - processVideoQueueMessages");

        String bucketName = awsS3Api.getBucketFullPath();

        List<Message> messages = awsSQSApi.receiveMessages(awsSQSApi.getVideosToBeProcessedQueueUrl());

        List<CompletableFuture<Void>> futures = messages.stream()
                .map(message -> CompletableFuture.runAsync(() -> processMessage(message, bucketName)))
                .collect(Collectors.toList());

        // Wait for all futures to complete
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    }

    private void processMessage(Message message, String bucketName) {

        String messageBody = message.body();

        LOGGER.debug("Received message: " + messageBody);

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
            objectMapper.registerModule(new JavaTimeModule());

            VideoDto videoDto = objectMapper.readValue(messageBody, VideoDto.class);
            String path = "";

            if (videoDto.getPath() != null && !videoDto.equals("") && videoDto.getPath().startsWith("input"))
                path = bucketName + "/" + videoDto.getPath();
            else
                path = videoDto.getPath();

            Video videoEntity = Video.newVideo(videoDto.getId(), StatusVideo.TO_BE_PROCESSED, path);

            String confirmExecution = videoUseCases.executeVideoSlice(videoEntity, videoDto.getTimeFrame());

            if ((confirmExecution.equals("success")) || (confirmExecution.equals("error")))
                awsSQSApi.deleteMessageFromQueue(awsSQSApi.getVideosToBeProcessedQueueName(), message);

        } catch (JsonProcessingException e) {
            handleProcessingException(message, messageBody, e.getMessage());
        } catch (DomainArgumentException er) {
            handleProcessingException(message, messageBody, er.getMessage());
        }
    }

    private void handleProcessingException(Message message, String messageBody, String errorMessage) {
        JSONObject messageJson = new JSONObject(messageBody);
        messageJson.put("message", errorMessage);
        videoUseCases.notifyErrorProcessingTheVideo(messageJson.toString());
        awsSQSApi.deleteMessageFromQueue(awsSQSApi.getVideosToBeProcessedQueueName(), message);
        LOGGER.error(errorMessage);
    }






//    @Scheduled(fixedRate = 10000, initialDelay = 10000)
//    public void processVideoQueueMessages() {
//        LOGGER.debug("VideosToBeProcessedQueueListener - processVideoQueueMessages");
//
//        String bucketName = awsS3Api.getBucketFullPath();
//
//        List<Message> messages = awsSQSApi.receiveMessages(awsSQSApi.getVideosToBeProcessedQueueUrl());
//
//        for (Message message : messages) {
//            String messageBody = message.body();
//            LOGGER.debug("Received message: " + messageBody);
//
//            try {
//                ObjectMapper objectMapper = new ObjectMapper();
//                objectMapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
//                objectMapper.registerModule(new JavaTimeModule());
//
//                VideoDto videoDto = objectMapper.readValue(messageBody, VideoDto.class);
//                String path = "";
//
//                if (videoDto.getPath() != null && !videoDto.equals("") && videoDto.getPath().startsWith("input"))
//                    path = bucketName + "/" + videoDto.getPath();
//                else
//                    path = videoDto.getPath();
//
//                Video videoEntity = Video.newVideo(videoDto.getId(), StatusVideo.TO_BE_PROCESSED, path);
//
//                String confirmExecution = videoUseCases.executeVideoSlice(videoEntity, videoDto.getTimeFrame());
//
//                if ((confirmExecution.equals("success")) || (confirmExecution.equals("error")))
//                    awsSQSApi.deleteMessageFromQueue(awsSQSApi.getVideosToBeProcessedQueueName(), message);
//
//            } catch (JsonProcessingException e) {
//                JSONObject messageJson = new JSONObject(messageBody);
//                messageJson.put("message", e.getMessage());
//                videoUseCases.notifyErrorProcessingTheVideo(messageJson.toString());
//                awsSQSApi.deleteMessageFromQueue(awsSQSApi.getVideosToBeProcessedQueueName(), message);
//                LOGGER.error(e.getMessage());
//
//            } catch (DomainArgumentException er) {
//                JSONObject messageJson = new JSONObject(messageBody);
//                messageJson.put("message", er.getMessage());
//                videoUseCases.notifyErrorProcessingTheVideo(messageJson.toString());
//                awsSQSApi.deleteMessageFromQueue(awsSQSApi.getVideosToBeProcessedQueueName(), message);
//                LOGGER.error(er.getMessage());
//            }
//        }
//
//    }
}