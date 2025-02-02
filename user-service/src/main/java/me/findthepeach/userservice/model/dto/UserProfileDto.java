package me.findthepeach.userservice.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Schema(description = "User Profile Response")
@Builder
public class UserProfileDto {

    @Schema(description = "User sub", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID userSub;

    @Schema(description = "Username", example = "john_doe")
    private String username;

    @Schema(description = "Email address", example = "john.doe@example.com")
    private String email;

    @Schema(description = "Avatar URL", example = "https://example.com/avatar.jpg")
    private String avatarUrl;

    @Schema(description = "User role", example = "USER")
    private String role;
}
