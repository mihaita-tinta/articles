package com.mih.playground.driver;

import com.datastax.driver.core.utils.UUIDs;
import com.mih.playground.Infrastructure;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.cassandra.DataCassandraTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.CassandraContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@DataCassandraTest
@TestPropertySource(properties = "logging.level.org.springframework.data.cassandra.core.cql.CqlTemplate=DEBUG")
@Testcontainers
class DriverRepositoryTestcontainersTest {

    @Container
    public static CassandraContainer<?> cassandraContainer = (CassandraContainer) new CassandraContainer(DockerImageName.parse("cassandra:latest"))
                .withInitScript("schema.cql")
                .withExposedPorts(9042)
                .withReuse(true);

    @DynamicPropertySource
    static void setupCassandraConnectionProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.cassandra.contact-points", () -> "127.0.0.1");
        registry.add("spring.cassandra.port", () -> cassandraContainer.getMappedPort(9042));
    }
    @Autowired
    DriverRepository repository;

    @Test
    void test() {
        Driver driver = new Driver(UUIDs.timeBased(), ZonedDateTime.now().toEpochSecond(), "nickname");
        Driver db = repository.save(driver);

        assertNotNull(db.getId());
        assertEquals(1, repository.findAll().size());
    }

}