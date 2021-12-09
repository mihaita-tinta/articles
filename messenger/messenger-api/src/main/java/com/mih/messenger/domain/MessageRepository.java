package com.mih.messenger.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.stream.Stream;

public interface MessageRepository extends JpaRepository<Message, Long> {

    Stream<Message> findAllByConversationOrderByDateSentDesc(Conversation conversation);

    List<Message> findByConversationOrderByDateSentDesc(Conversation conversation);
}
