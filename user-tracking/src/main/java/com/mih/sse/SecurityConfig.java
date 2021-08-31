package com.mih.sse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private static final Logger log = LoggerFactory.getLogger(SecurityConfig.class);

    @Autowired
    SseTemplate template;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {

        auth.inMemoryAuthentication()
                .withUser("user")
                .password("{noop}user")
                .roles("USER")
                .and()
                .withUser("admin")
                .password("{noop}admin")
                .roles("ADMIN");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .formLogin()
                .and()
                .authorizeRequests()
                .antMatchers("/admin/**")
                .hasRole("ADMIN")
                .and()
                .authorizeRequests()
                .anyRequest()
                .authenticated()
        ;
    }


    private static final Map<String, Integer> MAP = new ConcurrentHashMap<>();

    static {
        MAP.put("success", 0);
        MAP.put("failure", 0);
    }

    @EventListener
    public void authenticationSuccess(AuthenticationSuccessEvent event) {

        log.debug("authenticationSuccess - event: {}", event);

        MAP.put("success", MAP.compute("success", (key, value) -> ++value));

        template.broadcast("users", SseEmitter.event()
                .name("success")
                .data(Map.of("current", event.getAuthentication().getName(),
                        "total", MAP)));
    }

    @EventListener
    public void authenticationFailed(AbstractAuthenticationFailureEvent event) {

        log.debug("authenticationFailed - event: {}", event);

        MAP.put("failure", MAP.compute("failure", (key, value) -> ++value));

        template.broadcast("users", SseEmitter.event()
                .name("failure")
                .data(Map.of("current", event.getAuthentication().getName(),
                        "total", MAP)));
    }
}
