package com.mih.playground;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;

@SpringBootApplication
@EnableCassandraRepositories
public class TestcontainersSpringBootApplication {

	public static void main(String[] args) {
		SpringApplication.run(TestcontainersSpringBootApplication.class, args);
	}

}
