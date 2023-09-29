package com.ringbufferlab.junit.benchmark;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;

import static com.ringbufferlab.junit.benchmark.AnnotationHelper.getParentAnnotation;

public class BenchmarkRunnerCondition {

    public static boolean shouldRunBenchmark(Method testMethod) {
        Optional<EnableBenchmark> enableBenchmark = getParentAnnotation(testMethod.getDeclaringClass(), EnableBenchmark.class);
        if (enableBenchmark.isPresent()) {
            try {
                return (boolean) enableBenchmark.get().value().getMethod("enableBenchmark").invoke(enableBenchmark.get().value());
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                System.err.println("EnableBenchmark annotation values has no boolean enableBenchmark() method");
                return false;
            }
        }
        Optional<EnableBenchmarkOnSystemProperty> enableBenchmarkOnSystemProperty = getParentAnnotation(testMethod.getDeclaringClass(), EnableBenchmarkOnSystemProperty.class);
        if (enableBenchmarkOnSystemProperty.isPresent()
                && System.getProperty(enableBenchmarkOnSystemProperty.get().name()) != null
                && System.getProperty(enableBenchmarkOnSystemProperty.get().name()).equals(enableBenchmarkOnSystemProperty.get().value())) {
            return true;
        }
        Optional<EnableBenchmarkOnEnvironment> enableBenchmarkOnEnvironment = getParentAnnotation(testMethod.getDeclaringClass(), EnableBenchmarkOnEnvironment.class);
        if (enableBenchmarkOnEnvironment.isPresent()
                && System.getenv(enableBenchmarkOnEnvironment.get().name()) != null
                && System.getenv(enableBenchmarkOnEnvironment.get().name()).equals(System.getenv(enableBenchmarkOnEnvironment.get().value()))) {
            return true;
        }
        if (!enableBenchmarkOnSystemProperty.isPresent() && !enableBenchmarkOnEnvironment.isPresent()) {
            System.err.printf("Benchmark on \"%s()\" won't be run because test class: \"%s\" or its parent(s) has not been annotated to enable benchmark .%n If you see this log it means you forgot to annotate test class with one of following annotation @%s, @%s or @%s.",
                    testMethod.getName(),
                    testMethod.getDeclaringClass().getCanonicalName(),
                    EnableBenchmark.class.getSimpleName(),
                    EnableBenchmarkOnSystemProperty.class.getSimpleName(),
                    EnableBenchmarkOnEnvironment.class.getSimpleName()
            );
        }
        return false;
    }
}
