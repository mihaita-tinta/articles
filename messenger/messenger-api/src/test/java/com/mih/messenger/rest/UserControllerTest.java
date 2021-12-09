package com.mih.messenger.rest;

import com.mih.messenger.WithUser;
import com.mih.messenger.domain.ConversationRepository;
import com.mih.messenger.domain.MessageRepository;
import com.mih.messenger.domain.User;
import com.mih.messenger.domain.UserRepository;
import com.mih.messenger.websocket.UserWebSocketService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document;

@WebFluxTest
@AutoConfigureRestDocs
class UserControllerTest {
    private static final Logger log = LoggerFactory.getLogger(UserControllerTest.class);

    @Autowired
    WebTestClient client;

    @MockBean
    UserRepository userRepository;
    @MockBean
    MessageRepository messageRepository;
    @MockBean
    ConversationRepository conversationRepository;
    @MockBean
    UserWebSocketService userWebSocketService;

    @Test
    @WithUser
    public void test() {

        client
                .get()
                .uri("/users/")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .consumeWith(s -> log.info(s.toString()))
                .jsonPath("$.id").isEqualTo("1")
                .jsonPath("$.username").isEqualTo("user")
                .jsonPath("$.dateCreated").doesNotExist()
                .consumeWith(document("users-get"));

    }

    @Test
    @WithUser
    public void testActive() {

        when(userWebSocketService.getActiveUsers()).thenReturn(
                LongStream.range(0, 100)
                                .mapToObj(i -> User.of(i, "user" + i))
                                        .collect(Collectors.toList()));
        client
                .get()
                .uri("/users/active/")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .consumeWith(s -> log.info(s.toString()))
                .jsonPath("$.length()").isEqualTo("100")
                .jsonPath("$[*].id").exists()
                .jsonPath("$[*].username").exists()
                .consumeWith(document("users-active-get"));

        verify(userWebSocketService).getActiveUsers();

    }
}
