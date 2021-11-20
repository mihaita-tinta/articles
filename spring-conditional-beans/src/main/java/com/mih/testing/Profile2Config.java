package com.mih.testing;

import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("profile-2")
@AutoConfigureOrder(90)
public class Profile2Config {

    @Bean
    public MyService myService() {
        return new MyService("profile-2");
    }
}
