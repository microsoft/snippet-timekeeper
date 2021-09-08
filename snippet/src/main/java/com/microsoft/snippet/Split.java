package com.microsoft.snippet;

import java.util.concurrent.atomic.AtomicInteger;

public class Split {

    private static final AtomicInteger SEQUENCE = new AtomicInteger(1);
    private final long mStarted;
    private final long mEnded;
    private final int mSequence;
    private String mName;

    private String mInfo;

    public Split(long start, long end) {
        this.mStarted = start;
        this.mEnded = end;
        this.mSequence = SEQUENCE.getAndIncrement();
    }

    public void setName(String name) {
        this.mName = name;
    }

    public String getName() {
        return this.mName;
    }

    public long getStarted() {
        return mStarted;
    }

    public long getEnded() {
        return mEnded;
    }

    public long delta() {
        return mEnded - mStarted;
    }

    public String getInfo() {
        return mInfo;
    }

    public void setInfo(String mInfo) {
        this.mInfo = mInfo;
    }

    public int sequence() {
        return this.mSequence;
    }

    public double percentage(long total) {
        return ((double) delta() / total) * 100;
    }
}
