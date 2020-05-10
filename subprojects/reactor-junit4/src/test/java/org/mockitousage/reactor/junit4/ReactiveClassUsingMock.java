/*
 * Copyright (c) 2020 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */

package org.mockitousage.reactor.junit4;

import reactor.core.publisher.Mono;

public class ReactiveClassUsingMock {

    private final ReactiveClassToMock reactiveClassToMock;

    public ReactiveClassUsingMock(ReactiveClassToMock reactiveClassToMock) {
        this.reactiveClassToMock = reactiveClassToMock;
    }

    public Mono<String> useMock(String string) {
        return reactiveClassToMock.method1ToMock(string);
    }

    public Mono<String> useMockNoSubscribe(String string) {
        return errorMono().then(useMock(string));
    }

    public Mono<String> useTwoSubscribes(String string) {
        return Mono.zip(
                doStep1(string),
                doStep2(string),
                (value1, value2) -> value1 + " " + value2
        );
    }

    private Mono<String> doStep1(String string) {
        return useMock(string);
    }

    private Mono<String> doStep2(String string) {
        return useMock(string);
    }

    private Mono<String> errorMono() {
        return Mono.error(new RuntimeException());
    }
}
