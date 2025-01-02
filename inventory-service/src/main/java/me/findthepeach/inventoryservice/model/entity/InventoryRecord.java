package me.findthepeach.inventoryservice.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "inventory_records")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class InventoryRecord {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "inventory_record_id", updatable = false, nullable = false, unique = true)
    private UUID inventoryRecordId;

    @Column(name = "product_id", nullable = false)
    private UUID productId;

    @Column(name = "quantity_changed", nullable = false)
    private int quantityChanged;

    @Column(name = "operator_id", nullable = false)
    private UUID operatorId;

    @Column(length = 300)
    private String description;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
