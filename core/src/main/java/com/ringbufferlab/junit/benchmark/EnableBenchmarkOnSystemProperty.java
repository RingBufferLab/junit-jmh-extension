package com.ringbufferlab.junit.benchmark;


import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * <b>Turns @Test annotated method into {@link org.openjdk.jmh.annotations.Benchmark} method when a system property match given name and value</b>
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface EnableBenchmarkOnSystemProperty {
    String name();
    String value();
}
