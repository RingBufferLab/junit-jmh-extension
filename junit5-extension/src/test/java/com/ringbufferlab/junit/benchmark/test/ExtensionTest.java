package com.ringbufferlab.junit.benchmark.test;

import com.ringbufferlab.junit.benchmark.BenchmarkConfiguration;
import com.ringbufferlab.junit.benchmark.BenchmarkTest;
import com.ringbufferlab.junit.benchmark.EnableBenchmarkOnSystemProperty;
import com.ringbufferlab.junit.benchmark.JMHJUnitExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Warmup;


@ExtendWith(JMHJUnitExtension.class)
@EnableBenchmarkOnSystemProperty(name = "benchmark", value = "true")
public class ExtensionTest {

    @Benchmark
    @BenchmarkTest(configuration = @BenchmarkConfiguration(warmup = @Warmup(iterations = 1, batchSize = 1, time = 5), measurement = @Measurement(batchSize = 1, time = 1, iterations = 1)))
    public void baseline() {

    }

    @Benchmark
    @BenchmarkTest(configuration = @BenchmarkConfiguration(warmup = @Warmup(iterations = 1, batchSize = 1, time = 5), measurement = @Measurement(batchSize = 1, time = 1, iterations = 1)))
    public void baseline_nowarmup() {

    }

    @Test
    public void standard_test() {
        System.out.println("standard_test");
    }
}
