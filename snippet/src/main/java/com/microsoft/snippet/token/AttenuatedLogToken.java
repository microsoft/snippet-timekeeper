/*
 * Copyright © Microsoft Corporation. All rights reserved.
 */

package com.microsoft.snippet.token;

import androidx.annotation.RestrictTo;

import com.microsoft.snippet.ExecutionContext;

/**
 * Attenuated token represents a LogToken with suppressed functionality.
 * This is helpful in
 * 1. Creating NO-OP implementations for our public facing APIs.
 * 2. While using {@link com.microsoft.snippet.Snippet#find(String)} it might be possible that we
 * ask for a tag that does not exist it would lead to a null result and would require the caller
 * to use a null check. Using this there helps in returning a no op token and user can avoid null check
 * 3. In some flows you won't always find logtokens for example,
 * while creating a token with tag in onCreate() of Application class and trying to fetch it in
 * some activity. As application onCreate() will not be called always, activity might need a
 * null check after find call, that is avoided by the use of this class.
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
public final class AttenuatedLogToken implements ILogToken {
    private static final ExecutionContext NONE_INFO = new ExecutionContext();

    public AttenuatedLogToken() {
    }

    @Override
    public ExecutionContext endCapture() {
        return NONE_INFO;
    }

    @Override
    public long getStart() {
        return -1;
    }

    @Override
    public long getEnd() {
        return -1;
    }

    @Override
    public void setStart(long start) {
    }

    @Override
    public void setEnd(long start) {
    }

    @Override
    public ILogToken overrideFilter(String newFilter) {
        return this;
    }

    @Override
    public String filter() {
        return "";
    }

    @Override
    public ExecutionContext endCapture(String message) {
        return NONE_INFO;
    }

    @Override
    public long creatorThreadId() {
        return -1L;
    }

    @Override
    public boolean isThreadLockEnabled() {
        return false;
    }

    @Override
    public ILogToken enableThreadLock() {
        return this;
    }

    @Override
    public void setCreatorThreadId(long threadId) {
    }

    @Override
    public void reset() {
    }

    @Override
    public void addSplit() {
    }

    @Override
    public void addSplit(String message) {
    }

    @Override
    public void setState(LogTokenState state) {
    }

    @Override
    public LogTokenState getState() {
        return LogTokenState.ATTENUATED;
    }

}
