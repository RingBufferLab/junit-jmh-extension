package com.ringbufferlab.junit.benchmark;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface EnableBenchmark {
    Class<? extends EnableBenchmarkProvider> value() default EnableBenchmarkProvider.class;
}
