package com.ringbufferlab.junit.benchmark.fixtures;

import org.springframework.stereotype.Component;

@Component
public class GoodbyeBean {
    public String goodBye() {
        return "GoodBye";
    }
    public String goodBye2() {
        return "GoodBye2";
    }
}
