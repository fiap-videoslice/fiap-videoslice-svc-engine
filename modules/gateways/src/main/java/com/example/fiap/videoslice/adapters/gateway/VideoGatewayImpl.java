package com.example.fiap.videoslice.adapters.gateway;

import com.example.fiap.videoslice.domain.gateway.VideoGateway;
import com.example.fiap.videoslice.domain.processor.VideoProcessor;
import com.example.fiap.videoslice.domain.processor.VideoFileStoreDataSource;
import com.example.fiap.videoslice.domain.entities.Video;
import com.example.fiap.videoslice.domain.exception.ApplicationException;

import java.io.File;


public class VideoGatewayImpl implements VideoGateway {
    private final VideoProcessor videoProcessor;
    private final VideoFileStoreDataSource videoFileStoreDataSource;

    public VideoGatewayImpl(VideoProcessor videoProcessor, VideoFileStoreDataSource videoFileStoreDataSource) {
        this.videoProcessor = videoProcessor;
        this.videoFileStoreDataSource = videoFileStoreDataSource;
    }

    @Override
    public File videoSlice(Video video, int periodInSeconds) throws ApplicationException {
        return videoProcessor.videoSlice(video, periodInSeconds);
    }

    @Override
    public String saveFile(File file) throws ApplicationException {
        return this.videoFileStoreDataSource.saveFile(file);
    }

    @Override
    public void deleteFile(String filePath) {
        this.videoFileStoreDataSource.deleteFile(filePath);
    }

}
