/*
 * Copyright © Microsoft Corporation. All rights reserved.
 */

package com.microsoft.snippet;

import android.util.Log;

import androidx.annotation.NonNull;

import com.microsoft.snippet.token.ILogToken;
import com.microsoft.snippet.token.LogTokenState;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

/**
 * LogToken Pool is helps recycling the log token objects that are used by Snippet.
 */
public final class LogTokenPool {
    private static final String TAG = LogTokenPool.class.getSimpleName();

    private final List<ILogToken> mPool = new LinkedList<>();

    // A register to keep track of allocated tokens.
    private final HashSet<Integer> mRegister = new HashSet<>();

    LogTokenPool() {
    }

    @NonNull
    ILogToken obtain() {
        if (Snippet.mPrintDebugLogs) {
            Log.d(TAG, "obtain() called");
            Log.d(TAG, "Number of objects in POOL while entering into obtain(): " + mPool.size());
        }
        ILogToken temp;
        synchronized (this) {
            if (mPool.size() > 0) {
                if (Snippet.mPrintDebugLogs) {
                    Log.d(TAG, "Pool has reusable objects available. Will use one.");
                }
                temp = mPool.remove(0);
                boolean isAddingToRegisterSuccess = mRegister.add(temp.hashCode());
                if (isAddingToRegisterSuccess) {
                    temp.setState(LogTokenState.ACTIVE);
                    return temp;
                } else {
                    Log.d(TAG, "Register already has the hashcode belonging to the current token, we need to recycle it and create a new one! ");
                    return assureTokenWithUniqueHashCode(temp);
                }
            } else {
                if (Snippet.mPrintDebugLogs) {
                    Log.d(TAG, "Pool is empty, a new LogToken object will be created.");
                }
                temp = createTokenLocked();
                boolean isAddingToRegisterSuccess = mRegister.add(temp.hashCode());
                if (isAddingToRegisterSuccess) {
                    temp.setState(LogTokenState.ACTIVE);
                    return temp;
                } else {
                    Log.d(TAG, "Register already has the hashcode belonging to the current token, we need to recycle it and create a new one! ");
                    return assureTokenWithUniqueHashCode(temp);
                }
            }
        }
    }

    /**
     * This method gives a one level safety against the situation where the hash codes of 2 log tokens
     * become same accidentally given the hash functions are not messed up. It recycles the old token
     * that has the same hash code that exists in the register and tries to generate a new token that
     * will be having different hash code.
     * If someone has messed the hash function badly. This will not help.
     *
     * @param oldToken old token that needs to get recycled.
     * @return new token
     */
    @NonNull
    ILogToken assureTokenWithUniqueHashCode(ILogToken oldToken) {
        // Return old token to the pool
        oldToken.reset();
        mPool.add(oldToken);

        // create a new token and return it.
        ILogToken newToken = createTokenLocked();
        newToken.setState(LogTokenState.ACTIVE);
        return newToken;
    }

    synchronized void recycle(@NonNull ILogToken token) {
        if (Snippet.mPrintDebugLogs) {
            Log.d(TAG, "recycle() called");
            Log.d(TAG, "Number of objects in POOL while entering into recycle(): " + mPool.size());
        }

        // See if we are returning token which was not created using obtain()
        if (!mRegister.contains(token.hashCode())) {
            throw new IllegalStateException("Trying to return object which was not created using obtain() "
                    + "OR  May be endCapture() was called multiple times on the same token object.");
        } else {
            if (Snippet.mPrintDebugLogs) {
                Log.d(TAG, " Recycling the LogToken object in the pool.");
            }
            token.reset();
            token.setState(LogTokenState.IN_POOL);
            mPool.add(token);
            mRegister.remove(token.hashCode());
            if (Snippet.mPrintDebugLogs) {
                Log.d(TAG, "Number of objects in POOL while after recycle(): " + mPool.size());
            }
        }
    }

    private synchronized ILogToken createTokenLocked() {
        return new Snippet.LogToken();
    }
}
