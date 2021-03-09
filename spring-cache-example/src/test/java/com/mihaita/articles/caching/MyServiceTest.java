package com.mihaita.articles.caching;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.stream.IntStream;


@SpringBootTest(classes = {MyServiceTestConfiguration.class, MyCacheConfiguration.class},
        properties = {"logging.level.org.springframework.cache=TRACE",
                "logging.level.com.mihaita.articles.caching=TRACE"})
@ExtendWith(SpringExtension.class)
class MyServiceTest {
    private static final Logger log = LoggerFactory.getLogger(MyServiceTest.class);

    @Autowired
    MyServiceTestConfiguration.MyService service;
    @Autowired
    MyServiceTestConfiguration.MyCompletableFutureService completableFutureService;

    @Test
    public void test() {
        IntStream.range(0, 10)
                .forEach(i -> service.getStuffById(1));
    }


    @Test
    public void testCompletableFuture() {

        IntStream.range(0, 10)
                .mapToObj(i -> 0)
                .forEach(i -> completableFutureService.getStuffById(i)
                        .thenAccept(res -> {
                            log.info("testCompletableFuture - {} call thenAccept: {}", i, res);
                        })
                        .exceptionally(res -> {
                            log.info("testCompletableFuture - {} call failed", i);
                            return null;
                        })
                );

    }

    @Test
    public void testFutureCacheableCompletableFuture() {

        IntStream.range(0, 10)
                .forEach(i -> completableFutureService.getStuffById(i%3)
                        .thenAccept(res -> {
                            log.info("testFutureCacheableCompletableFuture - {} call thenAccept: {}", i, res);
                        })
                        .exceptionally(res -> {
                            log.info("testFutureCacheableCompletableFuture - {} call failed", i);
                            return null;
                        })
                );

    }


}
