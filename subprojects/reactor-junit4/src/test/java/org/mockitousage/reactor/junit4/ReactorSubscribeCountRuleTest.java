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
import org.mockito.reactor.junit4.MockitoReactorJunit4;
import org.mockito.reactor.subscribecount.listener.UnexpectedSubscribeCountException;
import org.mockito.reactor.subscribecount.publisher.MonoUtils;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.when;

public class ReactorSubscribeCountRuleTest {

    @Test
    public void testMultipleUnexpectedSubscribeCount() {
        Result result = JUnitCore.runClasses(
                MultipleUnexpectedSubscribeCount.class
        );
        List<Failure> failures = result.getFailures();

        assertThat(failures).hasSize(2);
        assertThat(failures).allSatisfy(failure -> assertThat(failure.getException())
                .isInstanceOf(UnexpectedSubscribeCountException.class)
        );
    }

    public static class MultipleUnexpectedSubscribeCount {

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
        public void testTwoSubscribes() {
            assertThat(reactiveClassUsingMock.useTwoSubscribes("input").block())
                    .isEqualTo("output output");
        }

        @Test
        public void testNoSubscribe() {
            assertThatThrownBy(() -> reactiveClassUsingMock.useMockNoSubscribe("input").block())
                    .isInstanceOf(RuntimeException.class);
        }
    }

    @Test
    public void testNoUnexpectedSubscribeCount() {
        Result result = JUnitCore.runClasses(
                NoUnexpectedSubscribeCount.class
        );
        assertThat(result.wasSuccessful()).isTrue();
    }

    public static class NoUnexpectedSubscribeCount {

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
        public void testOneSubscribe() {
            when(reactiveClassToMock.method1ToMock("input")).thenReturn(Mono.just("output"));
            assertThat(reactiveClassUsingMock.useMock("input").block())
                    .isEqualTo("output");
        }

        @Test
        public void testNoSubscribe() {
            when(reactiveClassToMock.method1ToMock("input"))
                    .thenReturn(MonoUtils.justWithSubscribeCount(0, "output"));
            assertThatThrownBy(() -> reactiveClassUsingMock.useMockNoSubscribe("input").block())
                    .isInstanceOf(RuntimeException.class);
        }

        @Test
        public void testTwoSubscribes() {
            when(reactiveClassToMock.method1ToMock("input"))
                    .thenReturn(MonoUtils.justWithSubscribeCount(2, "output"));
            assertThat(reactiveClassUsingMock.useTwoSubscribes("input").block())
                    .isEqualTo("output output");
        }
    }

    @Test
    public void testUnexpectedSubscribeCountMoreThanOneSubscribe() {
        Result result = JUnitCore.runClasses(
                UnexpectedSubscribeCountMoreThanOneSubscribe.class
        );
        List<Failure> failures = result.getFailures();

        assertThat(failures).hasSize(1);
        assertThat(failures).allSatisfy(failure -> assertThat(failure.getException())
                .isInstanceOf(UnexpectedSubscribeCountException.class)
        );
    }

    public static class UnexpectedSubscribeCountMoreThanOneSubscribe {

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
        public void test() {
            assertThat(reactiveClassUsingMock.useTwoSubscribes("input").block())
                    .isEqualTo("output output");
        }
    }

    @Test
    public void testUnexpectedSubscribeCountNoSubscribe() {
        Result result = JUnitCore.runClasses(
                UnexpectedSubscribeCountNoSubscribe.class
        );
        List<Failure> failures = result.getFailures();

        assertThat(failures).hasSize(1);
        assertThat(failures).allSatisfy(failure -> assertThat(failure.getException())
                .isInstanceOf(UnexpectedSubscribeCountException.class)
        );
    }

    public static class UnexpectedSubscribeCountNoSubscribe {

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
        public void test() {
            assertThatThrownBy(() -> reactiveClassUsingMock.useMockNoSubscribe("input").block())
                    .isInstanceOf(RuntimeException.class);
        }
    }

    @Test
    public void testUnexpectedSubscribeCountAfterFailedTest() {
        Result result = JUnitCore.runClasses(
                UnexpectedSubscribeCountAfterFailure.class
        );
        List<Failure> failures = result.getFailures();

        assertThat(failures).hasSize(2);
        assertThat(failures.get(0)).satisfies(failure -> assertThat(failure.getException())
                .isInstanceOf(AssertionError.class)
        );
        assertThat(failures.get(1)).satisfies(failure -> assertThat(failure.getException())
                .isInstanceOf(UnexpectedSubscribeCountException.class)
        );
    }

    @FixMethodOrder(MethodSorters.NAME_ASCENDING)
    public static class UnexpectedSubscribeCountAfterFailure {

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
        public void failingTest() {
            fail("I failed");
        }

        @Test
        public void testTwoSubscribes() {
            assertThat(reactiveClassUsingMock.useTwoSubscribes("input").block())
                    .isEqualTo("output output");
        }
    }

    @Test
    public void testBeforeMethodFails() {
        Result result = JUnitCore.runClasses(BeforeMethodFails.class);
        List<Failure> failures = result.getFailures();

        assertThat(failures).hasSize(1);
        assertThat(failures.get(0)).satisfies(failure -> assertThat(failure.getException())
                .isInstanceOf(AssertionError.class)
        );
    }

    public static class BeforeMethodFails {

        @Rule
        public final TestRule strictUnitTestRule = MockitoReactorJunit4.strictUnitTestRule(this);

        @Mock
        private ReactiveClassToMock reactiveClassToMock;

        private ReactiveClassUsingMock reactiveClassUsingMock;

        @Before
        public void setUp() {
            reactiveClassUsingMock = new ReactiveClassUsingMock(reactiveClassToMock);
            fail("I failed");
        }

        @Test
        public void testTwoSubscribes() {
            when(reactiveClassToMock.method1ToMock("input")).thenReturn(Mono.just("output"));
            assertThat(reactiveClassUsingMock.useTwoSubscribes("input").block())
                    .isEqualTo("output output");
        }
    }
}
