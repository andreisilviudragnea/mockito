/*
 * Copyright (c) 2020 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */

package org.mockito.reactor.junit4;

import org.junit.rules.RuleChain;
import org.junit.rules.TestRule;
import org.mockito.junit.MockitoJUnit;
import org.mockito.quality.Strictness;

public class MockitoReactorJunit4 {

    private MockitoReactorJunit4() {

    }

    public static TestRule strictUnitTestRule(Object testInstance) {
        return RuleChain
                .outerRule(new ReactorSubscribeCountRule())
                .around(MockitoJUnit.testRule(testInstance).strictness(Strictness.STRICT_STUBS));
    }
}
