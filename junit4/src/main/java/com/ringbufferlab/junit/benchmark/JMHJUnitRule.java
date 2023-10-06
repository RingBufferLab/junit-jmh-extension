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

import org.junit.After;
import org.junit.Before;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * Rule to turn {@link org.junit.Test} annotated method into {@link org.openjdk.jmh.annotations.Benchmark} method
 */
public class JMHJUnitRule implements TestRule {
    private final BenchmarkRunner benchmarkRunner;
    private final Object testInstance;

    public JMHJUnitRule(Object testInstance) {
        this.benchmarkRunner = new BenchmarkRunner();
        this.testInstance = testInstance;
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
                    BenchmarkContext.setTestInstance(testInstance);
                    applyBefore();
                    benchmarkRunner.run(testMethod, testMethod.getAnnotation(BenchmarkTest.class).configuration());
                    applyAfter();
                } else {
                    base.evaluate();
                }
            }
        };
    }

    private void applyBefore() {
       applyAnnotated(Before.class);
    }

    private void applyAfter() {
       applyAnnotated(After.class);
    }

    private void applyAnnotated(Class<? extends Annotation> annotation) {
        Class<?> testInstanceClass = testInstance.getClass();
        while(testInstanceClass != null) {
            Arrays.stream(testInstance.getClass().getDeclaredMethods()).filter(m -> m.isAnnotationPresent(annotation))
                    .forEach(m -> {
                        try {
                            m.setAccessible(true);
                            m.invoke(testInstance);
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            throw new RuntimeException(e);
                        }
                    });
            testInstanceClass = testInstanceClass.getSuperclass();
        }
    }


    private static boolean canRunBenchmark(Method testMethod) {
        return testMethod.isAnnotationPresent(BenchmarkTest.class);
    }

}
