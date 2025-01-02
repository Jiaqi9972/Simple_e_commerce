package me.findthepeach.shopservice.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.findthepeach.common.enums.ShopStatus;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "shops")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Shop {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "shop_id", updatable = false, nullable = false, unique = true)
    private UUID shopId;

    @Column(name = "shop_name", nullable = false, unique = true, length = 100)
    private String shopName;

    @Column(name = "owner_id", updatable = false, nullable = false)
    private UUID ownerId;

    @Column(length = 500)
    private String description;

    @Column(name = "logo_url", nullable = false)
    private String logoUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "shop_status", nullable = false, length = 20)
    private ShopStatus shopStatus;

    @Column(name = "contact_email", nullable = false, length = 100)
    private String contactEmail;

    @Column(name = "is_verified", nullable = false)
    private boolean isVerified;

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
