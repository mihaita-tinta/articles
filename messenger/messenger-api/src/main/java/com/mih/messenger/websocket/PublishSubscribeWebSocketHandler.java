package com.mih.messenger.websocket;

import com.mih.messenger.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;

import java.util.function.Consumer;

@Component
public class PublishSubscribeWebSocketHandler extends AuthorizedWebSocketHandler {
    private static final Logger log = LoggerFactory.getLogger(PublishSubscribeWebSocketHandler.class);

    private final UserWebSocketService userWebSocketService;
    private final IncomingMessageStrategy incomingStrategy;

    @Autowired
    public PublishSubscribeWebSocketHandler(UserWebSocketService userWebSocketService,
                                            IncomingMessageStrategy incomingStrategy) {
        this.userWebSocketService = userWebSocketService;
        this.incomingStrategy = incomingStrategy;
    }

    @Override
    public Mono<Void> doHandle(User user, WebSocketSession session) {

        String sessionId = session.getId();

        Flux<WebSocketMessage> futureMessages = Flux
                .create((Consumer<FluxSink<WebSocketMessage>>) sink -> {
                    userWebSocketService.onConnect(sink, session, user);
                })
                .onErrorResume(Exception.class, Flux::error)
                .doOnComplete(() -> log.info("handle - doOnComplete for " + sessionId))
                .doFinally(signalType -> {
                    log.info("handle - finally for " + sessionId + ", signalType: " + signalType);
                    userWebSocketService.onClose(session, user);
                });

        Mono<Void> input = session.receive()
                .flatMap(message -> incomingStrategy.onMessage(user, message.getPayloadAsText()))
                .doOnError(e -> {
                    log.warn("handle - error receiving message from user {}", user, e);
                })
                .then();

        Mono<Void> output = session.send(futureMessages);

        return Mono.zip(input, output).then();
    }

}
