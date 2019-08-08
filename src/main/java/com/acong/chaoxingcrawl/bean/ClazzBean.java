package com.acong.chaoxingcrawl.bean;

public class ClazzBean {
    private String className;
    private String classURL;
    private Boolean isComplete;

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getClassURL() {
        return classURL;
    }

    public void setClassURL(String classURL) {
        this.classURL = classURL;
    }

    public Boolean getComplete() {
        return isComplete;
    }

    public void setComplete(Boolean complete) {
        isComplete = complete;
    }

    @Override
    public String toString() {
        return "[" + this.getClassName() + "]:\n    URL:"
                + this.getClassURL()
                + "\n    完成情况:" + (this.getComplete() ? " 已完成" : "未完成");
    }
}
