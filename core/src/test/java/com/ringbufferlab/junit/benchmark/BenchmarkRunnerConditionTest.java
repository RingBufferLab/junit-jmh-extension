package com.ringbufferlab.junit.benchmark;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

public class BenchmarkRunnerConditionTest {

    @Test
    public void shouldRunBenchmark_shouldReturnTrue_whenClassHasEnableBenchmarkAnnotationAndMethodHasBenchmarkTestAnnotation() throws NoSuchMethodException {
        // Given
        Method testCase = TestCase3.class.getMethod("testCase");
        // When
        boolean canRunBenchmark = BenchmarkRunnerCondition.shouldRunBenchmark(testCase);
        // Then
        assertThat(canRunBenchmark).isTrue();
    }

    @Test
    public void shouldRunBenchmark_shouldReturnTrue_whenClassHasEnableBenchmarkAnnotationAndClassHasBenchmarkTestAnnotation() throws NoSuchMethodException {
        // Given
        Method testCase = TestCase2.class.getMethod("testCase");
        // When
        boolean canRunBenchmark = BenchmarkRunnerCondition.shouldRunBenchmark(testCase);
        // Then
        assertThat(canRunBenchmark).isTrue();
    }

    @Test
    public void shouldRunBenchmark_shouldReturnTrue_whenClassHasEnableBenchmarkOnSystemPropertyMatchingSystemPropsProvided() throws NoSuchMethodException {
        // Given
        Method testCase = TestCase4.class.getMethod("testCase");
        System.setProperty("enable-benchmark", "yes");
        // When
        boolean canRunBenchmark = BenchmarkRunnerCondition.shouldRunBenchmark(testCase);
        // Then
        assertThat(canRunBenchmark).isTrue();
    }

    @Test
    public void shouldRunBenchmark_shouldReturnFalse_whenClassHasEnableBenchmarkOnSystemPropertyNOTMatchingSystemPropsProvided() throws NoSuchMethodException {
        // Given
        Method testCase = TestCase4.class.getMethod("testCase");
        System.setProperty("enable-benchmark", "true");
        // When
        boolean canRunBenchmark = BenchmarkRunnerCondition.shouldRunBenchmark(testCase);
        // Then
        assertThat(canRunBenchmark).isFalse();
    }

    @Test
    public void shouldRunBenchmark_shouldReturnFalse_whenClassHasEnableBenchmarkAnnotationWithCustomImplementation() throws NoSuchMethodException {
        // Given
        Method testCase = TestCase5.class.getMethod("testCase");
        // When
        boolean canRunBenchmark = BenchmarkRunnerCondition.shouldRunBenchmark(testCase);
        // Then
        assertThat(canRunBenchmark).isFalse();
    }

    @Test
    public void shouldRunBenchmark_whenClassHasEnableBenchmarkAnnotationWithCustomImplementation() throws NoSuchMethodException {
        // Given
        Method testCase = TestCase6.class.getMethod("testCase");
        // When
        MyBenchmarkEnabler.setEnable(false);
        boolean canRunBenchmark = BenchmarkRunnerCondition.shouldRunBenchmark(testCase);
        // Then
        assertThat(canRunBenchmark).isFalse();
        // When
        MyBenchmarkEnabler.setEnable(true);
        canRunBenchmark = BenchmarkRunnerCondition.shouldRunBenchmark(testCase);
        // Then
        assertThat(canRunBenchmark).isTrue();
    }

    @EnableBenchmark
    static class TestCase1 {
        public void testCase() {
        }
    }

    @EnableBenchmark
    @BenchmarkConfiguration
    static class TestCase2 {
        public void testCase() {
        }
    }

    @EnableBenchmark
    static class TestCase3 {
        @BenchmarkTest
        public void testCase() {
        }
    }

    @EnableBenchmarkOnSystemProperty(name = "enable-benchmark", value = "yes")
    static class TestCase4 {
        @BenchmarkTest
        public void testCase() {
        }
    }

    @EnableBenchmark(MyBenchmarkEnablerAlwaysFalse.class)
    static class TestCase5 {
        @BenchmarkTest
        public void testCase() {
        }
    }

    @EnableBenchmark(MyBenchmarkEnabler.class)
    static class TestCase6 {
        @BenchmarkTest
        public void testCase() {
        }
    }

    static class MyBenchmarkEnablerAlwaysFalse extends EnableBenchmarkProvider {
        public static boolean enableBenchmark() {
            return false;
        }
    }

    static class MyBenchmarkEnabler extends EnableBenchmarkProvider {
        private static boolean enable = false;
        public static boolean enableBenchmark() {
            return enable;
        }

        public static void setEnable(boolean enable) {
            MyBenchmarkEnabler.enable = enable;
        }
    }
}
