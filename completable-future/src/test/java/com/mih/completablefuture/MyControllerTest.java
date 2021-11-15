package com.mih.completablefuture;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "management.endpoints.web.exposure.include=*",
        "spring.boot.admin.client.url=http://localhost:8080",
        "logging.level.com.mih.completablefuture=DEBUG"
}, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureMockMvc
@AutoConfigureWireMock(port = 0)
class MyControllerTest {
    private static final Logger log = LoggerFactory.getLogger(MyControllerTest.class);
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testAccountDetailsOneCall() throws Exception {

        this.mockMvc.perform(get("/api/accounts/details"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    public void testAccountsDetailsSequential() {
        IntStream.range(0, 20)
                .forEach(i -> {
                    try {

                        this.mockMvc.perform(get("/api/accounts/details"))
                                .andDo(print())
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.length()").value(1));
                    } catch (Exception e) {
                        throw new IllegalStateException("Failed with: " + e);
                    }
                });
    }

    @Test
    public void testAccountsDetails() throws Exception {

        log.debug("CPU Core: " + Runtime.getRuntime().availableProcessors());
        log.debug("CommonPool Parallelism: " + ForkJoinPool.commonPool().getParallelism());
        log.debug("CommonPool Common Parallelism: " + ForkJoinPool.getCommonPoolParallelism());
        int count = 10_000;
        CountDownLatch latch = new CountDownLatch(1);
        ExecutorService executor = Executors.newFixedThreadPool(100);
        List<CompletableFuture<Void>> cfs = IntStream.range(0, count)
                .mapToObj(i ->
                        CompletableFuture.runAsync(() ->
                        {
                            try {

                                this.mockMvc.perform(get("/api/accounts/details"))
                                        .andDo(print())
                                        .andExpect(status().isOk())
                                        .andExpect(jsonPath("$.length()").value(1));
                            } catch (Exception e) {
                                throw new IllegalStateException(e);
                            }
                        }, executor))
                .collect(Collectors.toList());

        AtomicBoolean hasErrors = new AtomicBoolean();
        CompletableFuture.allOf(cfs.toArray(new CompletableFuture[]{}))
                .handle((v, err) -> {
                    hasErrors.set(err != null);
                    log.debug("testAccountsDetails - finish with: " + err);
                    latch.countDown();
                    return null;
                });
        latch.await();
        Assertions.assertFalse(hasErrors.get());
    }

    @Test
    public void testAccountsDetailsAsync() throws Exception {

        log.debug("CPU Core: " + Runtime.getRuntime().availableProcessors());
        log.debug("CommonPool Parallelism: " + ForkJoinPool.commonPool().getParallelism());
        log.debug("CommonPool Common Parallelism: " + ForkJoinPool.getCommonPoolParallelism());
        int count = 10_000;
        CountDownLatch latch = new CountDownLatch(1);
        ExecutorService executor = Executors.newFixedThreadPool(100);
        List<CompletableFuture<Void>> cfs = IntStream.range(0, count)
                .mapToObj(i ->
                        CompletableFuture.runAsync(() ->
                        {
                            try {

                                MvcResult mvcResult = mockMvc.perform(
                                                get("/api/accounts/details-async"))
                                        .andReturn();

                                this.mockMvc.perform(MockMvcRequestBuilders.asyncDispatch(mvcResult))
                                        .andDo(print())
                                        .andExpect(status().isOk())
                                        .andExpect(jsonPath("$.length()").value(1));
                            } catch (Exception e) {
                                throw new IllegalStateException(e);
                            }
                        }, executor))
                .collect(Collectors.toList());

        AtomicBoolean hasErrors = new AtomicBoolean();
        CompletableFuture.allOf(cfs.toArray(new CompletableFuture[]{}))
                .handle((v, err) -> {
                    hasErrors.set(err != null);
                    log.debug("testAccountsDetails - finish with: " + err);
                    latch.countDown();
                    return null;
                });
        latch.await();
        Assertions.assertFalse(hasErrors.get());
    }

}
