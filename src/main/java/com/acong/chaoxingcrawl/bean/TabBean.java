package com.acong.chaoxingcrawl.bean;

public class TabBean {
    private String tabURL;
    private String tabName;

    public String getTabURL() {
        return tabURL;
    }

    public void setTabURL(String tabURL) {
        this.tabURL = tabURL;
    }

    public String getTabName() {
        return tabName;
    }

    public void setTabName(String tabName) {
        this.tabName = tabName;
    }

    public TabBean(String tabURL, String tabName) {
        this.tabURL = tabURL;
        this.tabName = tabName;
    }
}
