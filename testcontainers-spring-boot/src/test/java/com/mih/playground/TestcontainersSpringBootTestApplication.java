package com.mih.playground;

import org.springframework.boot.SpringApplication;

public class TestcontainersSpringBootTestApplication {

    public static void main(String[] args) {
        SpringApplication.from(TestcontainersSpringBootApplication::main)
                .with(Infrastructure.class)
                .run(args);
    }

}
