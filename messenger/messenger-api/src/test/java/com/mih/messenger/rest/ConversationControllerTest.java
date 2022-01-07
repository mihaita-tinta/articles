package com.mih.messenger.rest;

import com.mih.messenger.WithUser;
import com.mih.messenger.domain.*;
import com.mih.messenger.rest.model.ConversationDto;
import com.mih.messenger.rest.model.ConversationRequest;
import com.mih.messenger.rest.model.UserMapper;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

@WebFluxTest
@AutoConfigureWebTestClient
@AutoConfigureRestDocs
class ConversationControllerTest {

    private static final Logger log = LoggerFactory.getLogger(UserControllerTest.class);

    @Autowired
    WebTestClient client;

    @MockBean
    UserRepository userRepository;
    @MockBean
    MessageRepository messageRepository;
    @MockBean
    ConversationRepository conversationRepository;

    @Test
    @WithUser("participant1")
    public void testGet() {

        when(conversationRepository.findAllByParticipantsContaining(any()))
                .thenReturn(Stream.of(
                        Conversation.between(User.of(1L, "participant1"), User.of(2L, "participant2"))
                                .with(1L),
                        Conversation.between(User.of(1L, "participant1"), User.of(3L, "participant3"), User.of(4L, "participant4"))
                                .with(2L)
                ));

        client
                .get()
                .uri("/conversations/")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .consumeWith(s -> System.out.println(new String(s.getResponseBody())))
                .jsonPath("$.length()").isEqualTo("2")
                .jsonPath("$[0].participants.length()").isEqualTo("2")
                .jsonPath("$[*].participants[*].dateCreated").doesNotExist()
                .jsonPath("$[1].participants.length()").isEqualTo("3")
                .consumeWith(document("conversations-get"));

    }

    @Test
    @WithUser
    public void testNewConversation() {

        Conversation conversation = Conversation.between(User.of(1L, "user"),
                        User.of(2L, "participant"))
                .with(1L);

        when(conversationRepository.save(any())).thenReturn(conversation);

        ConversationRequest req = new ConversationRequest();
        req.setParticipants(Stream.of(User.of(1L, "user"),
                        User.of(2L, "participant"))
                .map(UserMapper.INSTANCE::toDto)
                .collect(Collectors.toList()));
        client
                .mutateWith(csrf())
                .post()
                .uri("/conversations/")
                .body(Mono.just(req), ConversationDto.class)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .consumeWith(s -> System.out.println(new String(s.getResponseBody())))
                .jsonPath("$.participants.length()").isEqualTo("2")
                .jsonPath("$.participants[?(@.username == 'user')]").exists()
                .consumeWith(document("conversations-post"));

    }

    @Test
    @WithUser
    public void testNewConversationWithoutOtherParticipants() {

        ConversationRequest req = new ConversationRequest();
        req.setParticipants(Stream.of(User.of(1L, "user"))
                .map(UserMapper.INSTANCE::toDto)
                .collect(Collectors.toList()));
        client
                .mutateWith(csrf())
                .post()
                .uri("/conversations/")
                .body(Mono.just(req), ConversationDto.class)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody()
                .consumeWith(s -> System.out.println(new String(s.getResponseBody())))
                .jsonPath("$.error").isEqualTo("At least another user needs to be added in the conversation")
                .consumeWith(document("conversations-post-validation"));

        verify(conversationRepository, never()).save(any());
    }

    @Test
    @WithUser
    public void testNewConversationWithoutAnyParticipants() {

        ConversationRequest req = new ConversationRequest();
        req.setParticipants(Collections.emptyList());
        client
                .mutateWith(csrf())
                .post()
                .uri("/conversations/")
                .body(Mono.just(req), ConversationDto.class)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody()
                .consumeWith(s -> System.out.println(new String(s.getResponseBody())))
                .jsonPath("$.error").isEqualTo("At least another user needs to be added in the conversation")
                .consumeWith(document("conversations-post-validation-empty-list"));

        verify(conversationRepository, never()).save(any());
    }

    @Test
    @WithAnonymousUser
    public void testNewConversationByAnonymousUser() {

        ConversationRequest req = new ConversationRequest();
        req.setParticipants(Stream.of(User.of(1L, "user"),
                        User.of(2L, "participant"))
                .map(UserMapper.INSTANCE::toDto)
                .collect(Collectors.toList()));
        client
                .mutateWith(csrf())
                .post()
                .uri("/conversations/")
                .body(Mono.just(req), ConversationDto.class)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .exchange()
                .expectStatus()
                .isUnauthorized()
                .expectBody()
                .consumeWith(document("conversations-post-anonymous-user"));

        verify(conversationRepository, never()).save(any());
    }


    @Test
    @WithUser("participant1")
    public void testGetMessages() {

        Conversation conversation = Conversation.between(User.of(1L, "participant1"), User.of(3L, "participant2"))
                .with(1L);

        when(conversationRepository.findById(1L)).thenReturn(Optional.of(conversation));

        User participant1 = User.of(1L, "participant1");
        User participant2 = User.of(2L, "participant2");
        when(messageRepository.findAllByConversationOrderByDateSentDesc(any()))
                .thenReturn(Stream.of(
                        Message.from(participant1, conversation, "Hello", 1L).with(1L),
                        Message.from(participant2, conversation, "Hey, how are you?", 1L).with(2L)));

        when(conversationRepository.findAllByParticipantsContaining(any()))
                .thenReturn(Stream.of(
                        Conversation.between(User.of(1L, "participant1"), User.of(2L, "participant2"))
                                .with(1L),
                        Conversation.between(User.of(1L, "participant1"), User.of(3L, "participant3"), User.of(4L, "participant4"))
                                .with(2L)
                ));

        client
                .get()
                .uri("/conversations/1/messages")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .consumeWith(s -> System.out.println(new String(s.getResponseBody())))
                .jsonPath("$.length()").isEqualTo("2")
                .jsonPath("$[0].content").isEqualTo("Hello")
                .jsonPath("$[0].from").isEqualTo("participant1")
                .jsonPath("$[1].content").isEqualTo("Hey, how are you?")
                .jsonPath("$[1].from").isEqualTo("participant2")
                .jsonPath("$.length()").isEqualTo("2")
                .consumeWith(document("messages-get"));

    }
}
