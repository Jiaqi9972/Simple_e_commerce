package me.findthepeach.inventoryservice.service;

import me.findthepeach.inventoryservice.model.dto.ModifyProductDto;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.UUID;

public interface ProductManagementService {

    void createProduct(ModifyProductDto modifyProductDto, UUID ownerId, Jwt jwt);

    void updateProduct(ModifyProductDto modifyProductDto, UUID ownerId);

    void deleteProduct(UUID productId, UUID ownerId);

    void deactivateProduct(UUID productId, UUID ownerId);

    void activateProduct(UUID productId, UUID ownerId);
}
