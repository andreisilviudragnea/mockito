/*
 * Copyright (c) 2020 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */

package org.mockito.reactor.subscribecount.publisher;

import reactor.core.CoreSubscriber;
import reactor.core.publisher.Flux;
import reactor.test.publisher.TestPublisher;

public class FluxExpectedSubscribeCount<T> extends Flux<T> {

	private final int expectedSubscribeCount;
	private final Flux<T> flux;

	@SafeVarargs
	FluxExpectedSubscribeCount(int expectedSubscribeCount, T... array) {
		this.expectedSubscribeCount = expectedSubscribeCount;
		this.flux = TestPublisher.<T>createCold().emit(array).flux();
	}

	@Override
	public void subscribe(CoreSubscriber<? super T> actual) {
		flux.subscribe(actual);
	}

	public int getExpectedSubscribeCount() {
		return expectedSubscribeCount;
	}
}
