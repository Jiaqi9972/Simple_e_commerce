package me.findthepeach.userservice.service;

import me.findthepeach.userservice.model.dto.UserDto;

import java.util.UUID;

public interface UserService {
    void registerUser(UserDto userDto);

    UserDto getUserInfo(UUID userSub);

    void setRole(UUID userSub, UserDto userDto);

    void updateUserInfo(UUID userSub, UserDto userDto);

}
