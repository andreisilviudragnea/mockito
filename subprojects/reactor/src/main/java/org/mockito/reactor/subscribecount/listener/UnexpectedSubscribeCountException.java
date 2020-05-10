/*
 * Copyright (c) 2020 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */

package org.mockito.reactor.subscribecount.listener;

import java.util.StringJoiner;

import org.mockito.invocation.Location;

public class UnexpectedSubscribeCountException extends RuntimeException {

    private final long subscribeCount;
    private final long expectedSubscribeCount;
    private final transient Location location;

    public UnexpectedSubscribeCountException(long subscribeCount,
                                             long expectedSubscribeCount,
                                             Location location) {
        super(String.format(
                "UnexpectedSubscribeCount=%s instead of expectedSubscribeCount=%s at %s",
                subscribeCount, expectedSubscribeCount, location
        ));
        this.subscribeCount = subscribeCount;
        this.expectedSubscribeCount = expectedSubscribeCount;
        this.location = location;
    }

    public long getSubscribeCount() {
        return subscribeCount;
    }

    public long getExpectedSubscribeCount() {
        return expectedSubscribeCount;
    }

    public Location getLocation() {
        return location;
    }

    @Override
    public String toString() {
        return new StringJoiner(
                ", ",
                UnexpectedSubscribeCountException.class.getSimpleName() + "[",
                "]"
        )
                .add("subscribeCount=" + subscribeCount)
                .add("expectedSubscribeCount=" + expectedSubscribeCount)
                .add("location=" + location)
                .toString();
    }
}
