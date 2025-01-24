package com.example.fiap.videoslice.adapters.messaging;


import com.example.fiap.videoslice.testUtils.StaticEnvironment;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThatThrownBy;


@ExtendWith(MockitoExtension.class)
public class AwsSQSApiTest {

    @Test
    void validarVariaveisDeAmbiente() {
        Map<String, String> env = new HashMap<>();

        assertThatThrownBy(() -> {
            new AwsSQSApi(new StaticEnvironment(env));
        }).hasMessageContaining("videoslice.integration.sqs.sqsEndpoint not set");

        env.put("videoslice.integration.sqs.sqsEndpoint", "http://localhost:4566");

        assertThatThrownBy(() -> {
            new AwsSQSApi(new StaticEnvironment(env));
        }).hasMessageContaining("videoslice.integration.sqs.videosToBeProcessedQueueName not set");

        env.put("videoslice.integration.sqs.videosToBeProcessedQueueName", "videosToBeProcessed");

        assertThatThrownBy(() -> {
            new AwsSQSApi(new StaticEnvironment(env));
        }).hasMessageContaining("videoslice.integration.sqs.videosToBeProcessedQueueUrl not set");

        env.put("videoslice.integration.sqs.videosToBeProcessedQueueUrl", "http://localhost:4566/000000000000/videosToBeProcessed");

        assertThatThrownBy(() -> {
            new AwsSQSApi(new StaticEnvironment(env));
        }).hasMessageContaining("videoslice.integration.sqs.videoStatusQueueName not set");

        env.put("videoslice.integration.sqs.videoStatusQueueName", "videoStatus");

        assertThatThrownBy(() -> {
            new AwsSQSApi(new StaticEnvironment(env));
        }).hasMessageContaining("videoslice.integration.sqs.videoStatusQueueUrl not set");

        env.put("videoslice.integration.sqs.videoStatusQueueUrl", "http://localhost:4566/000000000000/videoStatus");

        AwsSQSApi awsSQSApi = new AwsSQSApi(new StaticEnvironment(env));
        Assertions.assertThat(awsSQSApi.getVideoStatusQueueName()).isEqualTo("videoStatus");
        Assertions.assertThat(awsSQSApi.getVideoStatusQueueUrl()).isEqualTo("http://localhost:4566/000000000000/videoStatus");
        Assertions.assertThat(awsSQSApi.getVideosToBeProcessedQueueName()).isEqualTo("videosToBeProcessed");
        Assertions.assertThat(awsSQSApi.getVideosToBeProcessedQueueUrl()).isEqualTo("http://localhost:4566/000000000000/videosToBeProcessed");
    }


}
