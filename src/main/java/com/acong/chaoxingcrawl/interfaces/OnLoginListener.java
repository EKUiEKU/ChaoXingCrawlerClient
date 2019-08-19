package com.acong.chaoxingcrawl.interfaces;

public interface OnLoginListener{
    void onLoginSuccess(Long uid);
    void onLoginFailure(String causeBy);
}