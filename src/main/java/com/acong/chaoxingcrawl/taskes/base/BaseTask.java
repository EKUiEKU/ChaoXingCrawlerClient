package com.acong.chaoxingcrawl.taskes.base;

import com.acong.chaoxingcrawl.ChaoXingTaskExecutor;
import com.acong.chaoxingcrawl.bean.UserInfo;
import com.acong.chaoxingcrawl.mq.Handler;
import com.acong.chaoxingcrawl.mq.Message;

public class BaseTask implements Runnable{
    private Handler handler;
    public BaseTask(){
        handler = ChaoXingTaskExecutor.getInstance().getHandler();
    }

    /**
     *
     * @param code    message的响应码
     * @param b       message的obj
     * @param arg1    调用者
     */
    protected void sendMessage(int code,Object b,Object arg1,Object arg2){
        Message msg = new Message();
        msg.what = code;
        msg.obj = b;
        msg.arg1 = (UserInfo) arg1;
        msg.arg2 = arg2;
        handler.dispatchMessage(msg);
    }

    protected void sendMessage(int code,Object b,Object arg1){
        sendMessage(code,b,arg1,null);
    }

    protected void sendMessage(int code,Object b){
        sendMessage(code,b,null);
    }

    protected void sendMessage(int code){
        sendMessage(code,null);
    }

    public void run() {

    }
}
