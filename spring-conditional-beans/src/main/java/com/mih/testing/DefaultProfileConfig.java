package com.mih.testing;

import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

@Configuration
public class DefaultProfileConfig {

    @Bean
    @ConditionalOnMissingBean(MyService.class)
    public MyService myService() {
        return new MyService("default");
    }
}
