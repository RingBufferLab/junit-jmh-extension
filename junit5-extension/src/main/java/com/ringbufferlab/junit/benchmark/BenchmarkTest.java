package com.ringbufferlab.junit.benchmark;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Tag("com.ringbufferlab.junit.benchmark.BenchmarkTest")
@Test
public @interface BenchmarkTest {
    BenchmarkConfiguration configuration() default @BenchmarkConfiguration;
}
