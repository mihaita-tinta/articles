package com.mih.messenger.domain;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class MessageRepositoryTest {

    private static final Logger log = LoggerFactory.getLogger(MessageRepositoryTest.class);
    @Autowired
    MessageRepository messageRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ConversationRepository conversationRepository;

    @Test
    public void test() {
        User participant1 = userRepository.findByUsername("participant1").get();
        Conversation conversation = conversationRepository.findAllByParticipantsContaining(participant1)
                .findFirst().get();

        Message message = Message.from(participant1, conversation, "Hello World");

        messageRepository.saveAndFlush(message);

        assertEquals(1, messageRepository.findAllByConversationOrderByDateSentDesc(conversation)
                .count());

    }

}
