package com.mih.completablefuture;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
//        "management.endpoints.web.exposure.include=*",
//        "spring.boot.admin.client.url=http://localhost:8080",
        "logging.level.com.mih.completablefuture=DEBUG"
}, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureMockMvc
@AutoConfigureWireMock(port = 0)
class BalanceServiceTest {
    private static final Logger log = LoggerFactory.getLogger(BalanceServiceTest.class);
    @Autowired
    private BalanceService service;

    @Test
    public void test() throws Exception {

        service.getBalance("123")
                .get();
    }

    @Test
    public void testWebFlux() {
        Mono<Balance> balance = service.getBalanceWebFlux("123")
                .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(1))
                        .filter(exception -> exception instanceof WebClientResponseException &&
                                ((WebClientResponseException) exception).getStatusCode().is5xxServerError())
                );

        assertNotNull(balance.block());
    }

}
