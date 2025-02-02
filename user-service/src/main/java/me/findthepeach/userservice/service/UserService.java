package me.findthepeach.userservice.service;

import me.findthepeach.userservice.model.dto.*;

import java.util.UUID;

public interface UserService {
    void registerUser(UserRegistrationDto registrationDto);

    UserProfileDto getUserInfo(UUID userSub);

    void setRole(UUID userSub, UserRoleUpdateDto roleUpdateDto);

    void updateUserInfo(UUID userSub, UserUpdateDto updateDto);

}
