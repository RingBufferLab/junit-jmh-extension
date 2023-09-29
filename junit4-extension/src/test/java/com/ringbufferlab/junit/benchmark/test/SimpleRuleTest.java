package com.ringbufferlab.junit.benchmark.test;

import com.ringbufferlab.junit.benchmark.BenchmarkConfiguration;
import com.ringbufferlab.junit.benchmark.BenchmarkTest;
import com.ringbufferlab.junit.benchmark.EnableBenchmarkOnSystemProperty;
import com.ringbufferlab.junit.benchmark.JMHJUnitRule;
import org.junit.Rule;
import org.junit.Test;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

@State(Scope.Benchmark)
@EnableBenchmarkOnSystemProperty(name = "benchmark", value = "true")
public class SimpleRuleTest {

    @Rule
    public JMHJUnitRule jmhRule = new JMHJUnitRule();

    @Test
    @Benchmark
    @BenchmarkTest(configuration = @BenchmarkConfiguration(warmup = @Warmup(iterations = 1, batchSize = 1, time = 5), measurement = @Measurement(batchSize = 1, time = 1, iterations = 1)))
    public void baseline() {

    }

    @Test
    @Benchmark
    @BenchmarkTest(configuration = @BenchmarkConfiguration(warmup = @Warmup(iterations = 1, batchSize = 1, time = 5), measurement = @Measurement(batchSize = 1, time = 1, iterations = 1)))
    public void baseline_nowarmup() {

    }

    @Test
    public void standard_test() {
        System.out.println("standard_test");
    }
}
