package com.mih.messenger.websocket;

import com.mih.messenger.domain.User;
import org.springframework.security.core.Authentication;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;

public abstract class AuthorizedWebSocketHandler implements WebSocketHandler {

    @Override
    public final Mono<Void> handle(WebSocketSession session) {
        return session.getHandshakeInfo().getPrincipal()
                .map(principal -> (User) ((Authentication) principal).getPrincipal())
                .flatMap(user -> doHandle(user, session));
    }

    abstract protected Mono<Void> doHandle(User user, WebSocketSession session);
}
