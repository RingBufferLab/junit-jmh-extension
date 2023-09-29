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
