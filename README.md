[![Java CI with Maven](https://github.com/RingBufferLab/junit-jmh-extension/actions/workflows/java.yml/badge.svg)](https://github.com/RingBufferLab/junit-jmh-extension/actions/workflows/java.yml)

Turn your unit or integration test into benchmark with ease!

Supports `junit4` and `junit5`
# Prerequisite
- java >=1.8


# Limitations
Be sure to read this before using this extension.

- `junit4` currently does not support `runner`. Runner (like: `@MockitoJUnitRunner.class`) will simply not be triggered

## Limitations with Spring
- Behavior normally triggered by `org.springframework.test.context.junit.jupiter.SpringExtension`(junit5) or `org.springframework.test.context.junit4.SpringRunner`(junit4) are not supported (e.g: TestSecurityContext won't be initialized, etc..)

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
    <groupId>com.ringbufferlab</groupId>
    <artifactId>junit5-jmh-extension</artifactId>
    <version>0.1</version>
</dependency>
```
Minimal example
```java
@ExtendWith(JMHJUnitExtension.class)
@EnableBenchmark
public class MyTest {
    // No need to add @Test because already brought by @BenchmarkTest
    @Benchmark // Required by JMH to generate benchmark wrapper
    @BenchmarkTest(configuration = @BenchmarkConfiguration(warmup = @Warmup(iterations = 1, batchSize = 1, time = 5), measurement = @Measurement(batchSize = 1, time = 1, iterations = 1)))
    public void baseline() {

    }
}
```
Full example
```java
public abstract class CloneTestContextBase {
    @Mock
    GoodbyeBean goodbyeBean;
}
@State(Scope.Benchmark)
@EnableBenchmark
@ExtendWith(JMHJUnitExtension.class)
public class CloneTestContextTest extends CloneTestContextBase {
    
    private HelloBean helloBean;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        helloBean = new HelloBean();
        when(goodbyeBean.goodBye2()).thenCallRealMethod();
    }

    // Use Level.Trial/Iteration if test instance fields are immutable, if so we can initialize them only once before benchmark execution
    // Use Level.Invocation if test instance fields are mutate during benchmark invocation. But be sure to understand warnings of this Level before using it
    @Setup(Level.Trial)
    public void initialize() {
        // Will copy all fields of current test instance into Benchmark instance.
        BenchmarkContextInitializer.cloneFromTest(this);
    }

    @Test
    @Benchmark
    @BenchmarkTest(configuration = @BenchmarkConfiguration(warmup = @Warmup(iterations = 1, batchSize = 1, time = 1), measurement = @Measurement(batchSize = 1, time = 1, iterations = 1)))
    public void ensure_cloningContextFromTestHandleAnnotationMock() {
        assertThat(goodbyeBean.goodBye2()).isEqualTo("GoodBye2");
    }
}
```

## JUnit 4
```xml
<dependency>
    <groupId>com.ringbufferlab</groupId>
    <artifactId>junit4-jmh-rule</artifactId>
    <version>0.1</version>
</dependency>
```
Simple example
```java

@State(Scope.Benchmark) // If not set JMH annotation processor will complain jmhRule is not static and can't be used outside a @State.
@EnableBenchmark
public class MyTest {

    @Rule
    public JMHJUnitRule jmhRule = new JMHJUnitRule(this);

    @Test // Required by junit as in junit4 @Test can't be added on annotation type, it is not brought by our annotation @BenchmarkTest
    @Benchmark // Required by JMH to generate benchmark wrapper
    @BenchmarkTest(configuration = @BenchmarkConfiguration(warmup = @Warmup(iterations = 1, batchSize = 1, time = 5), measurement = @Measurement(batchSize = 1, time = 1, iterations = 1)))
    public void baseline() {

    }
}
```
Full example
```java
@State(Scope.Benchmark)
@EnableBenchmark
public class CloneTestContextTest extends CloneTestContextBase {

    @Rule
    public JMHJUnitRule jmhRule = new JMHJUnitRule(this);

    private HelloBean helloBean;

    @Before
    public void setup() {
        helloBean = new HelloBean();
        MockitoAnnotations.openMocks(this);
        when(goodbyeBean.goodBye2()).thenCallRealMethod();
    }

    // Use Level.Trial/Iteration if test instance fields are immutable, if so we can initialize them only once before benchmark execution
    // Use Level.Invocation if test instance fields are mutate during benchmark invocation. But be sure to understand warnings of this Level before using it
    @Setup(Level.Trial)
    public void initialize() {
        // Will copy all fields of current test instance into Benchmark instance.
        BenchmarkContextInitializer.cloneFromTest(this);
    }

    @Test
    @Benchmark
    @BenchmarkTest(configuration = @BenchmarkConfiguration(warmup = @Warmup(iterations = 1, batchSize = 1, time = 1), measurement = @Measurement(batchSize = 1, time = 1, iterations = 1)))
    public void ensure_cloningContextFromTestHandleAnnotationMock() {
        assertThat(goodbyeBean.goodBye2()).isEqualTo("GoodBye2");
    }
}
```
## Enable benchmark
There is `3` annotations to enable benchmark to run, to be add on test classes.


**Example below will run annotated method as benchmark instead of test when a system property called `enable-benchmark` is set to `yes` (`-Denable-benchmark=yes`)**
```
@EnableBenchmarkOnSystemProperty(name = "enable-benchmark", value = "yes")
```

**Example below will run annotated method as benchmark instead of test when an environment variable called `ENABLE_BENCHMARK` is set to `on` (`export ENABLE_BENCHMARK=on`)**
```
@EnableBenchmarkOnEnvironment(name = "ENABLE_BENCHMARK", value = "on")
```
**Example below will run annotated method as benchmark instead of test without condition**
```
@EnableBenchmark
```
**Example below will run annotated method as benchmark instead of test when static method "enableBenchmark" implemented on given class.**
```
@EnableBenchmark(EnableBenchmarkOnSunday.class)
```
With a custom implementation to run only on sunday
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
This extension automatically create a `@Tag`(junit5) or a `@Category` (junit4), named `com.ringbufferlab.BenchmarkTest` for method annotated with `@BenchmarkTest`.

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
                        <groups>com.ringbufferlab.BenchmarkTest</groups>
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
