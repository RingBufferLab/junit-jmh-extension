package com.ringbufferlab.junit.benchmark.test;

import com.ringbufferlab.junit.benchmark.BenchmarkConfiguration;
import com.ringbufferlab.junit.benchmark.BenchmarkContextInitializer;
import com.ringbufferlab.junit.benchmark.BenchmarkTest;
import com.ringbufferlab.junit.benchmark.EnableBenchmark;
import com.ringbufferlab.junit.benchmark.JMHJUnitRule;
import com.ringbufferlab.junit.benchmark.test.fixtures.HelloBean;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
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
public class CloneTestContextTest extends CloneTestContextBase {

    @Rule
    public JMHJUnitRule jmhRule = new JMHJUnitRule(this);

    private HelloBean helloBean;

    @Before
    public void setup() {
        helloBean = new HelloBean();
        MockitoAnnotations.openMocks(this);
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
