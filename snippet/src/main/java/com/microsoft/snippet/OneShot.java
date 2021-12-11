/*
 * Copyright © Microsoft Corporation. All rights reserved.
 */

package com.microsoft.snippet;

import android.util.Log;

import androidx.annotation.RestrictTo;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * A thread safe implementation which let's the caller thread to set the value of the wrapping
 * type once using {@link OneShot#set(Object)}, once the value is set, any other calls to set
 * is not honoured. The value remains the same for the lifetime of the instance.
 * Call {@link OneShot#get()} to access the wrapped instance.
 *
 * @param <T>
 * @author vishalratna
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
public final class OneShot<T> {
    private static final String TAG = OneShot.class.getSimpleName();

    private final AtomicReference<T> mData;
    private final AtomicInteger mCounter;

    public OneShot(T data) {
        this.mData = new AtomicReference<>(data);
        this.mCounter = new AtomicInteger(0);
    }

    public void set(T data) {
        if (mCounter.compareAndSet(0, 1)) {
            T oldVal = mData.get();
            if (mData.compareAndSet(oldVal, data)) {
                if (Snippet.mPrintDebugLogs) {
                    Log.d(TAG, "OneShot value set successfully.");
                }
            } else {
                if (Snippet.mPrintDebugLogs) {
                    Log.e(TAG, "OneShot already set after the counter was set to 1. Cannot change the value again.");
                }
            }
        } else {
            if (Snippet.mPrintDebugLogs) {
                Log.e(TAG, "OneShot already set once. Cannot change the value again.");
            }
        }
    }

    public T get() {
        return mData.get();
    }
}
