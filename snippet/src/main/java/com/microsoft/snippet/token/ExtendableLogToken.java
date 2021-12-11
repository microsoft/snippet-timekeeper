package com.microsoft.snippet.token;

import com.microsoft.snippet.ExecutionContext;
import com.microsoft.snippet.Snippet;

/**
 * A decorator over the {@link ILogToken} API that accepts a log token and routes all the calls
 * from the inner token that is used for creation of this class.
 * This could be potentially used for scenarios where you have to write a custom execution path
 * implementation and {@link com.microsoft.snippet.Snippet#startCapture(String)} and {@link Snippet#startCapture()}
 * return a log token that contains the {@link ExecutionContext}.
 * Using this token we can use the returned execution context information and do additional work that
 * we need to perform.
 */
public class ExtendableLogToken implements ILogToken {

    private final ILogToken mSnippetToken;

    public ExtendableLogToken(ILogToken logToken) {
        this.mSnippetToken = logToken;
    }

    @Override
    public ExecutionContext endCapture(String message) {
        return mSnippetToken.endCapture(message);
    }

    @Override
    public ExecutionContext endCapture() {
        return mSnippetToken.endCapture();
    }

    @Override
    public final long getStart() {
        return mSnippetToken.getStart();
    }

    @Override
    public final long getEnd() {
        return mSnippetToken.getEnd();
    }

    @Override
    public final void setStart(long start) {
        mSnippetToken.setStart(start);
    }

    @Override
    public final void setEnd(long start) {
        mSnippetToken.setEnd(start);
    }

    @Override
    public final ILogToken overrideFilter(String newFilter) {
        return mSnippetToken.overrideFilter(newFilter);
    }

    @Override
    public final String filter() {
        return mSnippetToken.filter();
    }

    @Override
    public final long creatorThreadId() {
        return mSnippetToken.creatorThreadId();
    }

    @Override
    public final boolean isThreadLockEnabled() {
        return mSnippetToken.isThreadLockEnabled();
    }

    @Override
    public final ILogToken enableThreadLock() {
        return mSnippetToken.enableThreadLock();
    }

    @Override
    public final void setCreatorThreadId(long threadId) {
        mSnippetToken.setCreatorThreadId(threadId);
    }

    @Override
    public final void reset() {
        mSnippetToken.reset();
    }

    @Override
    public final void addSplit() {
        mSnippetToken.addSplit();
    }

    @Override
    public final void addSplit(String message) {
        mSnippetToken.addSplit(message);
    }

    @Override
    public final void setState(LogTokenState state) {
        mSnippetToken.setState(state);
    }

    @Override
    public final LogTokenState getState() {
        return mSnippetToken.getState();
    }
}
