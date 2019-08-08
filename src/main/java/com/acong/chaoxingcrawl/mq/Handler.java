package com.acong.chaoxingcrawl.mq;

public class Handler {

    Looper mLooper;

    MessageQueue queue;

    private Callback mCallback;

    public Handler() {
        this(null);
    }

    public Handler(Callback callback) {
        mCallback = callback;
        mLooper = Looper.myLooper();
        if (mLooper == null) {
            throw new IllegalStateException("cannot create handler with null looper");
        }
        queue = mLooper.queue;
    }

    public interface Callback {
        public boolean handleMessage(Message msg);
    }

    public void handleMessage(Message msg) {

    }

    public void dispatchMessage(Message msg) {
        if (msg.callback != null) {
            handleCallback(msg);
        } else {
            if (mCallback != null) {
                if (mCallback.handleMessage(msg)) {
                    return;
                }
            }

            handleMessage(msg);
        }
    }

    public void sendMessage(Message msg) {
        sendMessageDelayed(msg, 0);
    }

    public void sendMessageDelayed(Message msg, long delayedTime) {
        long time = System.currentTimeMillis() + delayedTime;
        sendMessageAtTime(msg, time);
    }

    public void sendMessageAtTime(Message msg, long time) {
        msg.when = time;
        queue.enqueue(msg);

        //System.out.println("sendMessageAtTime, long = " + time);
    }

    public void post(Runnable r) {
        Message msg = getPostMessage(r);

        sendMessage(msg);
    }

    public void postDelayed(Runnable r, long delayedTime) {
        Message msg = getPostMessage(r);

        sendMessageDelayed(msg, delayedTime);
    }

    private Message getPostMessage(Runnable r) {
        Message msg = Message.obtain(this);
        msg.callback = r;

        return msg;
    }

    private void handleCallback(Message msg) {
        msg.callback.run();
    }
}
