/*
 * Copyright (c) 2020 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */

package org.mockito.reactor.subscribecount.listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.mockito.internal.stubbing.StubbedInvocationMatcher;
import org.mockito.internal.stubbing.answers.Returns;
import org.mockito.invocation.Location;
import org.mockito.listeners.StubbingLookupEvent;
import org.mockito.listeners.StubbingLookupListener;
import org.mockito.reactor.subscribecount.publisher.FluxExpectedSubscribeCount;
import org.mockito.reactor.subscribecount.publisher.MonoExpectedSubscribeCount;
import org.mockito.stubbing.Answer;
import org.mockito.stubbing.Stubbing;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.publisher.PublisherProbe;

public class SubscribeCountStubbingLookupListener implements StubbingLookupListener {

    private final Map<StubbedInvocationMatcher, Set<Returns>> seenReturns = new HashMap<>();
    private final List<PublisherProbeContext> publisherProbeContexts = new ArrayList<>();

    @Override
    public void onStubbingLookup(StubbingLookupEvent stubbingLookupEvent) {
        Stubbing stubbing = stubbingLookupEvent.getStubbingFound();

        if (!(stubbing instanceof StubbedInvocationMatcher)) {
            return;
        }

        StubbedInvocationMatcher stubbedInvocationMatcher = (StubbedInvocationMatcher) stubbing;

		Queue<Answer> answers = stubbedInvocationMatcher.getAnswers();

		Answer<?> answer = answers.peek();

        if (!(answer instanceof Returns)) {
            return;
        }

        Returns returns = (Returns) answer;

        Set<Returns> seenReturnsForStubbing = seenReturns
                .computeIfAbsent(stubbedInvocationMatcher, key -> new HashSet<>());

        // Do not wrap the returned publisher again, if already wrapped
        if (seenReturnsForStubbing.contains(returns)) {
            return;
        }

        Object value = returns.getValue();

        // If the return value is not a Mono or Flux,
        // do not wrap the returned value in a SubscriberProbe-based one
        if (!(value instanceof Mono || value instanceof Flux)) {
            return;
        }

        Publisher<?> publisher = (Publisher<?>) value;

        // If the returned value is a publisher, wrap it in SubscriberProbe-based one
        // and replace the answer of the stubbing with the wrapper, in order to track the
        // subscribe count

        PublisherProbe<?> publisherProbe = PublisherProbe.of(publisher);

        Returns returnsPublisherProbe = new Returns(
                publisher instanceof Mono ? publisherProbe.mono() : publisherProbe.flux()
        );

        addToHeadOfQueue(answers, returnsPublisherProbe);

        // Add the wrapped answer to the already seen answers set for this stubbing,
        // in order not to wrap it again afterwards
        seenReturnsForStubbing.add(returnsPublisherProbe);

        publisherProbeContexts.add(new PublisherProbeContext(
                getExpectedSubscribeCount(publisher),
                publisherProbe,
                stubbedInvocationMatcher.getLocation()
        ));
    }

    private void addToHeadOfQueue(Queue<Answer> answers, Returns returnsPublisherProbe) {
        answers.poll();

        // The answers queue needs to be emptied, the wrapped publisher is added first and
        // then all the initial answers are added back too, because there is no way to add an
        // element back to the head of a Queue
        List<Answer> otherAnswers = new ArrayList<>(answers);

        while (answers.poll() != null) {
        }

        answers.add(returnsPublisherProbe);
        answers.addAll(otherAnswers);
    }

    public void checkPublisherProbes() {
        for (PublisherProbeContext publisherProbeContext : publisherProbeContexts) {
            long expectedSubscribeCount = publisherProbeContext.getExpectedSubscribeCount();
            long subscribeCount = publisherProbeContext.getPublisherProbe().subscribeCount();
            if (subscribeCount != expectedSubscribeCount) {
				throw new UnexpectedSubscribeCountException(
						subscribeCount,
						expectedSubscribeCount,
						publisherProbeContext.getLocation()
				);
            }
        }
    }

	private static int getExpectedSubscribeCount(Publisher<?> publisher) {
		if (publisher instanceof MonoExpectedSubscribeCount) {
			return ((MonoExpectedSubscribeCount<?>) publisher).getExpectedSubscribeCount();
		}

		if (publisher instanceof FluxExpectedSubscribeCount) {
			return ((FluxExpectedSubscribeCount<?>) publisher).getExpectedSubscribeCount();
		}

		return 1;
	}

    private static class PublisherProbeContext {

        private final int expectedSubscribeCount;
        private final PublisherProbe<?> publisherProbe;
        private final Location location;

        public PublisherProbeContext(int expectedSubscribeCount,
                                     PublisherProbe<?> publisherProbe,
                                     Location location) {
            this.expectedSubscribeCount = expectedSubscribeCount;
            this.publisherProbe = publisherProbe;
            this.location = location;
        }

        public int getExpectedSubscribeCount() {
            return expectedSubscribeCount;
        }

        public PublisherProbe<?> getPublisherProbe() {
            return publisherProbe;
        }

        public Location getLocation() {
            return location;
        }
    }
}
