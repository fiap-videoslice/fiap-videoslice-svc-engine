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
        System.out.println("executeVideoSlice 1");
        String uploadedFilePath = "";
        File file = null;
        try {
            video = video.setVideoStatusToInProcess();
            System.out.println("executeVideoSlice - Atualizar updateStatusVideo");
            updateStatusVideo(video);
            System.out.println("executeVideoSlice - extractFramesFromVideo");
            file = extractFramesFromVideo(video, frameTime);
            System.out.println("executeVideoSlice - saveFile");
            uploadedFilePath = saveFile(file);
            System.out.println("executeVideoSlice - Fase 1 ok");
        } catch (ApplicationException e) {
            System.out.println("Erro Fase 1 - " + e.toString());
            JSONObject messageJson = new JSONObject(video.getVideoJson());
            messageJson.put("status", StatusVideo.PROCESSED_ERROR.toString());
            messageJson.put("message", e.getMessage());
            notifyErrorProcessingTheVideo(messageJson.toString());
            return "error";
        }

        try {
            System.out.println("Fase 2 - setVideoFrameFilePath");
            Video videoSuccess = video.setVideoFrameFilePath(file.getName()).setVideoStatusToProcessedSuccess();
            System.out.println("Fase 2 - updateStatusVideo");
            updateStatusVideo(videoSuccess);

        } catch (ApplicationException e) {
            System.out.println("Erro Fase 2 - " + e.toString());
            deleteFile(uploadedFilePath);
            return "error";
        }
        System.out.println("executeVideoSlice - Fase 2 ok");
        return "success";
    }

    private File extractFramesFromVideo(Video video, int periodInSeconds) throws ApplicationException {
        System.out.println("Method - extractFramesFromVideo");
        return this.videoGateway.videoSlice(video, periodInSeconds);

    }

    private String saveFile(File file) throws ApplicationException {
        System.out.println("Method - saveFile");
        return this.videoGateway.saveFile(file);
    }

    public String updateStatusVideo(Video video) throws ApplicationException {
        System.out.println("Method - updateStatusVideo");
        this.videoEventMessagingGateway.updateStatusVideo(video);
        return "ok";
    }

    public void notifyErrorProcessingTheVideo(String message) {
        System.out.println("Method - notifyErrorProcessingTheVideo");
        JSONObject messageJson = new JSONObject(message);
        messageJson.put("status", StatusVideo.PROCESSED_ERROR.toString());
        this.videoEventMessagingGateway.notifyErrorProcessingTheVideo(messageJson.toString());
    }

    public void deleteFile(String filePath) {
        System.out.println("Method - deleteFile");
        this.videoGateway.deleteFile(filePath);

    }

}
