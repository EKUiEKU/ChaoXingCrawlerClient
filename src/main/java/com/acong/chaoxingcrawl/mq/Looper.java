package com.acong.chaoxingcrawl.mq;

public class Looper {

    private static ThreadLocal<Looper> sThreadLocal = new ThreadLocal<Looper>();

    MessageQueue queue;

    private Looper() {
        queue = new MessageQueue();
    }

    public static void prepare() {
        if (sThreadLocal.get() != null) {
            throw new IllegalStateException("each thread should not has more than one looper");
        }

        sThreadLocal.set(new Looper());
    }

    public static Looper myLooper() {
        return sThreadLocal.get();
    }

    public static void loop() {
        Looper looper = myLooper();
        MessageQueue mMessageQueue = looper.queue;
        for (; ;) {
            Message msg = mMessageQueue.next();

            if (msg == null) {
                return;
            }

            msg.target.dispatchMessage(msg);

            msg.recyle();
        }
    }

}