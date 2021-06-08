 package com.mih.testing.cassandra;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = CassandraConfig.class)
class PersonRepository25Test {

    @Autowired
    PersonRepository repository;

    @Test
    public void testFindAll() {
        List<Person> junit = repository.findAll();
        assertEquals(1, junit.size());
    }

    @Test
    public void testInsert() {
        Person person = new Person("id-1", "junit-name", 20);
        Person saved = repository.save(person);
        assertNotNull(saved);
    }
}