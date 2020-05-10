/*
 * Copyright (c) 2020 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */

package org.mockito.reactor.junit4;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.mockito.Mockito;
import org.mockito.reactor.subscribecount.listener.ReactorMockCreationListener;

class ReactorSubscribeCountRule implements TestRule {

    @Override
    public Statement apply(Statement base, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                ReactorMockCreationListener reactorMockCreationListener = new ReactorMockCreationListener();
                Mockito.framework().addListener(reactorMockCreationListener);

                try {
                    base.evaluate();
                } finally {
                    Mockito.framework().removeListener(reactorMockCreationListener);
                }

                reactorMockCreationListener.checkSubscribeCount();
            }
        };
    }
}
