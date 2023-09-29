package com.ringbufferlab.junit.benchmark;


import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Timeout;
import org.openjdk.jmh.annotations.Warmup;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface BenchmarkConfiguration {
    Fork[] fork() default {};

    BenchmarkMode[] mode() default {};

    Warmup[] warmup() default {};

    Timeout[] timeout() default {};

    Measurement[] measurement() default {};

    int threads() default 0;

    int operationPerInvocation() default 0;
}