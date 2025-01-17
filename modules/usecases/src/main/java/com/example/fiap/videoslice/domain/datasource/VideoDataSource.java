package com.example.fiap.videoslice.domain.datasource;

import com.example.fiap.videoslice.domain.entities.Video;
import com.example.fiap.videoslice.domain.exception.ApplicationException;

import java.io.File;

public interface VideoDataSource {
    File videoSlice(Video video, int periodInSeconds) throws ApplicationException;
}
