package com.mih.messenger;

import io.github.webauthn.EnableWebAuthn;
import io.github.webauthn.config.WebAuthnConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
@EnableWebAuthn
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private static final Logger log = LoggerFactory.getLogger(SecurityConfig.class);


    @Override
    public void configure(WebSecurity web) {
        web
                .ignoring()
                .antMatchers(
                        "/login.html",
                        "/register.html",
                        "/node_modules/**",
                        "/error"
                );
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests()
                .anyRequest()
                .authenticated()
                .and()
                .apply(new WebAuthnConfigurer()
                        .defaultLoginSuccessHandler((user, credentials) -> log.info("user logged in: {}", user))
                        .registerSuccessHandler(user -> {
                            log.info("new user registered: {}", user);
                        })
                );
    }
}
