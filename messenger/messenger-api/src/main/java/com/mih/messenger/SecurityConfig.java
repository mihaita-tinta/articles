package com.mih.messenger;

import com.mih.messenger.domain.User;
import com.mih.messenger.domain.UserRepository;
import com.mih.messenger.rest.model.UserMapper;
import io.github.webauthn.EnableWebAuthn;
import io.github.webauthn.config.WebAuthnConfigurer;
import io.github.webauthn.config.WebAuthnUsernameAuthenticationToken;
import io.github.webauthn.domain.WebAuthnUser;
import io.github.webauthn.domain.WebAuthnUserRepository;
import io.github.webauthn.webflux.WebAuthnWebFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.util.matcher.NegatedServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.function.Supplier;

@Configuration
@EnableWebFluxSecurity
@EnableWebAuthn
public class SecurityConfig {
    private static final Logger log = LoggerFactory.getLogger(SecurityConfig.class);

    @Autowired
    UserRepository userRepository;
    @Autowired
    WebAuthnUserRepository webAuthnUserRepository;
    @Autowired
    Supplier<WebAuthnWebFilter> webAuthnWebFilterFactory;

    @Bean
    public RouterFunction<ServerResponse> imgRouter() {
        return RouterFunctions
                .resources("/**", new ClassPathResource("/webauthn/"));
    }
    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {

        http
                .securityMatcher(new NegatedServerWebExchangeMatcher(ServerWebExchangeMatchers.pathMatchers(
                        "/login.html",
                        "/register.html",
                        "/node_modules/**",
                        "/error")))
                .authorizeExchange()
                .anyExchange()
                .authenticated()
                .and()
                .cors()
                .and()
                .httpBasic().disable()
                .formLogin().disable()
                .addFilterBefore(webAuthnWebFilterFactory.get().withUser(ReactiveSecurityContextHolder.getContext()
                        .flatMap(sc -> {
                            UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) sc.getAuthentication();
                            if (token == null)
                                return Mono.empty();

                            Object principal = token.getPrincipal();
                            if (principal instanceof User) {
                                return Mono.just(webAuthnUserRepository.findByUsername(((User) principal).getUsername()).orElseThrow());
                            } else {
                              return Mono.error(new IllegalStateException("Only WebAuthn is allowed"));
                            }
                        }))
                                .withLoginSuccessHandler((user, credentials) -> {
                                    User dbUser = userRepository.findByUsername(user.getUsername()).orElseThrow();
                                    return new UsernamePasswordAuthenticationToken(dbUser, credentials, Collections.emptyList());
                                })
                                .withRegisterSuccessHandler(newUser -> {
                                    userRepository.save(User.of(newUser.getUsername()));
                                })
                        , SecurityWebFiltersOrder.AUTHENTICATION)
                .csrf()
                .disable()
        ;

        return http.build();
    }
}
