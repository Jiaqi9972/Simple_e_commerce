package me.findthepeach.userservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.findthepeach.common.enums.Role;
import me.findthepeach.common.response.constant.ReturnCode;
import me.findthepeach.common.response.exception.UserException;
import me.findthepeach.userservice.model.dto.*;
import me.findthepeach.userservice.model.entity.User;
import me.findthepeach.userservice.repository.UserRepository;
import me.findthepeach.userservice.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public void registerUser(UserRegistrationDto registrationDto) {
        // TODO
        // add cognito IAM auth
        log.info("Registering new user with sub: {}", registrationDto.getUserSub());
        if (registrationDto.getUserSub() == null || registrationDto.getUsername() == null || registrationDto.getEmail() == null) {
            throw new UserException(ReturnCode.INVALID_PARAMETER);
        }

        boolean existsBySub = userRepository.existsById(registrationDto.getUserSub());
        if (existsBySub) {
            throw new UserException(ReturnCode.USER_SUB_ALREADY_EXISTS);
        }

        boolean existsByEmail = userRepository.existsByEmail(registrationDto.getEmail());
        if (existsByEmail) {
            throw new UserException(ReturnCode.EMAIL_ALREADY_EXISTS);
        }

        User user = User.builder()
                .userSub(registrationDto.getUserSub())
                .username(registrationDto.getUsername())
                .email(registrationDto.getEmail())
                .role(Role.USER)
                .build();
        userRepository.save(user);
        log.debug("User registered successfully: {}", registrationDto.getUserSub());
    }

    @Override
    @Transactional(readOnly = true)
    public UserProfileDto getUserInfo(UUID userSub) {
        User user = userRepository.findById(userSub)
                .orElseThrow(() -> new UserException(ReturnCode.USER_NOT_FOUND));
        return UserProfileDto.builder()
                .userSub(user.getUserSub())
                .username(user.getUsername())
                .email(user.getEmail())
                .avatarUrl(user.getAvatarUrl())
                .role(user.getRole().name())
                .build();
    }

    @Override
    @Transactional
    public void setRole(UUID userSub, UserRoleUpdateDto roleUpdateDto) {
        log.info("Set Role for user: {}", roleUpdateDto.getUserSub());
        User user = userRepository.findById(userSub)
                .orElseThrow(() -> new UserException(ReturnCode.USER_NOT_FOUND));
        if (roleUpdateDto.getRole() == null || roleUpdateDto.getRole().equals(Role.ADMIN.name())) {
            throw new UserException(ReturnCode.INVALID_ROLE_TYPE);
        }
        if (user.getRole() != Role.USER) {
            throw new UserException(ReturnCode.INVALID_ROLE_TYPE);
        }

        // TODO set cognito user group
        try {
            Role role = Role.valueOf(roleUpdateDto.getRole().toUpperCase());
            user.setRole(role);
            userRepository.save(user);
            log.debug("Role set successfully: {}", roleUpdateDto.getRole());
        } catch (IllegalArgumentException e) {
            throw new UserException(ReturnCode.INVALID_ROLE_TYPE);
        }
    }

    @Override
    @Transactional
    public void updateUserInfo(UUID userSub, UserUpdateDto updateDto) {
        log.info("Update User Info: {}", updateDto.getUserSub());
        User user = userRepository.findById(userSub)
                .orElseThrow(() -> new UserException(ReturnCode.USER_NOT_FOUND));
        if (updateDto.getUsername() == null && updateDto.getAvatarUrl() == null) {
            throw new UserException(ReturnCode.INVALID_PARAMETER);
        }
        if (updateDto.getUsername() != null) {
            user.setUsername(updateDto.getUsername());
        }
        if (updateDto.getAvatarUrl() != null) {
            user.setAvatarUrl(updateDto.getAvatarUrl());
        }
        userRepository.save(user);
        log.debug("User updated successfully: {}", updateDto.getUserSub());
    }

}
