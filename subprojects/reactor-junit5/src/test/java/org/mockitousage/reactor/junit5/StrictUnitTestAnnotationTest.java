/*
 * Copyright (c) 2020 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */

package org.mockitousage.reactor.junit5;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.platform.engine.TestExecutionResult;
import org.mockito.Mock;
import org.mockito.exceptions.misusing.PotentialStubbingProblem;
import org.mockito.exceptions.misusing.UnnecessaryStubbingException;
import org.mockito.reactor.junit5.StrictUnitTest;
import org.mockito.reactor.subscribecount.listener.UnexpectedSubscribeCountException;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.when;

public class StrictUnitTestAnnotationTest {

    @Test
    public void testStrictUnitTestAnnotation() {
        List<Utils.TestReport> testReports = Utils.runTestClass(TestStrictUnitTest.class);

        List<TestExecutionResult>
                testExecutionResults =
                testReports
                        .stream()
                        .map(Utils.TestReport::getTestExecutionResult)
                        .collect(Collectors.toList());

        assertThat(testExecutionResults.get(0).getThrowable().get())
                .isInstanceOfSatisfying(AssertionError.class, ex -> {
                    assertThat(ex.getSuppressed()).isEmpty();
                });

        assertThat(testExecutionResults.get(1).getThrowable().get())
                .isInstanceOfSatisfying(UnexpectedSubscribeCountException.class, ex -> {
                    assertThat(ex.getSubscribeCount()).isEqualTo(0);
                    assertThat(ex.getExpectedSubscribeCount()).isEqualTo(1);
                    assertThat(ex.getSuppressed()).isEmpty();
                });

        assertThat(testExecutionResults.get(2).getThrowable().get())
                .isInstanceOfSatisfying(PotentialStubbingProblem.class, ex -> {
                    assertThat(ex.getSuppressed()).isEmpty();
                });

        assertThat(testExecutionResults.get(3).getThrowable().get())
                .isInstanceOfSatisfying(UnexpectedSubscribeCountException.class, ex -> {
                    assertThat(ex.getSubscribeCount()).isEqualTo(2);
                    assertThat(ex.getExpectedSubscribeCount()).isEqualTo(1);
                    assertThat(ex.getSuppressed()).isEmpty();
                });

        assertThat(testExecutionResults.get(4).getThrowable().get())
                .isInstanceOfSatisfying(UnnecessaryStubbingException.class, ex -> {
                    assertThat(ex.getSuppressed()).isEmpty();
                });

        assertThat(testExecutionResults.get(5).getThrowable().get())
                .isInstanceOfSatisfying(UnnecessaryStubbingException.class, ex -> {
                    assertThat(ex.getSuppressed()).isEmpty();
                });

        assertThat(testExecutionResults.get(6).getThrowable().get())
                .isInstanceOfSatisfying(UnnecessaryStubbingException.class, ex -> {
                    assertThat(ex.getSuppressed()).isEmpty();
                });
    }

    @StrictUnitTest
    @TestMethodOrder(MethodOrderer.Alphanumeric.class)
    public static class TestStrictUnitTest {

        @Mock
        private ReactiveClassToMock reactiveClassToMock;

        private ReactiveClassUsingMock reactiveClassUsingMock;

        @BeforeEach
        public void setUp() {
            reactiveClassUsingMock = new ReactiveClassUsingMock(reactiveClassToMock);
            when(reactiveClassToMock.method1ToMock("input")).thenReturn(Mono.just("output"));
        }

        @Test
        public void testFailure() {
            fail("I failed");
        }

        @Test
        public void testNoSubscribe() {
            assertThatThrownBy(() -> reactiveClassUsingMock.useMockNoSubscribe("input").block())
                    .isInstanceOf(RuntimeException.class);
        }

        @Test
        public void testPotentialStubbingProblem() {
            reactiveClassUsingMock.useMock("anotherInput").block();
        }

        @Test
        public void testTwoSubscribes() {
            assertThat(reactiveClassUsingMock.useTwoSubscribes("input").block())
                    .isEqualTo("output output");
        }

        @Test
        public void testUnnecessaryStubbing() {
            when(reactiveClassToMock.method2ToMock("input")).thenReturn(Mono.just("output"));
            assertThat(reactiveClassUsingMock.useMock("input").block())
                    .isEqualTo("output");
        }

        @Test
        public void testUnnecessaryStubbingAndUnexpectedSubscribeCount() {
            when(reactiveClassToMock.method2ToMock("input")).thenReturn(Mono.just("output"));

            assertThatThrownBy(() -> reactiveClassUsingMock.useMockNoSubscribe("input").block())
                    .isInstanceOf(RuntimeException.class);
        }

        @Test
        public void testUnnecessaryStubbingBeforeMethod() {

        }
    }
}
