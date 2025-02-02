package me.findthepeach.userservice.model.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.findthepeach.common.enums.Role;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
@Schema(description = "User entity")
public class User {

    @Id
    @Column(name = "user_sub", nullable = false, unique = true, updatable = false)
    @Schema(description = "User ID", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID userSub;

    @Column(name = "username", nullable = false, length = 50)
    @Schema(description = "Username", example = "john_doe", requiredMode = Schema.RequiredMode.REQUIRED)
    private String username;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    @Schema(description = "Email address", example = "john.doe@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;

    @Column(name = "avatar_url", length = 255)
    @Schema(description = "Avatar URL", example = "https://example.com/avatar.jpg")
    private String avatarUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    @Schema(description = "User role", example = "USER", requiredMode = Schema.RequiredMode.REQUIRED)
    private Role role;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    @Schema(description = "Creation timestamp")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    @Schema(description = "Last update timestamp")
    private LocalDateTime updatedAt;
}