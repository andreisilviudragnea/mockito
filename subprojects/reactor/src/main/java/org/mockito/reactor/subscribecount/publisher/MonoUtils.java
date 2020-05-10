/*
 * Copyright (c) 2020 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */

package org.mockito.reactor.subscribecount.publisher;

import reactor.core.publisher.Mono;

public class MonoUtils {

    public static <T> Mono<T> emptyWithSubscribeCount(int expectedSubscribeCount) {
		return new MonoExpectedSubscribeCount<>(expectedSubscribeCount);
    }

    public static <T> Mono<T> justWithSubscribeCount(int expectedSubscribeCount, T t) {
		return new MonoExpectedSubscribeCount<>(expectedSubscribeCount, t);
    }
}
