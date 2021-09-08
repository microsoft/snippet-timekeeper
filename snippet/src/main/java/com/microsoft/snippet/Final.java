/*
 * Copyright © Microsoft Corporation. All rights reserved.
 */

package com.microsoft.snippet;

/**
 * Provides a class that can be used for capturing variables in an anonymous class implementation.
 *
 * @param <T>
 */
public final class Final<T> {
    private T mValue;

    public Final() {
    }

    public Final(T value) {
        this.mValue = value;
    }

    public T get() {
        return mValue;
    }

    public void set(T value) {
        this.mValue = value;
    }
}
