package com.ringbufferlab.junit.benchmark;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

@Component
public class BenchmarkSpringIntegration implements ApplicationContextAware {
    private static ApplicationContext context;
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }

    public static <T> T getBean(Class<T> bean) {
        return context.getBean(bean);
    }

    public static void initializeBeans(Object testInstance) {
        try {
            Class<?> clazz = testInstance.getClass();
            while (clazz != null && clazz.getCanonicalName().contains("jmh_generated")) {
                clazz = clazz.getSuperclass();
            }
            while (clazz != null) {
                for (Field f : clazz.getDeclaredFields()) {
                    f.setAccessible(true);
                    Autowired autowired = f.getAnnotation(Autowired.class);
                    if (autowired != null) {
                        f.set(testInstance, BenchmarkSpringIntegration.getBean(f.getType()));
                    }
                }
                clazz = clazz.getSuperclass();
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
