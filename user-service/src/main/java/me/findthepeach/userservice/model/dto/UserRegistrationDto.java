package me.findthepeach.userservice.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
@Schema(description = "User Registration Request")
public class UserRegistrationDto {
    @NotNull
    @Schema(description = "User ID from Cognito", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID userSub;

    @NotBlank
    @Schema(description = "Username", example = "john_doe")
    private String username;

    @NotBlank
    @Email
    @Schema(description = "Email address", example = "john.doe@example.com")
    private String email;
}