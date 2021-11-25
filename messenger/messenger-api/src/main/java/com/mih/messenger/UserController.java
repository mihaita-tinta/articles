package com.mih.messenger;

import com.mih.messenger.domain.UserRepository;
import com.mih.messenger.model.UserDto;
import com.mih.messenger.model.UserMapper;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class UserController {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserController(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @GetMapping
    public UserDto get(@AuthenticationPrincipal Principal principal) {
        return userRepository.findByUsername(principal.getName())
                .map(userMapper::userToDto)
                .get();
    }
}
