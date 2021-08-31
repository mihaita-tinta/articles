package com.mih.sse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    SseTemplate template;

    @GetMapping(path = "/users", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter sseUsers() {
        return template.newSseEmitter("users");
    }

}
