package com.mihaita.articles.caching;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cache.interceptor.SimpleKeyGenerator;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.StandardReflectionParameterNameDiscoverer;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.SpelParserConfiguration;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CompletableFutureCacheableMethodInterceptor implements MethodInterceptor {

    private static final Logger log = LoggerFactory.getLogger(CompletableFutureCacheableMethodInterceptor.class);
    private final Map<String, Cache> caches;
    private final KeyGenerator keyGenerator = new SimpleKeyGenerator();

    public CompletableFutureCacheableMethodInterceptor(List<String> caches) {
        this.caches = caches
                        .stream()
                        .map(s -> new ConcurrentMapCache(s))
                        .collect(Collectors.toMap(ConcurrentMapCache::getName, Function.identity()));
    }
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {

        boolean returnsCompletableFuture = CompletableFuture.class.isAssignableFrom(invocation.getMethod().getReturnType());

        if (!returnsCompletableFuture) {
            throw new IllegalArgumentException(invocation.getMethod() + " should return CompletableFuture");
        }

        CompletableFutureCacheable cacheAnnotation = invocation.getMethod().getAnnotation(CompletableFutureCacheable.class);

        String cacheName = cacheAnnotation.value();
        Object cacheKey = extractCacheKey(cacheAnnotation.keySpel(), invocation);

        CompletableFutureCacheableEvict evictAnnotation = invocation.getMethod().getAnnotation(CompletableFutureCacheableEvict.class);

        Cache.ValueWrapper valueWrapper = getCache(cacheName).get(cacheKey);;

        if (valueWrapper != null) {
            log.trace("invoke - value from cache for {}", invocation.getMethod());
            return CompletableFuture.completedFuture(valueWrapper.get());
        }

        return callMethod(invocation)
                .thenApply(newValue -> {
                    getCache(cacheName)
                            .put(cacheKey, newValue);

                    if (evictAnnotation != null) {
                        String cacheEvictName = evictAnnotation.value();
                        Object evictKey = extractCacheKey(evictAnnotation.keySpel(), invocation);
                        getCache(cacheEvictName)
                                .evictIfPresent(evictKey);
                    }
                    return newValue;
                });
    }

    private Cache getCache(String cacheName) {
        return caches.get(cacheName);
    }

    private CompletableFuture callMethod(MethodInvocation invocation) throws Throwable {
        Object ret = invocation.proceed();
        CompletableFuture future = (CompletableFuture) ret;
        return future;
    }

    private Object extractCacheKey(String keySpel, MethodInvocation invocation) {

        if (StringUtils.hasText(keySpel)) {
            SpelParserConfiguration config = new SpelParserConfiguration(true, true);
//            StandardEvaluationContext context = new StandardEvaluationContext(carPark);
            MethodBasedEvaluationContext context = new MethodBasedEvaluationContext(
                    invocation.getThis(),
                    invocation.getMethod(),
                    invocation.getArguments(),
                    new StandardReflectionParameterNameDiscoverer()
            );
            ExpressionParser expressionParser = new SpelExpressionParser(config);
            Expression expression = expressionParser.parseExpression(keySpel);
            return expression.getValue(context);
        }
        return keyGenerator.generate(invocation.getThis(), invocation.getMethod(), invocation.getArguments());
    }
}
