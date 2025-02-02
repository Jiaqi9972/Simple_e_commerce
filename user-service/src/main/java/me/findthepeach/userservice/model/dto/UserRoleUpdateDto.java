package me.findthepeach.userservice.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
@Schema(description = "User Role Update Request")
public class UserRoleUpdateDto {

    @Schema(description = "User sub", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID userSub;

    @NotNull
    @Schema(description = "User role", example = "USER")
    private String role;
}
