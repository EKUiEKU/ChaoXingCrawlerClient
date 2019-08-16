package com.acong.chaoxingcrawl.bean;


import org.jetbrains.annotations.NotNull;

public class UserInfo {
    private String username;
    private String password;
    private String courseName;
    private String school;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public UserInfo(@NotNull String school, @NotNull String username, @NotNull String password, @NotNull String courseName) {
        this.username = username;
        this.password = password;
        this.courseName = courseName;
        this.school = school;
    }

    public UserInfo(){

    }
}
