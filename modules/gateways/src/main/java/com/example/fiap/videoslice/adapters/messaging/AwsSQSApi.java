package com.example.fiap.videoslice.adapters.messaging;

import com.example.fiap.videoslice.domain.exception.ApplicationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class AwsSQSApi {
    private static final Logger LOGGER = LoggerFactory.getLogger(AwsSQSApi.class);

    private String sqsEndpoint;
    private String videosToBeProcessedQueueName;
    private String videosToBeProcessedQueueUrl;
    private String videoStatusQueueName;
    private String videoStatusQueueUrl;

    @Autowired
    public AwsSQSApi(Environment environment) {
        this.sqsEndpoint = environment.getProperty("videoslice.integration.sqs.sqsEndpoint");
        this.videosToBeProcessedQueueName = environment.getProperty("videoslice.integration.sqs.videosToBeProcessedQueueName");
        this.videosToBeProcessedQueueUrl = environment.getProperty("videoslice.integration.sqs.videosToBeProcessedQueueUrl");
        this.videoStatusQueueName = environment.getProperty("videoslice.integration.sqs.videoStatusQueueName");
        this.videoStatusQueueUrl = environment.getProperty("videoslice.integration.sqs.videoStatusQueueUrl");

        this.sqsEndpoint = Objects.requireNonNull(sqsEndpoint, "videoslice.integration.sqs.sqsEndpoint not set");
        this.videosToBeProcessedQueueName = Objects.requireNonNull(videosToBeProcessedQueueName, "videoslice.integration.sqs.videosToBeProcessedQueueName not set");
        this.videosToBeProcessedQueueUrl = Objects.requireNonNull(videosToBeProcessedQueueUrl, "videoslice.integration.sqs.videosToBeProcessedQueueUrl not set");
        this.videoStatusQueueName = Objects.requireNonNull(videoStatusQueueName, "videoslice.integration.sqs.videoStatusQueueName not set");
        this.videoStatusQueueUrl = Objects.requireNonNull(videoStatusQueueUrl, "videoslice.integration.sqs.videoStatusQueueUrl not set");
    }

    public void sendMessage(String queueName, String queueUrl, String message) throws ApplicationException {
        try {
            SqsClient sqsClient = SqsClient.builder()
                    .region(Region.US_EAST_1)
                    .endpointOverride(URI.create(sqsEndpoint))
                    .build();

            // Create queue if it doesn't exist
            CreateQueueRequest createRequest = CreateQueueRequest.builder()
                    .queueName(queueName)
                    .build();
            sqsClient.createQueue(createRequest);

            // Send message
            SendMessageRequest sendMsgRequest = SendMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .messageBody(message)
                    .delaySeconds(5)
                    .build();
            sqsClient.sendMessage(sendMsgRequest);

            LOGGER.info("Body - " + message);
            LOGGER.info("Message sent successfully!");

        } catch (SqsException e) {
            LOGGER.error(e.getMessage());
            throw new ApplicationException("Error sending message to the queue");
//            System.exit(1);
        }
    }

    public List<Message> receiveMessages(String queueUrl) {

        LOGGER.info("\nReceive messages");

        SqsClient sqsClient = SqsClient.builder()
                .region(Region.US_EAST_1)
                .endpointOverride(URI.create(sqsEndpoint))
                .build();

        try {
            ReceiveMessageRequest receiveMessageRequest = ReceiveMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .maxNumberOfMessages(10)
                    .build();
            return sqsClient.receiveMessage(receiveMessageRequest).messages();

        } catch (SqsException e) {
            LOGGER.error(e.awsErrorDetails().errorMessage());
            System.exit(1);
            return new ArrayList<Message>();
        }

    }

    public void deleteMessageFromQueue(String queueUrl, Message message){

        LOGGER.info("Delete message - " + message.body());

        SqsClient sqsClient = SqsClient.builder()
                .region(Region.US_EAST_1)
                .endpointOverride(URI.create(sqsEndpoint))
                .build();

        String receiptHandle = message.receiptHandle();

        // Delete the message from the queue
        DeleteMessageRequest deleteMessageRequest = DeleteMessageRequest.builder()
                .queueUrl(queueUrl)
                .receiptHandle(receiptHandle)
                .build();
        sqsClient.deleteMessage(deleteMessageRequest);

        LOGGER.info("\nMessage deleted");
    }

    public String getVideosToBeProcessedQueueName() {
        return videosToBeProcessedQueueName;
    }

    public String getVideosToBeProcessedQueueUrl() {
        return videosToBeProcessedQueueUrl;
    }

    public String getVideoStatusQueueName() {
        return videoStatusQueueName;
    }

    public String getVideoStatusQueueUrl() {
        return videoStatusQueueUrl;
    }
}
