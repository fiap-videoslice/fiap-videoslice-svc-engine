package com.example.fiap.videoslice.domain.datasource;

import java.io.File;

public interface VideoFileStoreDataSource {
    String saveFile(File file);
    String getBucketFullPath();
}
