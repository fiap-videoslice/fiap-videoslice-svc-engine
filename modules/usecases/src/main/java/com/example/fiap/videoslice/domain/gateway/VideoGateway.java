package com.example.fiap.videoslice.domain.gateway;

import com.example.fiap.videoslice.domain.entities.Video;
import com.example.fiap.videoslice.domain.exception.ApplicationException;

import java.io.File;

public interface VideoGateway {
    File videoSlice(Video video, int periodInSeconds) throws ApplicationException;
    String saveFile(File file) throws ApplicationException;
    void deleteFile(String filePath);
}
