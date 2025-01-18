package com.example.fiap.videoslice.domain.entities;

import com.example.fiap.videoslice.domain.exception.DomainArgumentException;
import com.example.fiap.videoslice.domain.valueobjects.StatusVideo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

public class Video {

    private final @NotNull String id;
    private final @NotNull StatusVideo status;
    private final @NotNull String path;
    private final @NotNull String framesFilePath;

    private Video(@NotNull String id, @NotNull StatusVideo status, @NotNull String path, @Nullable String framesFilePath) {
        this.id = id;
        this.status = status;
        this.path = path;
        this.framesFilePath = framesFilePath;

        isExtensionValid();
    }

    public static Video newVideo(@NotNull String id, @Nullable StatusVideo status, @NotNull String path) {

        if (id == null) {
            throw new DomainArgumentException("Video should contain id");
        }

        if (path == null) {
            throw new DomainArgumentException("Video should contain path");
        }
        return new Video(id, StatusVideo.TO_BE_PROCESSED, path, "");
    }

    public void isExtensionValid() {
        if (!this.path.toLowerCase().endsWith(".mp4")) {

            throw new DomainArgumentException("Video format is invalid. It should be mp4.");
        }
    }

    public Video setVideoStatusToProcessedSuccess() {
        if (status == null) {
            throw new DomainArgumentException("Video should contain status");
        }
        return new Video(id, StatusVideo.PROCESSED_OK, path, framesFilePath);
    }

    public Video setVideoStatusToProcessedError() {
        if (status == null) {
            throw new DomainArgumentException("Error processing the file");
        }
        return new Video(id, StatusVideo.PROCESSED_ERROR, path, framesFilePath);
    }

    public Video setVideoStatusToInProcess() {
        if (status == null) {
            throw new DomainArgumentException("Video should contain status");
        }
        return new Video(id, StatusVideo.IN_PROCESS, path, framesFilePath);
    }

    public Video setVideoFrameFilePath(String framesFilePath) {
        if (framesFilePath == null) {
            throw new DomainArgumentException("Video should contain framesFilePath");
        }
        return new Video(id, status, path, framesFilePath);
    }

    public String getId() {
        return id;
    }

    public StatusVideo getStatus() {
        return status;
    }

    public String getPath() {
        return path;
    }

    public String getFramesFilePath() {
        return framesFilePath;
    }

    @Override
    public String toString() {
        return "Video[" +
                "id=" + id + ", " +
                "status=" + status + ", " +
                "path=" + path + ']';
    }

    public String getVideoJson() {

        JSONObject jo = new JSONObject();
        jo.put("id", id);
        jo.put("status", status.toString());
        jo.put("path", path);
        jo.put("framesFilePath", framesFilePath);

        return jo.toString();
    }
}
