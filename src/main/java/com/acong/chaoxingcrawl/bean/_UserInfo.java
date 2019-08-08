package com.acong.chaoxingcrawl.bean;

public class _UserInfo {

    /**
     * 账号的UID
     */
    Long uid;
    /**
     * 真实姓名
     */
    String name;
    /**
     * 学校
     */
    String school;
    /**
     * 学号
     */
    String studentid;
    /**
     * 上次登录的时间
     */
    Long lastTime;

    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getStudentid() {
        return studentid;
    }

    public void setStudentid(String studentid) {
        this.studentid = studentid;
    }

    public Long getLastTime() {
        return lastTime;
    }

    public void setLastTime(Long lastTime) {
        this.lastTime = lastTime;
    }

    public _UserInfo(){

    }

}
