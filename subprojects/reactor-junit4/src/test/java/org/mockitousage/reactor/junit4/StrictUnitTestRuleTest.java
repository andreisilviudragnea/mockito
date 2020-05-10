/*
 * Copyright (c) 2020 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */

package org.mockitousage.reactor.junit4;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runners.MethodSorters;
import org.mockito.Mock;
import org.mockito.exceptions.misusing.UnnecessaryStubbingException;
import org.mockito.reactor.junit4.MockitoReactorJunit4;
import org.mockito.reactor.subscribecount.listener.UnexpectedSubscribeCountException;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

public class StrictUnitTestRuleTest {

    @Test
    public void testAllFeaturesRule() {
        Result result = JUnitCore.runClasses(AllFeaturesRule.class);
        List<Failure> failures = result.getFailures();

        assertThat(failures).hasSize(2);
        assertThat(failures.get(0)).satisfies(failure -> assertThat(failure.getException())
                .isInstanceOf(UnexpectedSubscribeCountException.class)
        );
        assertThat(failures.get(1)).satisfies(failure -> assertThat(failure.getException())
                .isInstanceOf(UnnecessaryStubbingException.class)
        );
    }

    @FixMethodOrder(MethodSorters.NAME_ASCENDING)
    public static class AllFeaturesRule {

        @Rule
        public final TestRule strictUnitTestRule = MockitoReactorJunit4.strictUnitTestRule(this);

        @Mock
        private ReactiveClassToMock reactiveClassToMock;

        private ReactiveClassUsingMock reactiveClassUsingMock;

        @Before
        public void setUp() {
            reactiveClassUsingMock = new ReactiveClassUsingMock(reactiveClassToMock);
            when(reactiveClassToMock.method1ToMock("input")).thenReturn(Mono.just("output"));
        }

        @Test
        public void testUnexpectedSubscribeCount() {
            assertThatThrownBy(() -> reactiveClassUsingMock.useMockNoSubscribe("input").block())
                    .isInstanceOf(RuntimeException.class);
        }

        @Test
        public void unnecessaryStubbingInMethodBody() {
            when(reactiveClassToMock.method2ToMock("input")).thenReturn(Mono.just("output"));

            when(reactiveClassToMock.method1ToMock("input")).thenReturn(Mono.just("output"));
            assertThat(reactiveClassUsingMock.useMock("input").block())
                    .isEqualTo("output");
        }
    }

    @Test
    public void testOnlyOneFailureReported() {
        Result result = JUnitCore.runClasses(OnlyOneFailureReported.class);
        List<Failure> failures = result.getFailures();

        assertThat(failures).hasSize(1);
        assertThat(failures.get(0)).satisfies(failure -> assertThat(failure.getException())
                .isInstanceOf(UnnecessaryStubbingException.class)
        );
    }

    public static class OnlyOneFailureReported {

        @Rule
        public final TestRule strictUnitTestRule = MockitoReactorJunit4.strictUnitTestRule(this);

        @Mock
        private ReactiveClassToMock reactiveClassToMock;

        private ReactiveClassUsingMock reactiveClassUsingMock;

        @Before
        public void setUp() {
            reactiveClassUsingMock = new ReactiveClassUsingMock(reactiveClassToMock);
        }

        @Test
        public void testUnnecessaryStubbingAndUnexpectedSubscribeCount() {
            when(reactiveClassToMock.method2ToMock("input")).thenReturn(Mono.just("output"));

            when(reactiveClassToMock.method1ToMock("input")).thenReturn(Mono.just("output"));
            assertThatThrownBy(() -> reactiveClassUsingMock.useMockNoSubscribe("input").block())
                    .isInstanceOf(RuntimeException.class);
        }
    }
}
