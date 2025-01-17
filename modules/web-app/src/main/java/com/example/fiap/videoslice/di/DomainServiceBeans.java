package com.example.fiap.videoslice.di;

import com.example.fiap.videoslice.adapters.datagateway.VideoGatewayImpl;
import com.example.fiap.videoslice.adapters.datasource.VideoSliceRepositoryImpl;
import com.example.fiap.videoslice.adapters.messaging.VideoEventMessagingGatewayImpl;
import com.example.fiap.videoslice.domain.datagateway.VideoEventMessagingGateway;
import com.example.fiap.videoslice.domain.datagateway.VideoGateway;
import com.example.fiap.videoslice.domain.datasource.VideoDataSource;
import com.example.fiap.videoslice.domain.datasource.VideoFileStoreDataSource;
import com.example.fiap.videoslice.domain.messaging.VideoStatusEventMessaging;
import com.example.fiap.videoslice.domain.usecases.VideoUseCases;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DomainServiceBeans {

    @Bean
    public VideoDataSource getDataSource() {
        return new VideoSliceRepositoryImpl();
    }

    @Bean
    public VideoGateway getVideoGateway(VideoDataSource videoDataSource, VideoFileStoreDataSource videoFileStoreDataSource) {
        return new VideoGatewayImpl(videoDataSource, videoFileStoreDataSource);
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
