/*
 * Copyright (c) 2020 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */

package org.mockito.reactor.subscribecount.publisher;

import reactor.core.CoreSubscriber;
import reactor.core.publisher.Mono;
import reactor.test.publisher.TestPublisher;

public class MonoExpectedSubscribeCount<T> extends Mono<T> {

	private final int expectedSubscribeCount;
	private final Mono<T> mono;

	@SafeVarargs
	MonoExpectedSubscribeCount(int expectedSubscribeCount, T... array) {
		this.expectedSubscribeCount = expectedSubscribeCount;
		this.mono = TestPublisher.<T>createCold().emit(array).mono();
	}

	@Override
	public void subscribe(CoreSubscriber<? super T> actual) {
		mono.subscribe(actual);
	}

	public int getExpectedSubscribeCount() {
		return expectedSubscribeCount;
	}
}
