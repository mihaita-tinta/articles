package com.mih.testing.cassandra;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PersonRepository extends CassandraRepository<Person, String> {

    List<Person> findByNameAndAgeAfter(
            final String firstName, final int age);

    @Query(allowFiltering = true)
    List<Person> findByName(final String name);
}
