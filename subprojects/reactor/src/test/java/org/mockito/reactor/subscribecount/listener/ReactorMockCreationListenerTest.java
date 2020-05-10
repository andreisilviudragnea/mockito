/*
 * Copyright (c) 2020 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */

package org.mockito.reactor.subscribecount.listener;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.reactor.subscribecount.publisher.FluxUtils;
import org.mockito.reactor.subscribecount.publisher.MonoUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.test.publisher.PublisherProbe;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class ReactorMockCreationListenerTest {

    private static final String DATA = "data";

    private ReactorMockCreationListener listener;

    @Mock
    private Tested tested;

    @BeforeEach
    public void setUp() {
        listener = new ReactorMockCreationListener();
        Mockito.framework().addListener(listener);

        initMocks(this);
    }

    @AfterEach
    public void tearDown() {
        Mockito.framework().removeListener(listener);
    }

    @Test
    public void testDefaultSubscribeCount() {
        when(tested.mono()).thenReturn(Mono.just(DATA));

        verify(tested.mono());

        listener.checkSubscribeCount();
    }

    @Test
    public void testZeroSubscribeCount() {
        when(tested.mono()).thenReturn(MonoUtils.emptyWithSubscribeCount(
            0
        ));

        tested.mono();

        listener.checkSubscribeCount();
    }

    @Test
    public void testCustomSubscribeCount2() {
        when(tested.mono()).thenReturn(MonoUtils.justWithSubscribeCount(
            2, DATA
        ));

        verify(tested.mono());
        verify(tested.mono());

        listener.checkSubscribeCount();
    }

    @Test
    public void testCustomSubscribeCount3() {
        when(tested.mono()).thenReturn(MonoUtils.justWithSubscribeCount(
            3, DATA
        ));

        verify(tested.mono());
        verify(tested.mono());
        verify(tested.mono());

        listener.checkSubscribeCount();
    }

    @Test
    public void testCustomSubscribeCount3SameMono() {
        when(tested.mono()).thenReturn(MonoUtils.justWithSubscribeCount(
            3, DATA
        ));

        Mono<String> mono = tested.mono();

        verify(mono);
        verify(mono);
        verify(mono);

        listener.checkSubscribeCount();
    }

    @Test
    public void testTooManySubscribes() {
        when(tested.mono()).thenReturn(MonoUtils.justWithSubscribeCount(2, DATA));

        verify(tested.mono());
        verify(tested.mono());
        verify(tested.mono());

        checkListenerError(new UnexpectedSubscribeCountException(3, 2, null));
    }

    @Test
    public void testTooLittleSubscribes() {
        when(tested.mono()).thenReturn(MonoUtils.justWithSubscribeCount(3, DATA));

        verify(tested.mono());
        verify(tested.mono());

        checkListenerError(new UnexpectedSubscribeCountException(2, 3, null));
    }

    @Test
    public void testMultipleAnswers() {
        when(tested.mono())
            .thenReturn(MonoUtils.justWithSubscribeCount(1, DATA))
            .thenReturn(MonoUtils.justWithSubscribeCount(3, DATA));

        verify(tested.mono());
        verify(tested.mono());
        verify(tested.mono());
        verify(tested.mono());

        listener.checkSubscribeCount();
    }

    @Test
    public void testMultipleAnswersSameMono() {
        when(tested.mono())
            .thenReturn(MonoUtils.justWithSubscribeCount(3, DATA))
            .thenReturn(MonoUtils.justWithSubscribeCount(4, DATA));

        Mono<String> mono1 = tested.mono();
        verify(mono1);
        verify(mono1);
        verify(mono1);

        Mono<String> mono2 = tested.mono();
        verify(mono2);
        verify(mono2);
        verify(mono2);
        verify(mono2);

        listener.checkSubscribeCount();
    }

    @Test
    public void testCustomSubscribeCountFlux() {
        when(tested.flux())
            .thenReturn(FluxUtils.emptyWithSubscribeCount(3))
            .thenReturn(FluxUtils.justWithSubscribeCount(4, DATA))
            .thenReturn(FluxUtils.justWithSubscribeCount(1, DATA, DATA));

        Flux<String> emptyFlux = tested.flux();
        verifyEmptyFlux(emptyFlux);
        verifyEmptyFlux(emptyFlux);
        verifyEmptyFlux(emptyFlux);

        Flux<String> flux = tested.flux();
        verify(flux);
        verify(flux);
        verify(flux);
        verify(flux);

        StepVerifier
            .create(tested.flux())
            .expectNext(DATA)
            .expectNext(DATA)
            .verifyComplete();

        listener.checkSubscribeCount();
    }

    @Test
    public void testPublisherProbe() {
        when(tested.mono()).thenReturn(PublisherProbe.of(Mono.just(DATA)).mono());

        verify(tested.mono());
        verify(tested.mono());

        checkListenerError(new UnexpectedSubscribeCountException(2, 1, null));
    }

    private void verifyEmptyFlux(Flux<String> flux) {
        StepVerifier
            .create(flux)
            .verifyComplete();
    }

    private static void verify(Mono<String> mono) {
        StepVerifier
            .create(mono)
            .expectNext(DATA)
            .verifyComplete();
    }

    private static void verify(Flux<String> flux) {
        StepVerifier
            .create(flux)
            .expectNext(DATA)
            .verifyComplete();
    }

    private void checkListenerError(UnexpectedSubscribeCountException expected) {
        assertThatThrownBy(listener::checkSubscribeCount)
            .isInstanceOfSatisfying(UnexpectedSubscribeCountException.class, e -> {
                assertThat(e.getSubscribeCount()).isEqualTo(expected.getSubscribeCount());
                assertThat(e.getExpectedSubscribeCount())
                    .isEqualTo(expected.getExpectedSubscribeCount());
            });
    }

    private interface Tested {

        Mono<String> mono();

        Flux<String> flux();
    }
}
