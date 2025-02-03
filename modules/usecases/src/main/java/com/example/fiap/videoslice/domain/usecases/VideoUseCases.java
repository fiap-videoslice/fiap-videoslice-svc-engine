package com.example.fiap.videoslice.domain.usecases;

import com.example.fiap.videoslice.domain.entities.Video;
import com.example.fiap.videoslice.domain.exception.ApplicationException;
import com.example.fiap.videoslice.domain.gateway.VideoEventMessagingGateway;
import com.example.fiap.videoslice.domain.gateway.VideoGateway;
import com.example.fiap.videoslice.domain.valueobjects.StatusVideo;
import org.json.JSONObject;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;

public class VideoUseCases {

    private final VideoGateway videoGateway;
    private final VideoEventMessagingGateway videoEventMessagingGateway;

    public VideoUseCases(VideoGateway videoGateway,
                         VideoEventMessagingGateway videoEventMessagingGateway) {

        this.videoGateway = videoGateway;
        this.videoEventMessagingGateway = videoEventMessagingGateway;
    }


    @Transactional
    public String executeVideoSlice(Video video, Integer frameTime) {
        String uploadedFilePath = "";
        File file = null;
        try {
            video = video.setVideoStatusToInProcess();
            updateStatusVideo(video);

            file = extractFramesFromVideo(video, frameTime);
            uploadedFilePath = saveFile(file);

        } catch (ApplicationException e) {
            JSONObject messageJson = new JSONObject(video.getVideoJson());
            messageJson.put("status", StatusVideo.PROCESSED_ERROR.toString());
            messageJson.put("message", e.getMessage());
            notifyErrorProcessingTheVideo(messageJson.toString());
            return "error";
        }

        try {
            Video videoSuccess = video.setVideoFrameFilePath(file.getName()).setVideoStatusToProcessedSuccess();
            updateStatusVideo(videoSuccess);

        } catch (ApplicationException e) {
            deleteFile(uploadedFilePath);
            return "error";
        }

        return "success";
    }

    private File extractFramesFromVideo(Video video, int periodInSeconds) throws ApplicationException {
        return this.videoGateway.videoSlice(video, periodInSeconds);

    }

    private String saveFile(File file) throws ApplicationException {
        return this.videoGateway.saveFile(file);
    }

    public String updateStatusVideo(Video video) throws ApplicationException {
        this.videoEventMessagingGateway.updateStatusVideo(video);
        return "ok";
    }

    public void notifyErrorProcessingTheVideo(String message) {
        JSONObject messageJson = new JSONObject(message);
        messageJson.put("status", StatusVideo.PROCESSED_ERROR.toString());
        this.videoEventMessagingGateway.notifyErrorProcessingTheVideo(messageJson.toString());
    }

    public void deleteFile(String filePath) {
        this.videoGateway.deleteFile(filePath);

    }
}
