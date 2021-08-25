/*
 * Copyright © Microsoft Corporation. All rights reserved.
 */

package com.microsoft.snippet;

import android.util.Log;

import androidx.annotation.NonNull;

import com.microsoft.snippet.token.ILogToken;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Internal helper class that takes care of tagging the LogTokens.
 * Not intended for external use.
 *
 * @author vishalratna
 */
class TagHelper implements ILogTokenSearcher {
    private static final String LOG_TAG = TagHelper.class.getSimpleName();

    private final Map<String, ILogToken> mRegistry;

    TagHelper() {
        this.mRegistry = new HashMap<>();
    }

    @NonNull
    Pair<ILogToken, Boolean> tag(String tag, ILogToken token) {
        synchronized (mRegistry) {
            // Synchronizing to make sure, while we are checking the existence, a new thread does
            // not add a key which returns true for contains() call.
            if (mRegistry.containsKey(tag)) {
                if (Snippet.mPrintDebugLogs) {
                    Log.e(LOG_TAG, "Tag already existing, we will not provide tagged token. Returning the token without tagging.");
                }
                return new Pair<>(token, false);
            }

            // Now we know that entry is not existing, make an entry
            if (Snippet.mPrintDebugLogs) {
                Log.d(LOG_TAG, "Tagging the LogToken and returning it back");
            }
            mRegistry.put(tag, token);
        }

        return new Pair<>(token, true);
    }

    ILogToken unTag(ILogToken token) {
        synchronized (mRegistry) {
            String requestedTag = null;
            Set<Map.Entry<String, ILogToken>> entrySet = mRegistry.entrySet();
            for (Map.Entry<String, ILogToken> eachEntry : entrySet) {
                if (eachEntry.getValue() == token) {
                    requestedTag = eachEntry.getKey();
                    break;
                }
            }
            // If we did not get anything after searching the registry.
            if (requestedTag == null) {
                if (Snippet.mPrintDebugLogs) {
                    Log.e(LOG_TAG, "There is no such tagged LogToken existing. Bad request! Returning");
                }
                return null;
            }

            if (Snippet.mPrintDebugLogs) {
                Log.e(LOG_TAG, "We found a existing tag for the LogToken " + token.toString() + ", removing the tag");
            }
            return mRegistry.remove(requestedTag);
        }
    }

    @Override
    public ILogToken search(String tag) {
        synchronized (mRegistry) {
            if (mRegistry.containsKey(tag)) {
                return mRegistry.get(tag);
            } else {
                if (Snippet.mPrintDebugLogs) {
                    Log.d(LOG_TAG, "There is no log token with tag: " + tag);
                }
                return null;
            }
        }
    }
}

