package com.mih.messenger.rest.model;

import com.mih.messenger.domain.User;
import io.github.webauthn.domain.WebAuthnUser;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserDto toDto(User user);

    @Mapping(target ="dateCreated", expression = "java( new java.util.Date() )")
    User fromWebAuthn(WebAuthnUser user);
}
