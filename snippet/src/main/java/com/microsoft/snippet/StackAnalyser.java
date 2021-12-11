/*
 * Copyright © Microsoft Corporation. All rights reserved.
 */

package com.microsoft.snippet;

import androidx.annotation.RestrictTo;

/**
 * Internal helper class used to extract the execution context of the code which was guarded
 * by Snippet APIs.
 * NOT FOR EXTERNAL USE
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
final class StackAnalyser {
    static final int API_CAPTURE = 0;
    static final int API_LOG_TOKEN = 1;

    private final String mPackage;

    StackAnalyser(String packageName) {
        this.mPackage = packageName;
    }

    StackAnalyser() {
        this("com.microsoft");
    }

    String callingMethod(Thread mCallingThread, int apiType) {
        if (apiType == API_CAPTURE) {
            return getCallingFrameForCapture(mCallingThread, apiType).getMethodName();
        } else {
            return getDoEndSliceCallerFrame(mCallingThread, apiType).getMethodName();
        }
    }

    String callingClass(Thread mCallingThread, int apiType) {
        if (apiType == API_CAPTURE) {
            return getCallingFrameForCapture(mCallingThread, apiType).getClassName();
        } else {
            return getDoEndSliceCallerFrame(mCallingThread, apiType).getClassName();
        }
    }

    int callingLine(Thread mCallingThread, int apiType) {
        if (apiType == API_CAPTURE) {
            return getCallingFrameForCapture(mCallingThread, apiType).getLineNumber();
        } else {
            return getDoEndSliceCallerFrame(mCallingThread, apiType).getLineNumber();
        }
    }

    private StackTraceElement searchForCallingFrameOfMethodCaller(Thread callingThread, String methodName, int apiType) {
        StackTraceElement[] frames = callingThread.getStackTrace();
        int index = -1;
        for (int i = 0; i < frames.length; i++) {
                if (frames[i].getMethodName().equals(methodName)
                        && frames[i].getClassName().startsWith(mPackage) && apiType == API_CAPTURE
                        && frames[i].getClassName().equals("com.microsoft.snippet.Snippet")
                        ||
                        frames[i].getMethodName().equals(methodName)
                                && frames[i].getClassName().startsWith(mPackage) && apiType == API_LOG_TOKEN
                                && frames[i].getClassName().equals("com.microsoft.snippet.Snippet$LogToken")) {
                    index = i;
                    break;
                }
        }
        // Now we know the index at which we found the method in the stackframe, the next stack frame belongs to the code that called that method.
        return callingThread.getStackTrace()[index + 1];
    }

    private StackTraceElement getDoEndSliceCallerFrame(Thread thread, int apiType) {
        return searchForCallingFrameOfMethodCaller(thread, "endCapture", apiType);
    }

    private StackTraceElement getCallingFrameForCapture(Thread thread, int apiType) {
        return searchForCallingFrameOfMethodCaller(thread, "capture", apiType);
    }
}
