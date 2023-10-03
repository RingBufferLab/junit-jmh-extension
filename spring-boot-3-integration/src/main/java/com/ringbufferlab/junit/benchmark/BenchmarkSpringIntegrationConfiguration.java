package com.ringbufferlab.junit.benchmark;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@AutoConfiguration
@ComponentScan(value = "com.ringbufferlab.junit.benchmark")
public class BenchmarkSpringIntegrationConfiguration {
}
