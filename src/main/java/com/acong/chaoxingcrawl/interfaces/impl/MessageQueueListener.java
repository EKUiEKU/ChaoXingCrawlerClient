package com.acong.chaoxingcrawl.interfaces.impl;

import com.acong.chaoxingcrawl.bean.ClazzBean;
import com.acong.chaoxingcrawl.bean.Progress;
import com.acong.chaoxingcrawl.interfaces.OnMessageQueueListener;

public class MessageQueueListener implements OnMessageQueueListener {
    public void onMessage(String msg) {

    }

    public void onClassInfo(ClazzBean info) {

    }

    public void onLoginFailure() {

    }

    public void onLoginSuccess(String name) {

    }

    public void onCourseName(String courseName) {

    }

    public void onCourseNoFound(String courseName) {

    }

    public void onStartClass(String className) {

    }

    public void onPlaying(String type) {

    }

    public void onPlayingVideoProgress(Progress p) {

    }

    public void onPlayingPPTProgress(String pageAt) {

    }

    public void onExceptiom() {

    }

    public void onCourseStart() {

    }

    public void onCourseCompleted() {

    }

    public void onClassCompleted(String className) {

    }

    public void onSchoolURLFound(String url) {

    }

    public void onSchoolURLNOTFound(String causeBy) {

    }
}
