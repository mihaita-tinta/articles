package com.mih.messenger.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mih.messenger.WithUser;
import com.mih.messenger.domain.*;
import com.mih.messenger.rest.model.MessageDto;
import com.mih.messenger.rest.model.MessageMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import org.springframework.web.reactive.socket.client.WebSocketClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static java.time.temporal.ChronoUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(properties = {"debug=true", "logging.level.com.mih.messenger=DEBUG",
        "spring.main.web-application-type=reactive"},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserWebSocketServiceTest {
    private static final Logger log = LoggerFactory.getLogger(UserWebSocketServiceTest.class);
    @LocalServerPort
    int port;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    IncomingMessageStrategy incommingWebSocketStrategy;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ConversationRepository conversationRepository;
    @Autowired
    MessageRepository messageRepository;

    @Test
    @WithUser(username = "integration-user-1", id = 5L)
    public void test() throws InterruptedException {
        int expectedReceivedEvents = 2;
        CountDownLatch latch = new CountDownLatch(expectedReceivedEvents);

        User currentUser = userRepository.save(User.of("integration-user-1"));
        User user2 = userRepository.save(User.of("integration-user-2"));
        Conversation conversation = conversationRepository.save(Conversation.between(user2, currentUser));

        WebSocketClient client = new ReactorNettyWebSocketClient();

        client.execute(
                        URI.create("ws://localhost:" + port + "/websocket"),
                        new HttpHeaders(),
                        session -> {
                            Flux<WebSocketMessage> sentFlux = Flux.interval(Duration.of(2, SECONDS))
                                    .map(i -> MessageMapper.INSTANCE.toDto(Message.from(currentUser, conversation, currentUser.getUsername() + " says hello  " + i, 1L)))
                                    .map(this::eventToString)
                                    .map(json -> {
                                        log.info("test - generating payload: " + json);
                                        return session.textMessage(json);
                                    })
                                    .doFinally(signalType -> {
                                        log.info("test - finally for " + session.getId());
                                    });
                            Mono<Void> output = session.send(sentFlux);

                            Mono<Void> input = session.receive()
                                    .doOnNext(message -> {
                                        log.info("test - received by user: " + currentUser + ", message: " + message.getPayloadAsText());
                                        latch.countDown();
                                    })
                                    .then();

                            return Mono.zip(input, output).then();
                        })
                .subscribe();

        latch.await(5, TimeUnit.SECONDS);
        assertEquals(0, latch.getCount());

        List<Message> savedMessages = messageRepository.findByConversationOrderByDateSentDesc(conversation);
        assertEquals(2, savedMessages.size());
        savedMessages.forEach(m -> assertEquals(currentUser, m.getFrom()));
    }

    @Test
    @WithUser(username = "integration-user-1", id = 5L)
    public void testMessageDeliveredOrSeen() throws InterruptedException {
        int expectedReceivedEvents = 4;
        CountDownLatch latch = new CountDownLatch(expectedReceivedEvents);

        User currentUser = userRepository.save(User.of("integration-user-1"));
        User user2 = userRepository.save(User.of("integration-user-2"));
        Conversation conversation = conversationRepository.save(Conversation.between(user2, currentUser));

        Flux.interval(Duration.of(1, SECONDS))
                .map(i -> MessageMapper.INSTANCE.toDto(Message.from(user2, conversation, user2.getUsername() + " says hello  " + i, 1L)))
                .flatMap(event -> {
                    log.info("test - sending payload: " + event);
                    return incommingWebSocketStrategy.onMessage(user2, eventToString(event));
                })
                .doOnError(e -> {
                    e.printStackTrace();
                })
                .subscribe();

        WebSocketClient client = new ReactorNettyWebSocketClient();

        client.execute(
                        URI.create("ws://localhost:" + port + "/websocket"),
                        new HttpHeaders(),
                        session -> {
                            Mono<Void> input = session.receive()
                                    .flatMap(message -> {
                                        log.info("test - received by user: " + currentUser + ", message: " + message.getPayloadAsText());
                                        MessageDto m = eventFromString(message.getPayloadAsText());
                                        if (m.getDateDelivered() == null) {
                                            m.setDateDelivered(new Date());
                                            m.setDateViewed(new Date());
                                            return incommingWebSocketStrategy.onMessage(user2, eventToString(m))
                                                    .doOnNext(v -> latch.countDown());
                                        }

                                        latch.countDown();
                                        return Flux.empty();
                                    })
                                    .then();

                            return input.then();
                        })
                .subscribe();

        latch.await(4100, TimeUnit.MILLISECONDS);
        assertEquals(0, latch.getCount());

        List<Message> fromUser2 = messageRepository.findByConversationOrderByDateSentDesc(conversation)
                .stream()
                .filter(m -> m.getFrom().equals(user2))
                .collect(Collectors.toList());
        assertEquals(4, fromUser2.size());
        fromUser2.forEach(m -> {
            assertNotNull(m.getDateDelivered());
            assertNotNull(m.getDateViewed());
        });
    }

    private String eventToString(MessageDto event) {
        try {
            return mapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e);
        }
    }
    private MessageDto eventFromString(String string) {
        try {
            return mapper.readValue(string, MessageDto.class);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e);
        }
    }

}
