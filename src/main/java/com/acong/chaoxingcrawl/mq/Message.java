package com.acong.chaoxingcrawl.mq;

import com.acong.chaoxingcrawl.bean.UserInfo;

import java.util.List;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;


public class Message implements Delayed {

    public int what;

    public UserInfo arg1;

    public Object arg2;

    public Object obj;

    //微秒
    long when;

    List data;

    Handler target;

    Runnable callback;

    Message next;


    //使用消息池，实现反复利用
    private static Object POOL_LOCK = new Object();

    private static final int MAX_POOL_SIZE = 50;

    private static int poolSize = 0;

    private static Message sPool;



    public Message() {
    }

    public static Message obtain(Handler target) {
        Message msg = obtain();
        msg.target = target;

        return msg;
    }

    public static Message obtain() {
        if (sPool != null) {
            synchronized (POOL_LOCK) {
                Message m = sPool;
                sPool = sPool.next;
                m.next = null;
                poolSize--;
                return m;
            }
        }

        return new Message();
    }

    public void recyle() {
        what = 0;
        arg1 = null;
        arg2 = null;
        obj = null;
        data = null;
        target = null;
        callback = null;
        next = null;

        synchronized(POOL_LOCK) {
            if (poolSize < MAX_POOL_SIZE) {
                next = sPool;
                sPool = this;
                poolSize++;
            }

        }
    }



    public long getDelay(TimeUnit timeUnit) {//实现Delayed接口
        long result = timeUnit.convert((when - System.currentTimeMillis()) * 1000, TimeUnit.NANOSECONDS);
        //System.out.println("getDelay: result=" + result);
        return result;
    }

    public int compareTo(Delayed delayed) {//实现Comparable接口
        Message msg = (Message) delayed;
        if (this.when > msg.when) {
            return 1;
        } else if (this.when < msg.when){
            return -1;
        }
        return 0;
    }
}