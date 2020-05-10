/*
 * Copyright (c) 2020 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */

package org.mockitousage.reactor.junit4;

import reactor.core.publisher.Mono;

public class ReactiveClassToMock {

    public Mono<String> method1ToMock(String string) {
        return Mono.just(string);
    }

    public Mono<String> method2ToMock(String string) {
        return Mono.just(string);
    }
}
