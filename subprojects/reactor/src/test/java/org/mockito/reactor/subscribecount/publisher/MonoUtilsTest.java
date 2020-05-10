/*
 * Copyright (c) 2020 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */

package org.mockito.reactor.subscribecount.publisher;

import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

public class MonoUtilsTest {

    @Test
    public void testEmptyWithSubscribeCount() {
        StepVerifier
                .create(MonoUtils.emptyWithSubscribeCount(1))
                .verifyComplete();
    }

    @Test
    public void testJustWithSubscribeCount() {
        StepVerifier
                .create(MonoUtils.justWithSubscribeCount(1, 2))
                .expectNext(2)
                .verifyComplete();
    }
}
