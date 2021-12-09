package com.mih.messenger.rest;

import com.mih.messenger.domain.Conversation;
import com.mih.messenger.domain.ConversationRepository;
import com.mih.messenger.domain.MessageRepository;
import com.mih.messenger.domain.User;
import com.mih.messenger.rest.model.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/conversations")
@Transactional
public class ConversationController {

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;

    public ConversationController(ConversationRepository conversationRepository, MessageRepository messageRepository) {
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
    }

    @GetMapping("/")
    public List<ConversationDto> get(@AuthenticationPrincipal User user) {
        return conversationRepository.findAllByParticipantsContaining(user)
                .map(ConversationMapper.INSTANCE::toDto)
                .collect(Collectors.toList());
    }

    @PostMapping("/")
    public Conversation save(@AuthenticationPrincipal User user, @Validated @RequestBody ConversationRequest conversation) {
        Conversation c = ConversationMapper.INSTANCE.fromRequest(conversation);

        if (c.getParticipants() == null || c.getParticipants().isEmpty()) {
            throw new IllegalArgumentException("At least another user needs to be added in the conversation");
        }

        if (!c.getParticipants().contains(user)) {
            c.getParticipants().add(user);
        }

        if (c.getParticipants().size() == 1) {
            throw new IllegalArgumentException("At least another user needs to be added in the conversation");
        }

        c.setDateStarted(new Date());
        return conversationRepository.save(c);
    }

    @GetMapping("/{id}/messages")
    public List<MessageDto> get(@AuthenticationPrincipal User user, @PathVariable Long id) {
        return conversationRepository.findById(id)
                .map(conversation -> {
                    if (!conversation.getParticipants().contains(user)) {
                        throw new AccessDeniedException("User is not a participant to this conversation");
                    }
                    return messageRepository.findAllByConversationOrderByDateSentDesc(conversation)
                            .map(MessageMapper.INSTANCE::toDto)
                            .collect(Collectors.toList());
                })
                .orElseThrow();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handle(IllegalArgumentException e) {
        return Map.of("error", e.getMessage());
    }
}
