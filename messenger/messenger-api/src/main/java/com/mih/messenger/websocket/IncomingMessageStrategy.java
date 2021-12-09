package com.mih.messenger.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mih.messenger.domain.User;
import com.mih.messenger.rest.model.MessageDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;

@Service
public class IncomingMessageStrategy {
    private static final Logger log = LoggerFactory.getLogger(IncomingMessageStrategy.class);
    private final UserWebSocketService userWebSocketService;
    private final ObjectMapper mapper;

    public IncomingMessageStrategy(UserWebSocketService userWebSocketService, ObjectMapper mapper) {
        this.userWebSocketService = userWebSocketService;
        this.mapper = mapper;
    }

    public Mono<Void> onMessage(User sender, String payloadAsText) {

        log.debug("onMessage - received from sender: " + sender + ", message: " + payloadAsText);
        return Mono.fromRunnable(() -> {
                    try {
                        MessageDto event = mapper.readValue(payloadAsText, MessageDto.class);
                        userWebSocketService.notify(sender, event);

                    } catch (IOException e) {
                        throw new IllegalArgumentException("Invalid payload: " + payloadAsText, e);
                    }
                })
                .doOnError(e -> log.error("onMessage - encountered error", e))
                .subscribeOn(Schedulers.boundedElastic())
                .then();
    }
}
