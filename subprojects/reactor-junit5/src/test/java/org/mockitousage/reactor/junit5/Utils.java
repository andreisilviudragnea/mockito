/*
 * Copyright (c) 2020 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */

package org.mockitousage.reactor.junit5;

import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestIdentifier;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;

import java.util.ArrayList;
import java.util.List;

import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;

public class Utils {

    private Utils() {

    }

    public static List<TestReport> runTestClass(Class<?> testClass) {
        LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder
                .request()
                .selectors(selectClass(testClass))
                .build();

        List<TestReport> testReports = new ArrayList<>();

        Launcher launcher = LauncherFactory.create();

        launcher.registerTestExecutionListeners(new TestExecutionListener() {
            @Override
            public void executionFinished(TestIdentifier testIdentifier,
                                          TestExecutionResult testExecutionResult) {
                if (testIdentifier.getDisplayName().endsWith("()")) {
                    testReports.add(new TestReport(testIdentifier, testExecutionResult));
                }
            }
        });

        launcher.execute(request);

        return testReports;
    }

    public static class TestReport {
        private final TestIdentifier testIdentifier;
        private final TestExecutionResult testExecutionResult;

        public TestReport(TestIdentifier testIdentifier,
                          TestExecutionResult testExecutionResult) {
            this.testIdentifier = testIdentifier;
            this.testExecutionResult = testExecutionResult;
        }

        public TestIdentifier getTestIdentifier() {
            return testIdentifier;
        }

        public TestExecutionResult getTestExecutionResult() {
            return testExecutionResult;
        }
    }
}
