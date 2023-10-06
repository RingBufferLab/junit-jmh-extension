package com.ringbufferlab.junit.benchmark.fixtures;

import org.springframework.stereotype.Component;

@Component
public class HelloBean {

    public String hello() {
        return "Hello world";
    }
}
