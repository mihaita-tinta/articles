package com.mihaita.articles.caching;

import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import java.util.Arrays;

@Configuration
@EnableAspectJAutoProxy
public class CompletableFutureCacheableConfiguration {

    @Bean
    public Advisor advisor() {
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression("@annotation(com.mihaita.articles.caching.CompletableFutureCacheable)");
        return new DefaultPointcutAdvisor(pointcut, new CompletableFutureCacheableMethodInterceptor(Arrays.asList("cf1", "cf2")));
    }

}
