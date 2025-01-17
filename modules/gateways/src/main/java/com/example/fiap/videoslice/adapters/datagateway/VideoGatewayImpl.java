package com.example.fiap.videoslice.adapters.datagateway;

import com.example.fiap.videoslice.domain.datagateway.VideoGateway;
import com.example.fiap.videoslice.domain.datasource.VideoDataSource;
import com.example.fiap.videoslice.domain.datasource.VideoFileStoreDataSource;
import com.example.fiap.videoslice.domain.entities.Video;
import com.example.fiap.videoslice.domain.exception.ApplicationException;

import java.io.File;


public class VideoGatewayImpl implements VideoGateway {
    private final VideoDataSource videoDataSource;
    private final VideoFileStoreDataSource videoFileStoreDataSource;

    public VideoGatewayImpl(VideoDataSource videoDataSource, VideoFileStoreDataSource videoFileStoreDataSource) {
        this.videoDataSource = videoDataSource;
        this.videoFileStoreDataSource = videoFileStoreDataSource;
    }

    @Override
    public File videoSlice(Video video, int periodInSeconds) throws ApplicationException {
        return videoDataSource.videoSlice(video, periodInSeconds);
    }

    @Override
    public String saveFile(File file) {
        return this.videoFileStoreDataSource.saveFile(file);
    }


}
