package me.findthepeach.inventoryservice.repository;

import me.findthepeach.inventoryservice.model.entity.InventoryRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface InventoryRecordRepository extends JpaRepository<InventoryRecord, UUID> {

    Page<InventoryRecord> findByProductIdIn(List<UUID> productIds, Pageable pageable);

    @Query("SELECT ir FROM InventoryRecord ir JOIN Product p ON ir.productId = p.productId " +
            "WHERE ir.productId IN :productIds AND p.productName LIKE %:keyword%")
    Page<InventoryRecord> findByProductIdInAndProductNameContaining(
            List<UUID> productIds, String keyword, Pageable pageable);
}
