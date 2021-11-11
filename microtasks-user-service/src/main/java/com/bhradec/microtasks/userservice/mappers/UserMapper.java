package com.bhradec.microtasks.userservice.mappers;

import com.bhradec.microtasks.userservice.domain.User;
import com.bhradec.microtasks.userservice.dtos.UserCommandDto;
import com.bhradec.microtasks.userservice.dtos.UserDto;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserMapper(BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public User mapCommandToUser(UserCommandDto userCommandDto) {
        return User
                .builder()
                .firstName(userCommandDto.getFirstName())
                .lastName(userCommandDto.getLastName())
                .username(userCommandDto.getUsername())
                .email(userCommandDto.getEmail())
                .password(bCryptPasswordEncoder.encode(userCommandDto.getPassword()))
                .teamId(userCommandDto.getTeamId())
                .build();
    }

    public UserDto mapUserToDto(User user) {
        return UserDto
                .builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .username(user.getUsername())
                .email(user.getEmail())
                .teamId(user.getTeamId())
                .build();
    }
}
