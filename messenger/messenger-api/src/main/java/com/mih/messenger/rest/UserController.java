package com.mih.messenger.rest;

import com.mih.messenger.domain.User;
import com.mih.messenger.rest.model.UserDto;
import com.mih.messenger.rest.model.UserMapper;
import com.mih.messenger.websocket.UserWebSocketService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.stream.Stream;

@RestController
public class UserController {

    private final UserWebSocketService service;

    public UserController(UserWebSocketService service) {
        this.service = service;
    }

    @GetMapping("/users/")
    public Mono<UserDto> get(@AuthenticationPrincipal Mono<User> user) {
        return user
                .map(UserMapper.INSTANCE::toDto);
    }

    @GetMapping("/users/active/")
    public Stream<UserDto> getActiveUsers() {
        return service.getActiveUsers()
                .stream()
                .map(UserMapper.INSTANCE::toDto);
    }
}
