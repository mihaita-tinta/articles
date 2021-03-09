package com.mihaita.articles.caching;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.stream.IntStream;

@SpringBootTest(classes = {MyServiceTestConfiguration.class, CompletableFutureCacheableConfiguration.class},
        properties = {
                "logging.level.org.springframework.cache=TRACE",
                "logging.level.com.mihaita.articles.caching=TRACE",
        })
@ExtendWith(SpringExtension.class)
class CompletableFutureCacheableMethodInterceptorTest {
    private static final Logger log = LoggerFactory.getLogger(CompletableFutureCacheableMethodInterceptorTest.class);

    @Autowired
    MyServiceTestConfiguration.MyCompletableFutureCachedService service;

    @Test
    public void testFutureCacheableCompletableFuture() {

        IntStream.range(0, 10)
                .mapToObj(i -> 0)
                .forEach(i -> service.getStuffById(i)
//                .forEach(i -> service.callFutureUpdatingCache("aa", i)
                        .thenAccept(res -> {
                            log.info("testFutureCacheableCompletableFuture - {} call thenAccept: {}", i, res);
                        })
                        .exceptionally(res -> {
                            log.info("testFutureCacheableCompletableFuture - {} call failed", i);
                            return null;
                        })
                );

    }

    @Test
    public void testEvict() {
        int i = 1;

        service.getStuffById(i)
                .thenAccept(res -> {
                    log.info("testEvict - {} call thenAccept: {}", i, res);
                })
                .exceptionally(res -> {
                    log.info("testEvict - {} call failed", i);
                    return null;
                })
                .join();

        service.callFutureUpdatingCache("aaa", i)
                .thenAccept(res -> {
                    log.info("testEvict - {} call thenAccept: {}", i, res);
                })
                .exceptionally(res -> {
                    log.info("testEvict - {} call failed", i, res);
                    return null;
                })
                .join();

        service.getStuffById(i)
                .thenAccept(res -> {
                    log.info("testEvict - {} call thenAccept: {}", i, res);
                })
                .exceptionally(res -> {
                    log.info("testEvict - {} call failed", i);
                    return null;
                })
                .join();

    }
}
