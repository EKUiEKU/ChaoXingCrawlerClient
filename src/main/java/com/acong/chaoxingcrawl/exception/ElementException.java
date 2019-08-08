package com.acong.chaoxingcrawl.exception;

public class ElementException extends Exception{
    public ElementException() {
        super("元素没有找到。");
    }

    public ElementException(String message) {
        super(message);
    }
}
