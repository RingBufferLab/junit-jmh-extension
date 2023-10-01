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

import org.junit.jupiter.api.Test;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.TimeValue;

import java.lang.reflect.Method;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

public class BenchmarkRunnerTest {

    @Test
    public void buildOptions_shouldUseMethodAnnotationConfiguration() throws NoSuchMethodException {
        // Given
        Method testCase1 = SampleTestCase.class.getMethod("testCase1");
        // When
        Set<Options> options = BenchmarkRunner.buildOptions(testCase1, testCase1.getAnnotation(BenchmarkTest.class).configuration());
        // Then
        assertThat(options).hasSize(1);
        Options option = (Options)options.toArray()[0];

        assertThat(option.getForkCount().get()).isEqualTo(0);
        assertThat(option.getThreads().get()).isEqualTo(12);
        assertThat(option.getOperationsPerInvocation().get()).isEqualTo(2);

        assertThat(option.getWarmupBatchSize().get()).isEqualTo(12);
        assertThat(option.getWarmupIterations().get()).isEqualTo(11);
        assertThat(option.getWarmupTime().get()).isEqualTo(new TimeValue(13, TimeUnit.MINUTES));

        assertThat(option.getMeasurementBatchSize().get()).isEqualTo(14);
        assertThat(option.getMeasurementIterations().get()).isEqualTo(16);
        assertThat(option.getMeasurementTime().get()).isEqualTo(new TimeValue(15, TimeUnit.DAYS));
    }

    @Test
    public void buildOptions_shouldUseClassAnnotationConfigurationWhenMissingFromMethod() throws NoSuchMethodException {
        // Given
        Method testCase1 = SampleTestCase.class.getMethod("testCase2");
        // When
        Set<Options> options = BenchmarkRunner.buildOptions(testCase1, testCase1.getAnnotation(BenchmarkTest.class).configuration());
        // Then
        assertThat(options).hasSize(1);
        Options option = (Options)options.toArray()[0];

        assertThat(option.getForkCount().get()).isEqualTo(1);
        assertThat(option.getThreads().get()).isEqualTo(2);
        assertThat(option.getOperationsPerInvocation().get()).isEqualTo(2);

        assertThat(option.getWarmupBatchSize().get()).isEqualTo(2);
        assertThat(option.getWarmupIterations().get()).isEqualTo(1);
        assertThat(option.getWarmupTime().get()).isEqualTo(new TimeValue(3, TimeUnit.SECONDS));

        assertThat(option.getMeasurementBatchSize().get()).isEqualTo(21);
        assertThat(option.getMeasurementIterations().get()).isEqualTo(23);
        assertThat(option.getMeasurementTime().get()).isEqualTo(new TimeValue(22, TimeUnit.SECONDS));
    }

    @Test
    public void buildOptions_shouldUseParentClassAnnotationConfigurationWhenMissingFromMethod() throws NoSuchMethodException {
        // Given
        Method testCase1 = ChildTestCase.class.getMethod("testCase3");
        // When
        Set<Options> options = BenchmarkRunner.buildOptions(testCase1, testCase1.getAnnotation(BenchmarkTest.class).configuration());
        // Then
        assertThat(options).hasSize(1);
        Options option = (Options)options.toArray()[0];

        assertThat(option.getForkCount().get()).isEqualTo(1);
        assertThat(option.getThreads().get()).isEqualTo(1);
        assertThat(option.getOperationsPerInvocation().get()).isEqualTo(1);

        assertThat(option.getWarmupBatchSize().get()).isEqualTo(2);
        assertThat(option.getWarmupIterations().get()).isEqualTo(1);
        assertThat(option.getWarmupTime().get()).isEqualTo(new TimeValue(3, TimeUnit.SECONDS));

        assertThat(option.getMeasurementBatchSize().get()).isEqualTo(4);
        assertThat(option.getMeasurementIterations().get()).isEqualTo(6);
        assertThat(option.getMeasurementTime().get()).isEqualTo(new TimeValue(5, TimeUnit.SECONDS));
    }

    @BenchmarkConfiguration(
            warmup = @Warmup(iterations = 1, batchSize = 2, time = 3), measurement = @Measurement(batchSize = 4, time = 5, iterations = 6),
            fork = @Fork(1),
            mode = @BenchmarkMode(Mode.AverageTime),
            threads = 1, operationPerInvocation = 1)
    static class SampleTestCase {

    @BenchmarkTest(configuration = @BenchmarkConfiguration(warmup = @Warmup(iterations = 11, batchSize = 12, time = 13, timeUnit = TimeUnit.MINUTES), measurement = @Measurement(batchSize = 14, time = 15, iterations = 16, timeUnit = TimeUnit.DAYS),
            fork = @Fork(0),
            mode = @BenchmarkMode(Mode.SampleTime),
            threads = 12, operationPerInvocation = 2))
        public void testCase1() {

        }
    @BenchmarkTest(configuration = @BenchmarkConfiguration(measurement = @Measurement(batchSize = 21, time = 22, iterations = 23),
            threads = 2, operationPerInvocation = 2))
        public void testCase2() {

        }
    }

    @BenchmarkConfiguration(
            warmup = @Warmup(iterations = 1, batchSize = 2, time = 3), measurement = @Measurement(batchSize = 4, time = 5, iterations = 6),
            fork = @Fork(1),
            mode = @BenchmarkMode(Mode.AverageTime),
            threads = 1, operationPerInvocation = 1)
    static class ParentTestCase {

    }
    static class ChildTestCase extends ParentTestCase {
        @BenchmarkTest
        public void testCase3() {

        }
    }
}
