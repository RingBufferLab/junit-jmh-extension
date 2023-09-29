package com.ringbufferlab.junit.benchmark;

import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static com.ringbufferlab.junit.benchmark.AnnotationHelper.getParentAnnotation;

public class BenchmarkRunner {

    public void run(Method testMethod, BenchmarkConfiguration benchmarkConfiguration) {
        for (Options buildOption : buildOptions(testMethod, benchmarkConfiguration)) {
            try {
                new Runner(buildOption).run();
            } catch (RunnerException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static Set<Options> buildOptions(Method testMethod, BenchmarkConfiguration annotation) {
        String benchmarkName = testMethod.getDeclaringClass().getCanonicalName() + "\\." + testMethod.getName() + "$";
        BenchmarkConfiguration classAnnotation = getParentAnnotation(testMethod.getDeclaringClass(), BenchmarkConfiguration.class).orElse(null);
        Optional<Fork> forkConfiguration = getForkConfiguration(annotation, classAnnotation);
        Optional<Warmup> warmupConfiguration = getWarmupConfiguration(annotation, classAnnotation);
        Optional<Measurement> measurementConfiguration = getMeasurementConfiguration(annotation, classAnnotation);
        Set<Options> options = new HashSet<>();
        for (Mode mode : getModeConfiguration(annotation, classAnnotation).map(BenchmarkMode::value).orElse(new Mode[]{Mode.AverageTime})) {
            Options runnerOption = new OptionsBuilder()
                    .include(benchmarkName)
                    // JVM config
                    .forks(forkConfiguration.map(Fork::value).orElse(1))
                    .jvmArgs(forkConfiguration.map(Fork::jvmArgs).orElse(new String[]{}))
                    .jvmArgsAppend(forkConfiguration.map(Fork::jvmArgsAppend).orElse(new String[]{}))
                    .jvmArgsAppend(forkConfiguration.map(Fork::jvmArgsPrepend).orElse(new String[]{}))
                    // Threads
                    .threads(getThreadsConfiguration(annotation, classAnnotation))
                    // Mode
                    .mode(mode)
                    // Warmup
                    .warmupBatchSize(warmupConfiguration.map(Warmup::batchSize).orElse(1))
                    .warmupIterations(warmupConfiguration.map(Warmup::iterations).orElse(5))
                    .warmupTime(new TimeValue(warmupConfiguration.map(Warmup::time).orElse(5), warmupConfiguration.map(Warmup::timeUnit).orElse(TimeUnit.SECONDS)))
                    // Operations per invocation
                    .operationsPerInvocation(getOperationPerInvocation(annotation, classAnnotation))
                    // Measurement
                    .measurementBatchSize(measurementConfiguration.map(Measurement::batchSize).orElse(1))
                    .measurementIterations(measurementConfiguration.map(Measurement::iterations).orElse(5))
                    .measurementTime(new TimeValue(measurementConfiguration.map(Measurement::time).orElse(5), measurementConfiguration.map(Measurement::timeUnit).orElse(TimeUnit.SECONDS)))
                    .build();

            options.add(runnerOption);
        }
        return options;
    }


    private static Optional<Fork> getForkConfiguration(BenchmarkConfiguration methodAnnotation, BenchmarkConfiguration classAnnotation) {
        if (methodAnnotation.fork().length != 0) {
            return Optional.of(methodAnnotation.fork()[0]);
        }
        if (classAnnotation != null && classAnnotation.fork().length != 0) {
            return Optional.of(classAnnotation.fork()[0]);
        }
        return Optional.empty();
    }

    private static Optional<BenchmarkMode> getModeConfiguration(BenchmarkConfiguration methodAnnotation, BenchmarkConfiguration classAnnotation) {
        if (methodAnnotation.mode().length != 0) {
            return Optional.of(methodAnnotation.mode()[0]);
        }
        if (classAnnotation != null && classAnnotation.mode().length != 0) {
            return Optional.of(classAnnotation.mode()[0]);
        }
        return Optional.empty();
    }

    private static Optional<Warmup> getWarmupConfiguration(BenchmarkConfiguration methodAnnotation, BenchmarkConfiguration classAnnotation) {
        if (methodAnnotation.warmup().length != 0) {
            return Optional.of(methodAnnotation.warmup()[0]);
        }
        if (classAnnotation != null && classAnnotation.warmup().length != 0) {
            return Optional.of(classAnnotation.warmup()[0]);
        }
        return Optional.empty();
    }

    private static Optional<Measurement> getMeasurementConfiguration(BenchmarkConfiguration methodAnnotation, BenchmarkConfiguration classAnnotation) {
        if (methodAnnotation.measurement().length != 0) {
            return Optional.of(methodAnnotation.measurement()[0]);
        }
        if (classAnnotation != null && classAnnotation.measurement().length != 0) {
            return Optional.of(classAnnotation.measurement()[0]);
        }
        return Optional.empty();
    }

    private static int getThreadsConfiguration(BenchmarkConfiguration methodAnnotation, BenchmarkConfiguration classAnnotation) {
        int threads = methodAnnotation.threads();
        if (threads != 0) {
            return threads;
        }
        if (classAnnotation != null && classAnnotation.threads() != 0) {
            return classAnnotation.threads();
        }
        return 1;
    }

    private static int getOperationPerInvocation(BenchmarkConfiguration methodAnnotation, BenchmarkConfiguration classAnnotation) {
        int operationPerInvocation = methodAnnotation.operationPerInvocation();
        if (operationPerInvocation != 0) {
            return operationPerInvocation;
        }
        if (classAnnotation != null && classAnnotation.operationPerInvocation() != 0) {
            return classAnnotation.operationPerInvocation();
        }
        return 1;
    }
}
