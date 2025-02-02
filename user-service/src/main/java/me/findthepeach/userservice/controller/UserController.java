package me.findthepeach.userservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.findthepeach.userservice.model.dto.*;
import me.findthepeach.userservice.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "APIs for user CRUD operations")
public class UserController {
    private final UserService userService;

    private UUID getCurrentUserId(Jwt jwt) {
        return UUID.fromString(jwt.getClaimAsString("sub"));
    }

    @Operation(summary = "Register a new user")
    @PostMapping("/account")
    public void register(
            @RequestBody @Valid
            @Parameter(description = "User registration details", required = true)
            UserRegistrationDto registrationDto
    ) {
        userService.registerUser(registrationDto);
    }

    @Operation(summary = "Get current user information")
    @GetMapping("/user-info")
    public UserProfileDto userInfo(
            @Parameter(hidden = true)
            @AuthenticationPrincipal Jwt jwt
    ) {
        return userService.getUserInfo(getCurrentUserId(jwt));
    }

    @Operation(summary = "Set user role")
    @PatchMapping("/role")
    public void setRole(
            @RequestBody @Valid
            @Parameter(description = "User role information", required = true)
            UserRoleUpdateDto roleUpdateDto,
            @Parameter(hidden = true)
            @AuthenticationPrincipal Jwt jwt
    ) {
        userService.setRole(getCurrentUserId(jwt), roleUpdateDto);
    }

    @Operation(summary = "Update user information")
    @PatchMapping("/user-info")
    public void updateUserInfo(
            @RequestBody @Valid
            @Parameter(description = "User information to update", required = true)
            UserUpdateDto updateDto,
            @Parameter(hidden = true)
            @AuthenticationPrincipal Jwt jwt
    ) {
        userService.updateUserInfo(getCurrentUserId(jwt), updateDto);
    }
}