/*
 * Copyright © Microsoft Corporation. All rights reserved.
 */

package com.microsoft.snippet;

import androidx.annotation.RestrictTo;

/**
 * Split is a sub section of code within a capture( a contiguous/non-contiguous section of code).
 * It is used to measure the duration of subsections and helps in double clicking the areas that are
 * are creating problems.
 *
 * It is advised to use this only for debugging and investigation purposes. Though in release execution path
 * all these {@link Snippet.LogToken#addSplit()} calls are noop and none will be coming to your release builds.
 * But according our understanding it should not be shipped to production. It would make your code look dirty!
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
public class Split {

    private final long mStarted;
    private final long mEnded;
    private final int mSequence;
    private String mName;

    private String mInfo;

    public Split(long start, long end, int seqNumber) {
        this.mStarted = start;
        this.mEnded = end;
        this.mSequence = seqNumber;
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
