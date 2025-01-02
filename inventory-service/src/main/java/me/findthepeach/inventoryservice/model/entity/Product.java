package me.findthepeach.inventoryservice.model.entity;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import me.findthepeach.common.enums.ProductStatus;
import org.hibernate.annotations.Type;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "products")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Product {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "product_id", updatable = false, nullable = false, unique = true)
    private UUID productId;

    @Column(name = "product_name", nullable = false, unique = true, length = 100)
    private String productName;

    @Column(name = "shop_id", updatable = false, nullable = false)
    private UUID shopId;

    @Column(name = "owner_id",updatable = false, nullable = false)
    private UUID ownerId;

    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @Column(length = 500)
    private String description;

    @Type(JsonType.class)
    @Column(name = "product_image_urls", columnDefinition = "JSONB")
    private List<String> productImageUrls;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ProductStatus status = ProductStatus.ACTIVE;
}
