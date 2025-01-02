package me.findthepeach.shopservice.repository;

import me.findthepeach.common.enums.ShopStatus;
import me.findthepeach.shopservice.model.entity.Shop;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ShopRepository extends JpaRepository<Shop, UUID> {

    Page<Shop> findByOwnerId(UUID ownerId, Pageable pageable);

    Page<Shop> findByShopNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndShopStatus(String keyword, String keyword1, ShopStatus shopStatus, Pageable pageable);

    Page<Shop> findByShopStatus(ShopStatus shopStatus, Pageable pageable);

    Page<Shop> findByOwnerIdAndShopStatusIn(UUID ownerId, List<ShopStatus> statuses, Pageable pageable);
}
