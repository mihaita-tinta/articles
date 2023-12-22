package com.mih.playground;


import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.testcontainers.containers.CassandraContainer;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration
public class Infrastructure {


    @Bean
    @ServiceConnection
    CassandraContainer<?> cassandraContainer() {
        return (CassandraContainer) new CassandraContainer(DockerImageName.parse("cassandra:latest"))
                .withInitScript("schema.cql")
                .withExposedPorts(9042)
                .withReuse(true);
    }

    @Bean
    @ServiceConnection
    KafkaContainer kafkaContainer() {
        return new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:latest"))
                .withReuse(true);
    }
}
