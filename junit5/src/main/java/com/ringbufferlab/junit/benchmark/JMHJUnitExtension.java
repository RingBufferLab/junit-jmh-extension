/*
 * Copyright 2023 RingBufferLab
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ringbufferlab.junit.benchmark;


import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.InvocationInterceptor;
import org.junit.jupiter.api.extension.ReflectiveInvocationContext;

import java.lang.reflect.Method;
import java.util.Optional;

/**
 * Rule to turn {@link org.junit.jupiter.api.Test} annotated method into {@link org.openjdk.jmh.annotations.Benchmark} method
 */
public class JMHJUnitExtension implements InvocationInterceptor, ExecutionCondition {
    private final BenchmarkRunner benchmarkRunner;

    public JMHJUnitExtension() {
        this.benchmarkRunner = new BenchmarkRunner();
    }

    @Override
    public void interceptTestMethod(Invocation<Void> invocation, ReflectiveInvocationContext<Method> invocationContext, ExtensionContext extensionContext) throws Throwable {
        Method testMethod = invocationContext.getExecutable();
        if (canRunBenchmark(testMethod) && BenchmarkRunnerCondition.shouldRunBenchmark(testMethod)) {
            BenchmarkContext.setTestInstance(invocationContext.getTarget().get());
            benchmarkRunner.run(testMethod, testMethod.getAnnotation(BenchmarkTest.class).configuration());
            invocation.skip();
        } else {
            invocation.proceed();
        }
    }

    @Override
    public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext extensionContext) {
        Optional<Method> testMethod = extensionContext.getTestMethod();
        return ConditionEvaluationResult.enabled("");
    }

    private static boolean canRunBenchmark(Method testMethod) {
        return testMethod.isAnnotationPresent(BenchmarkTest.class);
    }
}