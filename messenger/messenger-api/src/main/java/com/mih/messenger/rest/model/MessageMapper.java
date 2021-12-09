package com.mih.messenger.rest.model;

import com.mih.messenger.domain.Message;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.Map;

@Mapper
public interface MessageMapper {
    MessageMapper INSTANCE = Mappers.getMapper(MessageMapper.class);

    @Mapping(source = "from.username", target = "from")
    @Mapping(source = "conversation.id", target = "conversationId")
    MessageDto toDto(Message message);

    @Mapping(target = "from", ignore = true)
    Message fromDto(MessageDto message);
}
