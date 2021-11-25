package com.mih.messenger;

import com.mih.messenger.domain.Conversation;
import com.mih.messenger.domain.ConversationRepository;
import com.mih.messenger.domain.MessageRepository;
import com.mih.messenger.domain.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class MessagingService {

    private final UserRepository userRepository;
    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;

    public MessagingService(UserRepository userRepository, ConversationRepository conversationRepository, MessageRepository messageRepository) {
        this.userRepository = userRepository;
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
    }

}
