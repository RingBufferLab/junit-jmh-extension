package com.ringbufferlab.junit.benchmark.test;

import com.ringbufferlab.junit.benchmark.BenchmarkConfiguration;
import com.ringbufferlab.junit.benchmark.BenchmarkTest;
import com.ringbufferlab.junit.benchmark.EnableBenchmarkOnSystemProperty;
import com.ringbufferlab.junit.benchmark.JMHJUnitExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

import java.util.concurrent.atomic.AtomicInteger;

@State(Scope.Benchmark)
@ExtendWith(JMHJUnitExtension.class)
@EnableBenchmarkOnSystemProperty(name = "benchmark", value = "true")
public class ExtensionWithJMHFeaturesTest {

    public AtomicInteger increment = new AtomicInteger(1);
    @Param({ "100", "200", "300" })
    private int param = 10;

    @Setup(Level.Invocation)
    public void setup() {
        increment.incrementAndGet();
    }


    @Benchmark
    @BenchmarkTest(configuration = @BenchmarkConfiguration(warmup = @Warmup(iterations = 1, batchSize = 1, time = 5), measurement = @Measurement(batchSize = 1, time = 1, iterations = 1)))
    public void baseline() {
        int i = increment.get() * param;
    }

    @Benchmark
    @BenchmarkTest(configuration = @BenchmarkConfiguration(warmup = @Warmup(iterations = 1, batchSize = 1, time = 5), measurement = @Measurement(batchSize = 1, time = 1, iterations = 1)))
    public void baseline_nowarmup() {

    }
}
