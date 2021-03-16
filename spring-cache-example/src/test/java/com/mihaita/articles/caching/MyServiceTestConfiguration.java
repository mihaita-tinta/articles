package com.mihaita.articles.caching;

import org.slf4j.Logger;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.CompletableFuture;

import static org.slf4j.LoggerFactory.getLogger;

@TestConfiguration
public class MyServiceTestConfiguration {


    @Bean
    public MyService myService() {
        return new MyService();
    }

    @Bean
    public MyCompletableFutureService myCompletableFutureService() {
        return new MyCompletableFutureService();
    }
    @Bean
    public MyCompletableFutureCachedService myCompletableFutureCachedService() {
        return new MyCompletableFutureCachedService();
    }

    public static class MyService {
        private static final Logger log = getLogger(MyService.class);


        @Cacheable("default")
        public String getStuffById(int id) {
            log.trace("getStuffById - id: {}", id);
            // Some logic to call another api
            return "stuff - " + id;
        }
    }

    public static class MyCompletableFutureService {
        private static final Logger log = getLogger(MyCompletableFutureService.class);

        @Cacheable("default-cache")
        public CompletableFuture<String> getStuffById(int id) {
            log.trace("getStuffById - id: {}", id);
            if (id % 2 == 0) {
                CompletableFuture error = new CompletableFuture();
                error.completeExceptionally(new IllegalArgumentException("oops, failing for id: " + id));
                return error;
            }
            return CompletableFuture.supplyAsync(() -> "test");
        }
    }

    public static class MyCompletableFutureCachedService {
        private static final Logger log = getLogger(MyCompletableFutureCachedService.class);

        @CompletableFutureCacheable("cf1")
        public CompletableFuture<String> getStuffById(int id) {
            log.trace("getStuffById - id: {}", id);

            if (id % 2 == 0) {
                CompletableFuture error = new CompletableFuture();
                error.completeExceptionally(new IllegalArgumentException("oops, failing for id: " + id));
                return error;
            }
            return CompletableFuture.supplyAsync(() -> "test");
        }

        @CompletableFutureCacheable("cf2")
        @CompletableFutureCacheableEvict(value = "cf1", keySpel = "#p1")
        public CompletableFuture<String> callFutureUpdatingCache(String value, int counter) {
            log.info("callFutureUpdatingCache" + counter);
            if (counter % 2 == 0) {
                CompletableFuture error = new CompletableFuture();
                error.completeExceptionally(new IllegalArgumentException("oops, failing for counter: " + counter));
                return error;
            }
            return CompletableFuture.supplyAsync(() -> value + counter);
        }
    }

}
