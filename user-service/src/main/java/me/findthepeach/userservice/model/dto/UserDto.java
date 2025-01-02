package me.findthepeach.userservice.model.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class UserDto {
    private UUID userSub;
    private String username;
    private String email;
    private String avatarUrl;
    private String role;
}
