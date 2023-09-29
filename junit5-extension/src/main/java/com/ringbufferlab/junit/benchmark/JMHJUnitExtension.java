package com.ringbufferlab.junit.benchmark;


import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.InvocationInterceptor;
import org.junit.jupiter.api.extension.ReflectiveInvocationContext;

import java.lang.reflect.Method;
import java.util.Optional;

public class JMHJUnitExtension implements InvocationInterceptor, ExecutionCondition {
    private final BenchmarkRunner benchmarkRunner;

    public JMHJUnitExtension() {
        this.benchmarkRunner = new BenchmarkRunner();
    }

    @Override
    public void interceptTestMethod(Invocation<Void> invocation, ReflectiveInvocationContext<Method> invocationContext, ExtensionContext extensionContext) throws Throwable {
        Method testMethod = invocationContext.getExecutable();
        if (canRunBenchmark(testMethod) && BenchmarkRunnerCondition.shouldRunBenchmark(testMethod)) {
            benchmarkRunner.run(testMethod, testMethod.getAnnotation(BenchmarkTest.class).configuration());
            invocation.skip();
        } else {
            invocation.proceed();
        }
    }

    @Override
    public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext extensionContext) {
        Optional<Method> testMethod = extensionContext.getTestMethod();
        if (testMethod.isPresent() && canRunBenchmark(testMethod.get()) && BenchmarkRunnerCondition.shouldRunBenchmark(testMethod.get())) {
            ConditionEvaluationResult.disabled("");
        }
        return ConditionEvaluationResult.enabled("");
    }

    private static boolean canRunBenchmark(Method testMethod) {
        return testMethod.isAnnotationPresent(BenchmarkTest.class);
    }
}