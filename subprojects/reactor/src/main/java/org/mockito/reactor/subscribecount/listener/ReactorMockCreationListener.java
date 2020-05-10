/*
 * Copyright (c) 2020 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */

package org.mockito.reactor.subscribecount.listener;

import org.mockito.listeners.MockCreationListener;
import org.mockito.mock.MockCreationSettings;

public class ReactorMockCreationListener implements MockCreationListener {

    private final SubscribeCountStubbingLookupListener subscribeCountStubbingLookupListener =
        new SubscribeCountStubbingLookupListener();

    @Override
    public void onMockCreated(Object mock, MockCreationSettings settings) {
        settings.getStubbingLookupListeners().add(subscribeCountStubbingLookupListener);
    }

    public void checkSubscribeCount() {
        subscribeCountStubbingLookupListener.checkPublisherProbes();
    }
}
