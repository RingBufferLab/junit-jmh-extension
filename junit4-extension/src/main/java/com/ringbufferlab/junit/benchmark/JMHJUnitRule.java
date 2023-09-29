package com.ringbufferlab.junit.benchmark;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import java.lang.reflect.Method;

public class JMHJUnitRule implements TestRule {
    private final BenchmarkRunner benchmarkRunner;

    public JMHJUnitRule() {
        this.benchmarkRunner = new BenchmarkRunner();
    }

    @Override
    public Statement apply(Statement base, Description description) {
        return new Statement() {

            @Override
            public void evaluate() throws Throwable {
                description.getClassName();
                Class<?> testClass = this.getClass().getClassLoader().loadClass(description.getClassName());
                Method testMethod = testClass.getMethod(description.getMethodName());
                if (canRunBenchmark(testMethod) && BenchmarkRunnerCondition.shouldRunBenchmark(testMethod)) {
                    benchmarkRunner.run(testMethod, testMethod.getAnnotation(BenchmarkTest.class).configuration());
                } else {
                    base.evaluate();
                }
            }
        };
    }


    private static boolean canRunBenchmark(Method testMethod) {
        return testMethod.isAnnotationPresent(BenchmarkTest.class);
    }

}
