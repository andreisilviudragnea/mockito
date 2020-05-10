/*
 * Copyright (c) 2020 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */

package org.mockito.reactor.junit5;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.mockito.Mockito;
import org.mockito.reactor.subscribecount.listener.ReactorMockCreationListener;

public class ReactorSubscribeCountExtension implements BeforeEachCallback, AfterEachCallback {

    private static final Namespace NAMESPACE = Namespace.create(ReactorSubscribeCountExtension.class);

    private static final String LISTENER = "listener";

    @Override
    public void beforeEach(ExtensionContext context) {
        ReactorMockCreationListener reactorMockCreationListener = new ReactorMockCreationListener();
        Mockito.framework().addListener(reactorMockCreationListener);

        context.getStore(NAMESPACE).put(LISTENER, reactorMockCreationListener);
    }

    @Override
    public void afterEach(ExtensionContext context) {
        ReactorMockCreationListener
            reactorMockCreationListener =
            context.getStore(NAMESPACE).remove(LISTENER, ReactorMockCreationListener.class);

        Mockito.framework().removeListener(reactorMockCreationListener);

        if (!context.getExecutionException().isPresent()) {
            reactorMockCreationListener.checkSubscribeCount();
        }
    }
}
