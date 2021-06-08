package com.mih.testing;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.util.StopWatch;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@EmbeddedKafka
class TestingTimeApplicationTests {
    private static final Logger log = LoggerFactory.getLogger(TestingTimeApplicationTests.class);
    private static final StopWatch stopWatch = new StopWatch();

    @BeforeAll
    public static void timeStart() {
        log.info("timeStart");
        stopWatch.start("SpringBootTest");
    }

    @Test
    void contextLoads() {
    }

    @AfterAll
    public static void timeEnd() {
        stopWatch.stop();
        log.info("timeEnd: {}", stopWatch.prettyPrint());
    }

}
