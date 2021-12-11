/*
 * Copyright © Microsoft Corporation. All rights reserved.
 */

package com.microsoft.snippet;

import androidx.annotation.RestrictTo;

/**
 * Represents a Triple of objects
 *
 * @param <A> A
 * @param <B> B
 * @param <C> C
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
public final class Triple<A, B, C> {
    A a;
    B b;
    C c;

    public Triple(A first, B second, C third) {
        this.a = first;
        this.b = second;
        this.c = third;
    }

    public A getFirst() {
        return a;
    }

    public B getSecond() {
        return b;
    }

    public C getThird() {
        return c;
    }
}