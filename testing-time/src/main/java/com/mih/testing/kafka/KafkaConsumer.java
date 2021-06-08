package com.mih.testing.kafka;

import com.mih.testing.PersonService;
import com.mih.testing.cassandra.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaConsumer.class);

    private final PersonService service;

    public KafkaConsumer(PersonService service) {
        this.service = service;
    }

    @KafkaListener(topics = "${test.topic}")
    public void receive(Person person) {
        LOGGER.info("receive person='{}'", person);
        service.save(person);
    }
}
