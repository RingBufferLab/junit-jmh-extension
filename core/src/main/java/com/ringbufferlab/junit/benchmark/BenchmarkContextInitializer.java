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
