package me.findthepeach.inventoryservice.repository;

import me.findthepeach.common.enums.ProductStatus;
import me.findthepeach.inventoryservice.model.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {
    List<Product> findByOwnerId(UUID ownerId);
    List<Product> findByProductIdIn(List<UUID> productIds);

    Optional<Product> findByProductIdAndStatus(UUID productId, ProductStatus productStatus);

    Page<Product> findByShopIdAndStatusAndProductNameContainingIgnoreCase(UUID shopUUID, ProductStatus productStatus, String keyword, Pageable pageable);

    Page<Product> findByShopIdAndStatus(UUID shopUUID, ProductStatus productStatus, Pageable pageable);

    Page<Product> findByStatusAndProductNameContainingIgnoreCase(ProductStatus productStatus, String keyword, Pageable pageable);

    Page<Product> findByStatus(ProductStatus productStatus, Pageable pageable);

    Page<Product> findAll(Specification<Product> spec, Pageable pageable);
}
