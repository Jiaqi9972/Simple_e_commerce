package me.findthepeach.orderservice.repository;

import me.findthepeach.orderservice.model.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {

    @Query("SELECT o FROM Order o WHERE o.merchantId = :merchantId " +
            "AND (:keyword IS NULL OR " +
            "LOWER(CAST(o.orderId AS string)) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(COALESCE(o.trackingNumber, '')) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Order> findByMerchantIdAndKeyword(
            @Param("merchantId") UUID merchantId,
            @Param("keyword") String keyword,
            Pageable pageable
    );

    @Query("SELECT o FROM Order o WHERE o.merchantId = :merchantId " +
            "AND o.shopId IN :shopIds " +
            "AND (:keyword IS NULL OR " +
            "LOWER(CAST(o.orderId AS string)) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(COALESCE(o.trackingNumber, '')) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Order> findByMerchantIdAndShopIdsAndKeyword(
            @Param("merchantId") UUID merchantId,
            @Param("shopIds") List<UUID> shopIds,
            @Param("keyword") String keyword,
            Pageable pageable
    );

    @Query("SELECT o FROM Order o WHERE o.userId = :userId " +
            "AND (:keyword IS NULL OR " +
            "LOWER(CAST(o.orderId AS string)) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(COALESCE(o.trackingNumber, '')) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Order> findByUserIdAndKeyword(
            @Param("userId") UUID userId,
            @Param("keyword") String keyword,
            Pageable pageable
    );
}
