package com.mih.messenger.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    Stream<Conversation> findAllByParticipantsContaining(User user);
    CompletableFuture<List<Conversation>> findByParticipantsContaining(User user);
}
