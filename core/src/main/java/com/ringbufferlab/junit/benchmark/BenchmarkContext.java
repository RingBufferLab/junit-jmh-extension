package com.ringbufferlab.junit.benchmark;

class BenchmarkContext {
    private static Object testInstance;

    static Object getTestInstance() {
        return testInstance;
    }

    static void setTestInstance(Object testInstance) {
        BenchmarkContext.testInstance = testInstance;
    }
}
