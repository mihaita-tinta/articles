 package com.mih.testing.kafka;

import com.mih.testing.PersonService;
import com.mih.testing.cassandra.Person;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.test.context.EmbeddedKafka;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE,
classes = {KafkaAutoConfiguration.class, KafkaConsumer.class, KafkaProducer.class})
@EmbeddedKafka
class EmbeddedKafka20Test {

    @Autowired
    private KafkaProducer producer;

    @MockBean
    PersonService service;

    @Value("${test.topic}")
    private String topic;

    @Test
    public void test() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        when(service.save(any())).thenAnswer(r -> {
            latch.countDown();
            return r.getArgument(0);
        });

        Person person = new Person("id-123", "test", 20);

        producer.send(topic, person);

        latch.await(10, TimeUnit.SECONDS);
        verify(service).save(any());
    }
}