package com.mihaita.articles.caching;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
@EnableCaching
public class MyCacheConfiguration extends CachingConfigurerSupport {

//    @Bean
//    @Override
//    public CacheManager cacheManager() {
//        CompositeCacheManager cacheManager = new CompositeCacheManager();
////        cacheManager.setFallbackToNoOpCache(true);
//        return cacheManager;
//    }

    @Bean
    @Override
    public CacheManager cacheManager() {
        SimpleCacheManager cacheManager = new SimpleCacheManager();
        cacheManager.setCaches(Arrays.asList(new ConcurrentMapCache("default")));
        return cacheManager;
    }

}
