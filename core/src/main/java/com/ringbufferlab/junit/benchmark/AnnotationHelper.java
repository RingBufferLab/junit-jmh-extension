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

import java.lang.annotation.Annotation;
import java.util.Optional;

public class AnnotationHelper {

    public static <T extends Annotation> Optional<T> getParentAnnotation(Class<?> clazz, Class<T> annotation) {
        if (clazz.isAnnotationPresent(annotation)) {
            return Optional.of(clazz.getAnnotation(annotation));
        }
        Class<?> parentClass = clazz.getSuperclass();
        while (parentClass != null) {
            if (parentClass.isAnnotationPresent(annotation)) {
                return Optional.of(parentClass.getAnnotation(annotation));
            }
            parentClass = parentClass.getSuperclass();
        }
        return Optional.empty();
    }
}
