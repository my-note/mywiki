# GuavaRetry


```xml

        <dependency>
            <groupId>com.github.rholder</groupId>
            <artifactId>guava-retrying</artifactId>
            <version>2.0.0</version>
        </dependency>

```



```java

Retryer<Boolean> retryer = RetryerBuilder.<Boolean>newBuilder()
        .retryIfResult(Predicates.equalTo(false))
        .retryIfRuntimeException()
        .retryIfException()
        .withStopStrategy(StopStrategies.neverStop())
        .build();
retryer.call(()->{
    scene.setSceneKey(IDUtil.verifyCode());
    return sceneService.save(scene);
});

```






















