package com.mih.testing;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SpringConditionalBeansApplication {

    @Autowired
    MyService service;

    @Bean
    public CommandLineRunner runner() {
        return args -> service.name();
    }
    public static void main(String[] args) {
        SpringApplication.run(SpringConditionalBeansApplication.class, args);
    }

}
