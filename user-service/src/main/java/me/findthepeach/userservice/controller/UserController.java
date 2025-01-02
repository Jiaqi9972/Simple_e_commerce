package me.findthepeach.userservice.controller;

import lombok.RequiredArgsConstructor;
import me.findthepeach.userservice.model.dto.UserDto;
import me.findthepeach.userservice.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    private UUID getCurrentUserId(Jwt jwt) {
        return UUID.fromString(jwt.getClaimAsString("sub"));
    }

    @PostMapping("/register")
    public void register(@RequestBody UserDto userDto) {
        userService.registerUser(userDto);
    }

    @GetMapping("/user-info")
    public UserDto userInfo(@AuthenticationPrincipal Jwt jwt) {
        return userService.getUserInfo(getCurrentUserId(jwt));
    }

    @PatchMapping("/set-role")
    public void setRole(@RequestBody UserDto userDto, @AuthenticationPrincipal Jwt jwt) {
        userService.setRole(getCurrentUserId(jwt), userDto);
    }

    @PatchMapping("/update-user-info")
    public void updateUserInfo(@RequestBody UserDto userDto, @AuthenticationPrincipal Jwt jwt) {
        userService.updateUserInfo(getCurrentUserId(jwt), userDto);
    }

}
