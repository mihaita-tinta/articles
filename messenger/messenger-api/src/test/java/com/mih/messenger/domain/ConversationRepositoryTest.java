package com.mih.messenger.domain;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Arrays;
import java.util.Date;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ConversationRepositoryTest {

    @Autowired
    ConversationRepository conversationRepository;
    @Autowired
    UserRepository userRepository;

    @Test
    public void test() {
        User participant1 = userRepository.findByUsername("participant1").get();
        User participant2 = userRepository.findByUsername("participant2").get();

        Conversation conversation = Conversation.between(participant1, participant2);
        conversationRepository.saveAndFlush(conversation);
    }

    @Test
    public void testFind() {
        User participant1 = userRepository.findByUsername("participant1").get();
        Stream<Conversation> conversations = conversationRepository.findAllByParticipantsContaining(participant1);
        assertEquals(1, conversations.count());
    }
}
