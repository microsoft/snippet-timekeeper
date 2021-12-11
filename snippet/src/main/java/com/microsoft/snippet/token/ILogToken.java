/*
 * Copyright © Microsoft Corporation. All rights reserved.
 */

package com.microsoft.snippet.token;

import com.microsoft.snippet.Snippet;
import com.microsoft.snippet.ExecutionContext;

/**
 * Representation of log token. All types of log token should implement this interface
 */
public interface ILogToken {
    /**
     * Set the filter for this log token, this is override the global filter.
     *
     * @param newFilter new filter
     * @return IToken instance on which start()/end() can be called.
     */
    ILogToken overrideFilter(String newFilter);

    /**
     * Returns existing filter
     *
     * @return existing filter
     */
    String filter();

    /**
     * Ends the capture which was started through {@link Snippet#startCapture()}.
     *
     * @param message Custom message if required
     */
    ExecutionContext endCapture(String message);

    /**
     * ThreadLock: It is a restriction that could be implemented on a log token level where the user
     * could say that the thread that starts the measurement should only be the one that ends the measurement.
     * Returns the id of the thread that asked for this log token.
     * @return Thread ID.
     */
    long creatorThreadId();

    /**
     * Returns whether a thread lock is enabled or not.
     */
    boolean isThreadLockEnabled();

    ILogToken enableThreadLock();

    void setCreatorThreadId(long threadId);

    void reset();

    void addSplit();

    void addSplit(String message);

    void setState(LogTokenState state);

    LogTokenState getState();

    /**
     * Ends the capture which was started through {@link Snippet#startCapture()}.
     */
    ExecutionContext endCapture();

    long getStart();

    long getEnd();

    void setStart(long start);

    void setEnd(long start);
}
