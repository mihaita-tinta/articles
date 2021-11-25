package com.mih.messenger.model;

import com.mih.messenger.domain.User;
import org.mapstruct.Mapper;

@Mapper
public interface UserMapper {

    UserDto userToDto(User user);
}
