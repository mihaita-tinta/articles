package com.mih.messenger.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mih.messenger.domain.*;
import com.mih.messenger.rest.model.MessageDto;
import com.mih.messenger.rest.model.MessageMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.FluxSink;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserWebSocketService {
    private static final Logger log = LoggerFactory.getLogger(UserWebSocketService.class);

    private final Map<User, WebSocketHolder> connections = new ConcurrentHashMap<>();
    private final ObjectMapper mapper;

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;

    public UserWebSocketService(ObjectMapper mapper, ConversationRepository conversationRepository, MessageRepository messageRepository) {
        this.mapper = mapper;
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
    }

    public void onConnect(FluxSink<WebSocketMessage> sink, WebSocketSession session, User user) {
        connections.put(user, WebSocketHolder.of(sink, session, user));
    }

    public void onClose(WebSocketSession session, User user) {
        connections.remove(user);
    }

    public List<User> getActiveUsers() {
        return connections.entrySet().stream().map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    public void notify(User sender, MessageDto message) {
        log.debug("notify - connections: {}, message: {}", connections.size(), message);
        Message processedMessage = processIncomingMessage(sender, message);
        try {
            String stringPayload = mapper.writeValueAsString(MessageMapper.INSTANCE.toDto(processedMessage));
            processedMessage.getConversation().getParticipants()
                    .forEach(participant -> {
                        WebSocketHolder holder = connections.get(participant);
                        boolean isConnectionAvailable = holder != null && holder.session.isOpen();
                        if (isConnectionAvailable) {
                            sendInternal(holder, stringPayload);
                        }
                    });
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    private Message processIncomingMessage(User sender, MessageDto newMessage) {
        Message m = MessageMapper.INSTANCE.fromDto(newMessage);
        Conversation conversation = conversationRepository.findById(newMessage.getConversationId())
                .orElseThrow();
        if (!conversation.getParticipants().contains(sender)) {
            throw new AccessDeniedException("User should be a participant");
        }

        boolean isANewMessage = m.getId() == null;
        if (isANewMessage) {
            return messageRepository.save(Message.from(sender, conversation, m.getContent()));
        } else {
            return messageRepository.findById(m.getId())
                    .map(existingMessage -> {
                        if (!existingMessage.getConversation().getId().equals(conversation.getId())) {
                            throw new AccessDeniedException("This message belongs to a different conversation");
                        }
                        log.debug("processIncomingMessage - update existing message: " + m);
                        existingMessage.setDateDelivered(m.getDateDelivered());
                        existingMessage.setDateViewed(m.getDateViewed());
                        return messageRepository.save(existingMessage);
                    })
                    .orElseThrow();
        }
    }

    private void sendInternal(WebSocketHolder holder, String stringPayload) {
        log.debug("sendInternal - message: {}, for user: {}", stringPayload, holder.user);
        FluxSink<WebSocketMessage> sink = holder.sink;
        WebSocketSession wsSession = holder.session;
        WebSocketMessage wsMsg = wsSession.textMessage(stringPayload);
        sink.next(wsMsg);
    }

    private static class WebSocketHolder {
        /**
         * Used to send messages
         */
        FluxSink<WebSocketMessage> sink;
        WebSocketSession session;
        /**
         * User receiving the events
         */
        User user;

        static WebSocketHolder of(FluxSink<WebSocketMessage> sink, WebSocketSession session, User user) {
            WebSocketHolder holder = new WebSocketHolder();
            holder.sink = sink;
            holder.session = session;
            holder.user = user;
            return holder;
        }
    }
}
