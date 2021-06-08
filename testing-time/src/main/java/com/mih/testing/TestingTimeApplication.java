package com.mih.testing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.metrics.jfr.FlightRecorderApplicationStartup;

@SpringBootApplication
public class TestingTimeApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(TestingTimeApplication.class);
        app.setApplicationStartup(new FlightRecorderApplicationStartup());
        app.run(args);
    }

}
