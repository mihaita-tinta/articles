package com.mih.testing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyService {
    private static final Logger log = LoggerFactory.getLogger(MyService.class);

    private final String name;

    public MyService(String name) {
        this.name = name;
    }

    public String name() {
        log.info("test - name: " + name);
        return name;
    }
}
