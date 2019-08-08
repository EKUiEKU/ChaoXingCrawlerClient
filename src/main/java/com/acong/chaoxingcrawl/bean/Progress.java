package com.acong.chaoxingcrawl.bean;

public class Progress {
    private String currentTime;
    private String totalTime;

    public Progress(String currentTime, String totalTime) {
        this.currentTime = currentTime;
        this.totalTime = totalTime;
    }

    public String getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(String currentTime) {
        this.currentTime = currentTime;
    }

    public String getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(String totalTime) {
        this.totalTime = totalTime;
    }
}
