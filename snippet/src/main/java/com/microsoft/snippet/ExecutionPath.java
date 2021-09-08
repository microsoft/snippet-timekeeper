/*
 * Copyright © Microsoft Corporation. All rights reserved.
 */

package com.microsoft.snippet;


import androidx.annotation.NonNull;

import com.microsoft.snippet.token.ILogToken;

/**
 * Execution path determines how core the functionality of this library should behave.
 * It might be possible that we do not want to execute the code entirely in release builds or
 * may want to add some extra information into the existing information and add it to files.
 * We can plugin a custom execution path or method through this.
 */
public interface ExecutionPath {

    /**
     * Return a pair of delta: the time taken to execute the closure. And the standard log message
     * printed by the Snippet.
     *
     * @param message Custom message to print on the log if any.
     * @param closure Closure who execution duration needs to be measured.
     * @return Pair of Delta and Log String
     */
    @NonNull
    ExecutionContext capture(String message, Snippet.Closure closure);

    /**
     * Return a pair of delta: the time taken to execute the closure. And the standard log message
     * printed by the Snippet.
     *
     * @param closure Closure who execution duration needs to be measured.
     * @return Pair of Delta and Log String
     */
    @NonNull
    ExecutionContext capture(Snippet.Closure closure);

    /**
     * Returns a log token and starts the measurement at this point. The token returned has a
     * method endCapture() which ends the measurement.
     *
     * @return IToken
     */
    ILogToken startCapture();

    /**
     * Gets a log token and ties it with a tag that can be used to search the instance of log token
     * using {@link Snippet#find(String)}
     *
     * @param tag Unique tag for the log token. If 2 token try to use same tag, the first one to get
     *            the tag wins and other calls becomes no-op.
     * @return IToken
     */
    ILogToken startCapture(String tag);

    /**
     * Finds a log token with a tag which has been created previously. Using
     * {@link Snippet#startCapture(String)}
     *
     * @param tag Custom tag. This tag will be used to search the log token across the app.
     * @return IToken if any available attached with this tag
     */
    ILogToken find(String tag);

}
