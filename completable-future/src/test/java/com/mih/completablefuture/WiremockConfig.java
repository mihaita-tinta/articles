package com.mih.completablefuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.contract.wiremock.WireMockConfigurationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WiremockConfig {
    private static final Logger log = LoggerFactory.getLogger(WiremockConfig.class);

    @Bean
    WireMockConfigurationCustomizer optionsCustomizer() {
        return options -> {
            options.asynchronousResponseEnabled(true);
            options.asynchronousResponseThreads(30);
            options.jettyAcceptQueueSize(100_000);
            log.info("changing wiremock config: {}", options);
        };
    }
}
