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

import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.profile.Profiler;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Defaults;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.ChainedOptionsBuilder;
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
        Optional<Class<? extends Profiler>[]> profilers = getProfilerConfiguration(annotation, classAnnotation);
        Optional<String> resultFileConfiguration = getResultFileConfiguration(annotation, classAnnotation);
        Set<Options> options = new HashSet<>();
        for (Mode mode : getModeConfiguration(annotation, classAnnotation).map(BenchmarkMode::value).orElse(new Mode[]{Mode.AverageTime})) {
            ChainedOptionsBuilder chainedOptionsBuilder = new OptionsBuilder()
                    .include(benchmarkName)
                    // JVM config
                    .forks(forkConfiguration.map(Fork::value).orElse(0))
                    .jvmArgs(forkConfiguration.map(Fork::jvmArgs).orElse(new String[]{}))
                    .jvmArgsAppend(forkConfiguration.map(Fork::jvmArgsAppend).orElse(new String[]{}))
                    .jvmArgsAppend(forkConfiguration.map(Fork::jvmArgsPrepend).orElse(new String[]{}))
                    // Threads
                    .threads(getThreadsConfiguration(annotation, classAnnotation))
                    // Mode
                    .mode(mode)
                    // Warmup
                    .warmupBatchSize(warmupConfiguration.filter(c -> c.batchSize() > 0).map(Warmup::batchSize).orElse(Defaults.WARMUP_BATCHSIZE))
                    .warmupIterations(warmupConfiguration.filter(c -> c.iterations() > 0).map(Warmup::iterations).orElse(Defaults.WARMUP_ITERATIONS))
                    .warmupTime(new TimeValue(warmupConfiguration.filter(c -> c.time() > 0).map(Warmup::time).orElse((int) Defaults.WARMUP_TIME.getTime()), warmupConfiguration.map(Warmup::timeUnit).orElse(TimeUnit.SECONDS)))
                    // Operations per invocation
                    .operationsPerInvocation(getOperationPerInvocation(annotation, classAnnotation))
                    // Measurement
                    .measurementBatchSize(measurementConfiguration.filter(c -> c.batchSize() > 0).map(Measurement::batchSize).orElse(Defaults.MEASUREMENT_BATCHSIZE))
                    .measurementIterations(measurementConfiguration.filter(c -> c.iterations() > 0).map(Measurement::iterations).orElse(Defaults.MEASUREMENT_ITERATIONS))
                    .measurementTime(new TimeValue(measurementConfiguration.filter(c -> c.time() > 0).map(Measurement::time).orElse((int) Defaults.MEASUREMENT_TIME.getTime()), measurementConfiguration.map(Measurement::timeUnit).orElse(TimeUnit.SECONDS)));

            if (profilers.isPresent()) {
                for (Class<? extends Profiler> profilerClass : profilers.get()) {
                    chainedOptionsBuilder.addProfiler(profilerClass);
                }
            }
            if (resultFileConfiguration.isPresent()) {
                chainedOptionsBuilder.result(resultFileConfiguration.get());
                chainedOptionsBuilder.resultFormat(getResultFormatConfiguration(annotation, classAnnotation));
            }
            options.add(chainedOptionsBuilder.build());
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

    private static Optional<Class<? extends Profiler>[]> getProfilerConfiguration(BenchmarkConfiguration methodAnnotation, BenchmarkConfiguration classAnnotation) {
        if (methodAnnotation.profilers().length != 0) {
            return Optional.of(methodAnnotation.profilers());
        }
        if (classAnnotation != null && classAnnotation.profilers().length != 0) {
            return Optional.of(classAnnotation.profilers());
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
        return Defaults.THREADS;
    }

    private static Optional<String> getResultFileConfiguration(BenchmarkConfiguration methodAnnotation, BenchmarkConfiguration classAnnotation) {
        String resultFile = methodAnnotation.resultFile();
        if (resultFile != null && !resultFile.isEmpty()) {
            return Optional.of(resultFile);
        }
        if (classAnnotation != null && !classAnnotation.resultFile().isEmpty()) {
            return Optional.of(classAnnotation.resultFile());
        }
        return Optional.empty();
    }

    private static ResultFormatType getResultFormatConfiguration(BenchmarkConfiguration methodAnnotation, BenchmarkConfiguration classAnnotation) {
        ResultFormatType resultFormat = methodAnnotation.resultFormat();
        if (resultFormat != null && !resultFormat.equals(ResultFormatType.CSV)) {
            return resultFormat;
        }
        if (classAnnotation != null && !classAnnotation.resultFormat().equals(ResultFormatType.CSV)) {
            return classAnnotation.resultFormat();
        }
        return ResultFormatType.CSV;
    }

    private static int getOperationPerInvocation(BenchmarkConfiguration methodAnnotation, BenchmarkConfiguration classAnnotation) {
        int operationPerInvocation = methodAnnotation.operationPerInvocation();
        if (operationPerInvocation != 0) {
            return operationPerInvocation;
        }
        if (classAnnotation != null && classAnnotation.operationPerInvocation() != 0) {
            return classAnnotation.operationPerInvocation();
        }
        return Defaults.OPS_PER_INVOCATION;
    }
}
