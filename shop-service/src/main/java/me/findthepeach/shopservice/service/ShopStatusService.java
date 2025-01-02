package me.findthepeach.shopservice.service;

import java.util.UUID;

public interface ShopStatusService {

    void markShopAsDeleted(UUID shopId, UUID ownerId);

    void activateShop(UUID shopId, UUID ownerId);

    void deactivateShop(UUID shopId, UUID ownerId);

    void verifyShop(UUID shopId, UUID userId);

}
