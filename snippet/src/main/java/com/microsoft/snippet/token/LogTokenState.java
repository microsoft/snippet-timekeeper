/*
 * Copyright © Microsoft Corporation. All rights reserved.
 */

package com.microsoft.snippet.token;

public enum LogTokenState {

    ACTIVE(1),

    END_CAPTURE_EXECUTED(2),

    IN_POOL(3),

    ATTENUATED(4);

    private final int mId;

    LogTokenState(int id) {
        this.mId = id;
    }

    public int getID() {
        return mId;
    }
}
