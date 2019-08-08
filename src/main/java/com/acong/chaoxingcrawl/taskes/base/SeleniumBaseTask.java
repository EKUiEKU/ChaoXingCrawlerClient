package com.acong.chaoxingcrawl.taskes.base;

import org.openqa.selenium.WebDriver;

public class SeleniumBaseTask<T extends WebDriver> extends BaseTask{
    protected T driver;

    public SeleniumBaseTask(T d){
        driver = d;
        driver.manage().window().maximize();
    }

    @Override
    public void run() {
        super.run();
    }
}
