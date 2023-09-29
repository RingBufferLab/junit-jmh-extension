[![Java CI with Maven](https://github.com/RingBufferLab/junit-jmh-extension/actions/workflows/java.yml/badge.svg)](https://github.com/RingBufferLab/junit-jmh-extension/actions/workflows/java.yml)

Turn your unit or integration test into benchmark with ease!

Supports `junit4` and `junit5`
# Prerequisite
- java >=1.8

# Usage
## Prerequisite
Add and configure JMH annotation processor 
```xml
<project>
    <properties>
        <java.version>8</java.version>
        <jmh.version>1.37</jmh.version>
    </properties>
    <dependencies>
        <dependency>
            <groupId>org.openjdk.jmh</groupId>
            <artifactId>jmh-core</artifactId>
            <version>${jmh.version}</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.openjdk.jmh</groupId>
            <artifactId>jmh-generator-annprocess</artifactId>
            <version>${jmh.version}</version>
            <scope>test</scope>
        </dependency>
    </dependencies>
    
    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>${maven-compiler-plugin.version}</version>
                <configuration>
                    <source>${java.version}</source>
                    <target>${java.version}</target>
                    <annotationProcessorPaths>
                        <path>
                            <groupId>org.openjdk.jmh</groupId>
                            <artifactId>jmh-generator-annprocess</artifactId>
                            <version>${jmh.version}</version>
                        </path>
                    </annotationProcessorPaths>
                    <annotationProcessors>org.openjdk.jmh.generators.BenchmarkProcessor</annotationProcessors>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

## JUnit 5
```xml
<dependency>
    <groupId>com.ringbufferlab.junit.benchmark</groupId>
    <artifactId>junit5-extension</artifactId>
</dependency>
```
```java
@ExtendWith(JMHJUnitExtension.class)
@EnableBenchmark
public class MyTest {
    // No need to add @Test
    @Benchmark
    @BenchmarkTest(configuration = @BenchmarkConfiguration(warmup = @Warmup(iterations = 1, batchSize = 1, time = 5), measurement = @Measurement(batchSize = 1, time = 1, iterations = 1)))
    public void baseline() {

    }
}
```

## JUnit 4
```xml
<dependency>
    <groupId>com.ringbufferlab.junit.benchmark</groupId>
    <artifactId>junit4-extension</artifactId>
</dependency>
```
```java

@State(Scope.Benchmark) // If not set JMH annotation processor will complain jmhRule is not static and can't be used outside a @State.
@EnableBenchmark
public class MyTest {

    @Rule
    public JMHJUnitRule jmhRule = new JMHJUnitRule();

    @Test
    @Benchmark
    @BenchmarkTest(configuration = @BenchmarkConfiguration(warmup = @Warmup(iterations = 1, batchSize = 1, time = 5), measurement = @Measurement(batchSize = 1, time = 1, iterations = 1)))
    public void baseline() {

    }
}
```

## Enable benchmark
There is `3` annotations to enable benchmark to run, to be add on test classes.


Will run annotated method as benchmark instead of test when a system property called `enable-benchmark` is set to `yes` (`-Denable-benchmark=yes`)
```
@EnableBenchmarkOnSystemProperty(name = "enable-benchmark", value = "yes")
```

Will run annotated method as benchmark instead of test when an environment variable called `ENABLE_BENCHMARK` is set to `on` (`export ENABLE_BENCHMARK=on`)
```
@EnableBenchmarkOnEnvironment(name = "ENABLE_BENCHMARK", value = "on")
```
Will run benchmark instead of test without condition
```
@EnableBenchmark
```

Will run benchmark instead of test when static method "enableBenchmark" implemented on given class.
```
@EnableBenchmark(EnableBenchmarkOnSunday.class)
```
```java
public class EnableBenchmarkOnSunday {
    public static boolean enableBenchmark() {
        return Instant.now().get(ChronoField.DAY_OF_WEEK) == 7;
    }
}
```
This feature could be used by spring boot user to enable benchmark base on spring context
```java
@Component
public class EnableBenchmarkOnContext implements ApplicationContextAware {
    private static ApplicationContext context;

    public static boolean enableBenchmark() {
        return context.getEnvironment().matchesProfiles("benchmark");
    }
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }
}
```

# Running only benchmark
This extension automatically create a `@Tag`(junit5) or a `@Category` (junit4), named `com.ringbufferlab.junit.benchmark.BenchmarkTest` for method annotated with `@BenchmarkTest`.

By configuring maven surfire plugin it is possible to run only benchmark tests
```xml 
<profiles>
    <profile>
        <id>benchmark</id>
        <properties>
            <enable.benchmark>true</enable.benchmark>
        </properties>
        <build>
            <plugins>
                <plugin>
                    <groupId>org.apache.maven.plugins</groupId>
                    <artifactId>maven-surefire-plugin</artifactId>
                    <version>${maven-surfire-plugin.version}</version>
                    <configuration>
                        <groups>com.ringbufferlab.junit.benchmark.BenchmarkTest</groups>
                        <systemProperties>
                            <benchmark>${enable.benchmark}</benchmark>
                        </systemProperties>
                    </configuration>
                </plugin>
            </plugins>
        </build>
    </profile>
</profiles>
```

```java
@ExtendWith(JMHJUnitExtension.class)
@EnableBenchmarkOnSystemProperty(name = "benchmark", value = "true")
public class ExtensionTest {

    @Benchmark
    @BenchmarkTest(configuration = @BenchmarkConfiguration(warmup = @Warmup(iterations = 1, batchSize = 1, time = 5), measurement = @Measurement(batchSize = 1, time = 1, iterations = 1)))
    public void baseline() {

    }
    
    @Test
    public void standardTest() {
        // Won't run when maven profile "benchmark" is active
    }
}
```