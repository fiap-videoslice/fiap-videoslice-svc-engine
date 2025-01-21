package com.example.fiap.videoslice.adapters.messaging;

import com.example.fiap.videoslice.testUtils.StaticEnvironment;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.utility.DockerImageName;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.CreateQueueRequest;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.testcontainers.containers.localstack.LocalStackContainer.Service.SQS;

public class AwsSQSApiIT {
    private AwsSQSApi awsSQSApi;

    private static String videoStatusQueueUrl;
    private static String videosToBeProcessedQueueUrl;

    private static LocalStackContainer localstack;

    @BeforeAll
    static void beforeAll() {
        DockerImageName localstackImage = DockerImageName.parse("localstack/localstack:4.0");

        localstack = new LocalStackContainer(localstackImage).withServices(SQS);
        localstack.start();

        try (SqsClient sqsClient = SqsClient.builder()
                .region(Region.US_EAST_1)
                .endpointOverride(localstack.getEndpointOverride(SQS))
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(localstack.getAccessKey(), localstack.getSecretKey())
                        )
                )
                .build()) {

            videoStatusQueueUrl = sqsClient.createQueue(CreateQueueRequest.builder().queueName("videoStatus").build()).queueUrl();
            videosToBeProcessedQueueUrl = sqsClient.createQueue(CreateQueueRequest.builder().queueName("videosToBeProcessed").build()).queueUrl();

            try {
                Thread.sleep(500L);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    @AfterAll
    static void afterAll() {
        localstack.stop();
    }

    @BeforeEach
    void setUp() {

        System.setProperty("aws.accessKeyId", localstack.getAccessKey());
        System.setProperty("aws.secretAccessKey", localstack.getSecretKey());

        Map<String, String> env = new HashMap<>();
        env.put("videoslice.integration.sqs.sqsEndpoint", localstack.getEndpointOverride(SQS).toString());
        env.put("videoslice.integration.sqs.videoStatusQueueName", "videoStatus");
        env.put("videoslice.integration.sqs.videoStatusQueueUrl", videoStatusQueueUrl);
        env.put("videoslice.integration.sqs.videosToBeProcessedQueueName", "videosToBeProcessed");
        env.put("videoslice.integration.sqs.videosToBeProcessedQueueUrl", videosToBeProcessedQueueUrl);

        awsSQSApi = new AwsSQSApi(new StaticEnvironment(env));

    }


    @Test
    public void queueExecutionValidation() throws InterruptedException {
        String testMessage1 = "Teste_1_" + System.currentTimeMillis();
        String testMessage2 = "Teste_2_" + System.currentTimeMillis();

        try (SqsClient sqsClient = SqsClient.builder()
                .region(Region.US_EAST_1)
                .endpointOverride(localstack.getEndpointOverride(SQS))
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(localstack.getAccessKey(), localstack.getSecretKey())
                        )
                )
                .build()) {

            for (String testMessage : List.of(testMessage1, testMessage2)) {
                SendMessageRequest sendMessageRequest = SendMessageRequest.builder()
                        .queueUrl(videosToBeProcessedQueueUrl)
                        .messageBody(testMessage)
                        .build();

                sqsClient.sendMessage(sendMessageRequest);
            }
            Thread.sleep(100L);
        }

        List<Message> messages = awsSQSApi.receiveMessages(videosToBeProcessedQueueUrl);

        assertThat(messages).hasSize(2);

        List<String> messageContents = messages.stream().map(Message::body).toList();
        assertThat(messageContents).containsExactlyInAnyOrder(testMessage1, testMessage2);

        messages.forEach(message -> awsSQSApi.deleteMessageFromQueue(videosToBeProcessedQueueUrl, message));
    }
}