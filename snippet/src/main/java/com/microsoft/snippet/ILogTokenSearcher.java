/*
 * Copyright © Microsoft Corporation. All rights reserved.
 */

package com.microsoft.snippet;

import com.microsoft.snippet.token.ILogToken;

/**
 * Log token Searcher. Not for external use
 */
interface ILogTokenSearcher {

    /**
     * Return the token for the tag provided else return null
     *
     * @param tag tag for the log token
     * @return Log Token
     */
    ILogToken search(String tag);
}
