/*
 * Copyright (c) 2020 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */

package org.mockito.reactor.subscribecount.publisher;

import reactor.core.publisher.Flux;

public class FluxUtils {

    public static <T> Flux<T> emptyWithSubscribeCount(int expectedSubscribeCount) {
		return new FluxExpectedSubscribeCount<>(expectedSubscribeCount);
    }

    public static <T> Flux<T> justWithSubscribeCount(int expectedSubscribeCount, T t) {
		return new FluxExpectedSubscribeCount<>(expectedSubscribeCount, t);
    }

    @SafeVarargs
    public static <T> Flux<T> justWithSubscribeCount(int expectedSubscribeCount, T... t) {
		return new FluxExpectedSubscribeCount<>(expectedSubscribeCount, t);
    }
}
