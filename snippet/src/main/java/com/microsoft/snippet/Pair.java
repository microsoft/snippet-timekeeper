/*
 * Copyright © Microsoft Corporation. All rights reserved.
 */

package com.microsoft.snippet;

/**
 * Represents a Pair of objects
 * @param <A> A
 * @param <B> B
 */
public final class Pair<A, B> {
    A a;
    B b;

    public Pair(A first, B second) {
        this.a = first;
        this.b = second;
    }

    public A getFirst() {
        return a;
    }

    public B getSecond() {
        return b;
    }
}