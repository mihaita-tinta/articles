package com.mih.messenger.rest.model;

import com.mih.messenger.domain.Conversation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(uses = UserMapper.class)
public interface ConversationMapper {
    ConversationMapper INSTANCE = Mappers.getMapper(ConversationMapper.class);

    @Mapping(source = "participants", target = "participants")
    ConversationDto toDto(Conversation conversation);

    Conversation fromRequest(ConversationRequest conversation);
}
