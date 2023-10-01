/*
 * Copyright 2023 RingBufferLab
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ringbufferlab.junit.benchmark;


import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Timeout;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.profile.Profiler;
import org.openjdk.jmh.results.format.ResultFormatType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <b>BenchmarkConfiguration annotation allows to set configuration for benchmarks</b>
 * <p>This annotation may be put in @BenchmarkTest annotation to have effect on the annotated
 * method only, or on class (or superclass) to have the effect over all BenchmarkTest annotated methods in the class</p>
 * <p>If BenchmarkConfiguration is provided inside BenchmarkTest annotation and is also defined on class,
 * the configuration to be used will be the one define in BenchmarkTest at method level</p>
 * <p>If not value are provided JMH default values will be used: {@link org.openjdk.jmh.runner.Defaults}</p>
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface BenchmarkConfiguration {
    /**
     * @return JMH {@link org.openjdk.jmh.annotations.Fork} configuration
     */
    Fork[] fork() default {};

    /**
     * @return JMH {@link org.openjdk.jmh.annotations.BenchmarkMode} configuration
     */
    BenchmarkMode[] mode() default {};

    /**
     * @return JMH {@link org.openjdk.jmh.annotations.Warmup} configuration
     */
    Warmup[] warmup() default {};

    /**
     * @return JMH {@link org.openjdk.jmh.annotations.Timeout} configuration
     */
    Timeout[] timeout() default {};

    /**
     * @return JMH {@link org.openjdk.jmh.annotations.Measurement} configuration
     */
    Measurement[] measurement() default {};

    /**
     * @return Number of threads to run the benchmark in
     */
    int threads() default 0;

    /**
     * @return Operations per invocation.
     */
    int operationPerInvocation() default 0;

    /**
     * @return JMH {@link org.openjdk.jmh.profile.Profiler} implementation to use
     */
    Class<? extends Profiler>[] profilers() default {};

    /**
     * @return path to file containing benchmark results.
     */
    String resultFile() default "";

    /**
     *
     * @return {@link org.openjdk.jmh.results.format.ResultFormatType} of the benchmar result file.
     */
    ResultFormatType resultFormat() default ResultFormatType.CSV;
}