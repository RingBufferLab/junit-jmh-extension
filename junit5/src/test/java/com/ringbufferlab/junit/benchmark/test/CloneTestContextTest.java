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
package com.ringbufferlab.junit.benchmark.test;

import com.ringbufferlab.junit.benchmark.BenchmarkConfiguration;
import com.ringbufferlab.junit.benchmark.BenchmarkContextInitializer;
import com.ringbufferlab.junit.benchmark.BenchmarkTest;
import com.ringbufferlab.junit.benchmark.EnableBenchmark;
import com.ringbufferlab.junit.benchmark.JMHJUnitExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@State(Scope.Benchmark)
@EnableBenchmark
@ExtendWith(JMHJUnitExtension.class)
public class CloneTestContextTest extends CloneTestContextBase {


    private HelloBean helloBean;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        helloBean = new HelloBean();
        when(goodbyeBean.goodBye2()).thenCallRealMethod();
    }

    @Setup(Level.Trial)
    public void initialize() {
        BenchmarkContextInitializer.cloneFromTest(this);
    }

    @Test
    @Benchmark
    @BenchmarkTest(configuration = @BenchmarkConfiguration(warmup = @Warmup(iterations = 1, batchSize = 1, time = 1), measurement = @Measurement(batchSize = 1, time = 1, iterations = 1)))
    public void ensure_cloningContextFromTestHandleAnnotationMock() {
        assertThat(goodbyeBean.goodBye2()).isEqualTo("GoodBye2");
    }

    @Test
    @Benchmark
    @BenchmarkTest(configuration = @BenchmarkConfiguration(warmup = @Warmup(iterations = 1, batchSize = 1, time = 1), measurement = @Measurement(batchSize = 1, time = 1, iterations = 1)))
    public void ensure_cloningContextFromTestHandlePlainInstance() {
        assertThat(helloBean.helloLowercase()).isEqualTo("hello world");
    }
}
