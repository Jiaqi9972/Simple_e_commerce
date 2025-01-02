package me.findthepeach.userservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.findthepeach.common.enums.Role;
import me.findthepeach.common.response.constant.ReturnCode;
import me.findthepeach.common.response.exception.UserException;
import me.findthepeach.userservice.model.dto.UserDto;
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
    public void registerUser(UserDto userDto) {
        // TODO
        // add cognito IAM auth
        log.info("Registering new user with sub: {}", userDto.getUserSub());
        if (userDto.getUserSub() == null || userDto.getUsername() == null || userDto.getEmail() == null) {
            throw new UserException(ReturnCode.INVALID_PARAMETER);
        }

        boolean existsBySub = userRepository.existsById(userDto.getUserSub());
        if (existsBySub) {
            throw new UserException(ReturnCode.USER_SUB_ALREADY_EXISTS);
        }

        boolean existsByEmail = userRepository.existsByEmail(userDto.getEmail());
        if (existsByEmail) {
            throw new UserException(ReturnCode.EMAIL_ALREADY_EXISTS);
        }

        User user = User.builder()
                .userSub(userDto.getUserSub())
                .username(userDto.getUsername())
                .email(userDto.getEmail())
                .role(Role.USER)
                .build();
        userRepository.save(user);
        log.debug("User registered successfully: {}", userDto.getUserSub());
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getUserInfo(UUID userSub) {
        User user = userRepository.findById(userSub)
                .orElseThrow(() -> new UserException(ReturnCode.USER_NOT_FOUND));
        return UserDto.builder()
                .userSub(user.getUserSub())
                .username(user.getUsername())
                .email(user.getEmail())
                .avatarUrl(user.getAvatarUrl())
                .role(user.getRole().name())
                .build();
    }

    @Override
    @Transactional
    public void setRole(UUID userSub, UserDto userDto) {
        log.info("Set Role for user: {}", userDto.getUserSub());
        User user = userRepository.findById(userSub)
                .orElseThrow(() -> new UserException(ReturnCode.USER_NOT_FOUND));
        if (userDto.getRole() == null || userDto.getRole().equals(Role.ADMIN.name())) {
            throw new UserException(ReturnCode.INVALID_ROLE_TYPE);
        }
        if (user.getRole() != Role.USER) {
            throw new UserException(ReturnCode.INVALID_ROLE_TYPE);
        }

        // TODO set cognito user group
        try {
            Role role = Role.valueOf(userDto.getRole().toUpperCase());
            user.setRole(role);
            userRepository.save(user);
            log.debug("Role set successfully: {}", userDto.getRole());
        } catch (IllegalArgumentException e) {
            throw new UserException(ReturnCode.INVALID_ROLE_TYPE);
        }
    }

    @Override
    @Transactional
    public void updateUserInfo(UUID userSub, UserDto userDto) {
        log.info("Update User Info: {}", userDto.getUserSub());
        User user = userRepository.findById(userSub)
                .orElseThrow(() -> new UserException(ReturnCode.USER_NOT_FOUND));
        if (userDto.getUsername() == null && userDto.getAvatarUrl() == null) {
            throw new UserException(ReturnCode.INVALID_PARAMETER);
        }
        if (userDto.getUsername() != null) {
            user.setUsername(userDto.getUsername());
        }
        if (userDto.getAvatarUrl() != null) {
            user.setAvatarUrl(userDto.getAvatarUrl());
        }
        userRepository.save(user);
        log.debug("User updated successfully: {}", userDto.getUserSub());
    }

}
