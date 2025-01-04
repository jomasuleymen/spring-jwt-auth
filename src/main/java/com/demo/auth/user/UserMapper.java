package com.demo.auth.user;

import com.demo.auth.core.configurations.security.UserDetailsImpl;
import com.demo.auth.user.dto.UserDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDTO toDto(User user);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "username", target = "username")
    @Mapping(source = "role", target = "role")
    UserDTO toDto(UserDetailsImpl user);
}
