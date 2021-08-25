/*
 * Copyright © Microsoft Corporation. All rights reserved.
 */

package com.microsoft.snippet;

import androidx.annotation.NonNull;

import com.microsoft.snippet.token.ILogToken;

/**
 * This is the NOOP execution path that is used by Release build types.
 */
public class ReleaseExecutionPath implements ExecutionPath {
    private static final ExecutionContext RESULT = new ExecutionContext();

    @Override
    @NonNull
    public ExecutionContext capture(String message, Snippet.Closure closure) {
        closure.invoke();
        return RESULT;
    }

    @Override
    @NonNull
    public ExecutionContext capture(Snippet.Closure closure) {
        closure.invoke();
        return RESULT;
    }

    @Override
    public ILogToken startCapture() {
        return Snippet.NO_OP_TOKEN;
    }

    @Override
    public ILogToken startCapture(String tag) {
        return Snippet.NO_OP_TOKEN;
    }

    @Override
    public ILogToken find(String tag) {
        return Snippet.NO_OP_TOKEN;
    }
}
