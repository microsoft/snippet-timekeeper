/*
 * Copyright © Microsoft Corporation. All rights reserved.
 */

package com.microsoft.snippet;

import android.os.SystemClock;

import com.microsoft.snippet.token.ILogToken;

/**
 * Utility class the houses some helper functions used across the library.
 */
final class ToolBox {

    private ToolBox() {

    }

    static long invokeAndMeasure(Snippet.Closure closure) {
        long start = SystemClock.uptimeMillis();
        closure.invoke();
        long end = SystemClock.uptimeMillis();

        return end - start;
    }

    static boolean willThreadLockGuardThisCapture(Thread currentThread, ILogToken token) {
        if (token.isThreadLockEnabled()) {
            return currentThread.getId() != token.creatorThreadId();
        } else {
            return false;
        }
    }

    static String combineThreadIdWithUserTag(String tag) {
        return tag + ":" + Thread.currentThread().getId();
    }

    static long getThreadIdFromSnippetTag(String snippetTag) {
        String[] items = snippetTag.split(":");
        if (items.length != 2) {
            throw new IllegalStateException("Snippet tag should have 2 items, user tag and thread ID");
        }
        return Long.parseLong(items[1]);
    }

    static String getUserTagFromSnippetTag(String snippetTag) {
        String[] items = snippetTag.split(":");
        if (items.length != 2) {
            throw new IllegalStateException("Snippet tag should have 2 items, user tag and thread ID");
        }
        return items[0];
    }

    static long currentTime() {
        return SystemClock.uptimeMillis();
    }
}
