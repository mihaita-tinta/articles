package com.mih.playground.driver;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DriverRepository extends CassandraRepository<Driver, UUID> {
}