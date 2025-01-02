package me.findthepeach.shopservice.service;

import me.findthepeach.shopservice.model.dto.ShopDto;

import java.util.UUID;

public interface ShopManagementService {
    void createShop(ShopDto shopDto, UUID ownerId);

    void updateShop(UUID shopId, ShopDto shopDto, UUID ownerID);
}
