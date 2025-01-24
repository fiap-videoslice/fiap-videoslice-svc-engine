package com.example.fiap.videoslice.di;

import com.example.fiap.videoslice.adapters.gateway.VideoGatewayImpl;
import com.example.fiap.videoslice.adapters.processor.VideoSliceProcessorImpl;
import com.example.fiap.videoslice.adapters.messaging.VideoEventMessagingGatewayImpl;
import com.example.fiap.videoslice.domain.gateway.VideoEventMessagingGateway;
import com.example.fiap.videoslice.domain.gateway.VideoGateway;
import com.example.fiap.videoslice.domain.processor.VideoProcessor;
import com.example.fiap.videoslice.domain.processor.VideoFileStoreDataSource;
import com.example.fiap.videoslice.domain.messaging.VideoStatusEventMessaging;
import com.example.fiap.videoslice.domain.usecases.VideoUseCases;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DomainServiceBeans {

    @Bean
    public VideoProcessor getDataSource() {
        return new VideoSliceProcessorImpl();
    }

    @Bean
    public VideoGateway getVideoGateway(VideoProcessor videoProcessor, VideoFileStoreDataSource videoFileStoreDataSource) {
        return new VideoGatewayImpl(videoProcessor, videoFileStoreDataSource);
    }

    @Bean
    public VideoEventMessagingGateway pagamentoEventMessagingGateway(VideoStatusEventMessaging pagamentoEventMessaging) {
        return new VideoEventMessagingGatewayImpl(pagamentoEventMessaging);
    }

    @Bean
    public VideoUseCases videoUseCases(VideoGateway videoGateway,
                                       VideoEventMessagingGateway videoEventMessagingGateway) {
        return new VideoUseCases(videoGateway, videoEventMessagingGateway);
    }

}
