package com.example.fiap.videoslice.adapters.dto;

import org.json.JSONObject;

public class VideoDto {
    private String id;
    private String status;
    private String path;
    private Integer timeFrame;
    private String message;

    public VideoDto(){

    }

    public VideoDto(String id, String status, String path, Integer timeFrame) {
        this.id = id;
        this.status = status;
        this.path = path;
        this.timeFrame = timeFrame;
    }

    public String getJson() {
        JSONObject jo = new JSONObject();
        jo.put("id", id);
        jo.put("status", status.toString());
        jo.put("path", path);
        jo.put("message", message);

        return jo.toString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Integer getTimeFrame() {
        return timeFrame;
    }

    public void setTimeFrame(Integer timeFrame) {
        this.timeFrame = timeFrame;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
