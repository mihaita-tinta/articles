package com.mih.completablefuture;

import com.twitter.finagle.GlobalRequestTimeoutException;
import com.twitter.finagle.Http;
import com.twitter.finagle.Service;
import com.twitter.finagle.http.Request;
import com.twitter.finagle.http.Response;
import com.twitter.finagle.param.HighResTimer;
import com.twitter.finagle.service.*;
import com.twitter.finagle.stats.NullStatsReceiver;
import com.twitter.finagle.util.DefaultTimer;
import com.twitter.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import scala.collection.immutable.Stream;

@Configuration
public class FinagleConfig {
    private static final Logger log = LoggerFactory.getLogger(FinagleConfig.class);

    @Bean
    public Service<Request, Response> httpClient(@Value("${wiremock.server.port:8080}") int port,
                                                 @Value("${global-timeout:5000}") int globalTimeout,
                                                 @Value("${request-timeout:1000}") int requestTimeout) {

        Duration timeoutDuration = Duration.fromMilliseconds(globalTimeout);
        final TimeoutFilter<Request, Response> timeoutFilter = new TimeoutFilter<>(
                timeoutDuration,
                new GlobalRequestTimeoutException(timeoutDuration),
                DefaultTimer.getInstance()
        );

        Stream<Duration> backoff = Backoff.exponentialJittered(Duration.fromMilliseconds(100), Duration.fromMilliseconds(30_000));
        RetryExceptionsFilter<Request, Response> rt = new RetryExceptionsFilter<>(
                RetryPolicy.backoffJava(Backoff
                                .toJava(backoff),
                        RetryPolicy.TimeoutAndWriteExceptionsOnly()), HighResTimer.Default(), NullStatsReceiver.get());

        RetryBudget budget = RetryBudgets.newRetryBudget(Duration.fromMilliseconds(1000), 10, 1);
        Http.Client client = Http.client()
                .withRetryBudget(budget)
                .withRetryBackoff(backoff)
                .withRequestTimeout(Duration.fromMilliseconds(requestTimeout));

        return new LogFilter()
                .andThen(timeoutFilter)
                .andThen(rt)
                .andThen(client.newService(":" + port));

    }
}
