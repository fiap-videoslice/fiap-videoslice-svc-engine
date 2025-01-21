package com.example.fiap.videoslice.domain.processor;

import com.example.fiap.videoslice.domain.exception.ApplicationException;

import java.io.File;

public interface VideoFileStoreDataSource {
    String saveFile(File file) throws ApplicationException;
    String getBucketFullPath();

    void deleteFile(String filePath);
}
