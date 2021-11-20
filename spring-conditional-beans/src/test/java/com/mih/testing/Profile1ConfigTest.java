package com.mih.testing;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("profile-1")
class Profile1ConfigTest {

    @Autowired
    MyService service;

    @Test
    public void test() {
            assertEquals("profile-1", service.name());
    }

}
