package com.ringbufferlab.junit.benchmark;

import org.junit.experimental.categories.Category;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Category(BenchmarkTest.class)
public @interface BenchmarkTest {
    BenchmarkConfiguration configuration() default @BenchmarkConfiguration;
}
