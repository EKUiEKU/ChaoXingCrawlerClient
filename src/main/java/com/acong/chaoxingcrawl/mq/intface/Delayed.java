package com.acong.chaoxingcrawl.mq.intface;

import java.util.concurrent.TimeUnit;

public interface Delayed extends Comparable<Delayed> {
    long getDelay(TimeUnit unit);
}