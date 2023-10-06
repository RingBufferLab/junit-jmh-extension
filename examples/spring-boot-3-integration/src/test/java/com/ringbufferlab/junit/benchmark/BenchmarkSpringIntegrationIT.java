package com.ringbufferlab.junit.benchmark;

import com.ringbufferlab.junit.benchmark.fixtures.GoodbyeBean;
import com.ringbufferlab.junit.benchmark.fixtures.HelloBean;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;


@ExtendWith(JMHJUnitExtension.class)
@EnableBenchmark
@State(Scope.Benchmark)
@SpringBootTest(classes = Application.class)
public class BenchmarkSpringIntegrationIT {
    @Autowired
    private HelloBean helloBean;

    @SpyBean
    private GoodbyeBean goodbyeBean;

    @Mock
    private GoodbyeBean goodbyeBeanMock;

    @BeforeEach
    public void setup() {
        when(goodbyeBean.goodBye2()).thenReturn("GoodBye3");
        when(goodbyeBeanMock.goodBye()).thenReturn("GoodBye mock");
    }

    @Setup(Level.Trial)
    public void initialize() {
        BenchmarkContextInitializer.cloneFromTest(this);
    }

    @Test
    @Benchmark
    @BenchmarkTest(configuration = @BenchmarkConfiguration(warmup = @Warmup(iterations = 1, time = 1, batchSize = 1), measurement = @Measurement(iterations = 1, time = 1, batchSize = 1)))
    public void test() {
        // Given
        // When
        // Then
        assertThat(helloBean.hello()).isEqualTo("Hello world");
        assertThat(goodbyeBean.goodBye()).isEqualTo("GoodBye");
        assertThat(goodbyeBean.goodBye2()).isEqualTo("GoodBye3");
        assertThat(goodbyeBeanMock.goodBye()).isEqualTo("GoodBye mock");
    }
}
