package com.ringbufferlab.junit.benchmark;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class BenchmarkContextInitializer {

    public static void cloneFromTest(Object benchmarkInstance) {
        try {
            Class<?> clazz = benchmarkInstance.getClass();
            while (clazz != null && clazz.getCanonicalName().contains("jmh_generated")) {
                clazz = clazz.getSuperclass();
            }
            Class<?> testInstanceClass = BenchmarkContext.getTestInstance().getClass();
            while (clazz != null && testInstanceClass != null) {
                for (Field f : clazz.getDeclaredFields()) {
                    f.setAccessible(true);
                    Field testInstanceField = testInstanceClass.getDeclaredField(f.getName());
                    testInstanceField.setAccessible(true);
                    if (Modifier.isFinal(f.getModifiers())) {
                        continue;
                    }
                    f.set(benchmarkInstance, testInstanceField.get(BenchmarkContext.getTestInstance()));
                }
                clazz = clazz.getSuperclass();
                testInstanceClass = testInstanceClass.getSuperclass();
            }
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }
}
