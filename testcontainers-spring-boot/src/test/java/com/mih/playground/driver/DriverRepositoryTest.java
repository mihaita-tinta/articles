package com.mih.playground.driver;

import com.datastax.driver.core.utils.UUIDs;
import com.mih.playground.Infrastructure;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.cassandra.DataCassandraTest;
import org.springframework.context.annotation.Import;

import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@DataCassandraTest
@Import(Infrastructure.class)
class DriverRepositoryTest {

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