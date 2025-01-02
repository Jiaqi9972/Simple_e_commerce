package me.findthepeach.shopservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.findthepeach.common.enums.Role;
import me.findthepeach.common.enums.ShopStatus;
import me.findthepeach.common.response.constant.ReturnCode;
import me.findthepeach.common.response.exception.ShopException;
import me.findthepeach.common.response.exception.UserException;
import me.findthepeach.shopservice.model.dto.UserDto;
import me.findthepeach.shopservice.model.entity.Shop;
import me.findthepeach.shopservice.repository.ShopRepository;
import me.findthepeach.shopservice.service.ShopStatusService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class ShopStatusServiceImpl implements ShopStatusService {

    private final WebClient userServiceWebClient;
    private final ShopRepository shopRepository;

    // get role from user-service
    private UserDto getUserRole(UUID userId) {
        return userServiceWebClient.get()
                .uri("/api/v1/user/public-info/{userId}", userId)
                .retrieve()
                .bodyToMono(UserDto.class)
                .block();
    }

    // check shop modify validation
    private Shop validateShopAndOwner(UUID shopId, UUID ownerId) {
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new ShopException(ReturnCode.SHOP_NOT_FOUND));

        if (!shop.getOwnerId().equals(ownerId)) {
            log.warn("Unauthorized shop access attempt - Shop: {}, RequestedBy: {}", shopId, ownerId);
            throw new UserException(ReturnCode.INVALID_ROLE);
        }
        return shop;
    }

    // check verified
    private void validateVerifiedStatus(Shop shop) {
        if (!shop.isVerified()) {
            throw new ShopException(ReturnCode.SHOP_NOT_VERIFIED);
        }
    }

    // check if shop is deleted
    private void validateShopNotDeleted(Shop shop) {
        if (shop.getShopStatus() == ShopStatus.DELETED) {
            log.warn("Attempt to modify a deleted shop - Shop: {}", shop.getShopId());
            throw new ShopException(ReturnCode.SHOP_ALREADY_DELETED);
        }
    }

    // check if user is admin
    private boolean isAdmin(UUID userId) {
        UserDto user = getUserRole(userId);
        return Role.ADMIN.name().equals(user.getRole().toUpperCase());
    }

    @Override
    @Transactional
    public void verifyShop(UUID shopId, UUID userId) {
        log.info("Verifying shop: {} by user: {}", shopId, userId);

        if (!isAdmin(userId)) {
            log.warn("Non-admin verification attempt - Shop: {}, RequestedBy: {}", shopId, userId);
            throw new UserException(ReturnCode.INVALID_ROLE);
        }

        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new ShopException(ReturnCode.SHOP_NOT_FOUND));

        validateShopNotDeleted(shop);

        shop.setVerified(true);
        shop.setShopStatus(ShopStatus.ACTIVE);
        shop.setVerifiedAt(LocalDateTime.now());
        shopRepository.save(shop);
        log.info("Shop verified successfully: {}", shopId);
    }

    @Override
    @Transactional
    public void markShopAsDeleted(UUID shopId, UUID ownerId) {
        log.info("Deleting shop: {} by owner: {}", shopId, ownerId);
        Shop shop = validateShopAndOwner(shopId, ownerId);
        validateVerifiedStatus(shop);

        shop.setShopStatus(ShopStatus.DELETED);
        shopRepository.save(shop);
        log.info("Shop deleted successfully: {}", shopId);
    }

    @Override
    @Transactional
    public void activateShop(UUID shopId, UUID ownerId) {
        log.info("Activating shop: {} by owner: {}", shopId, ownerId);
        Shop shop = validateShopAndOwner(shopId, ownerId);
        validateVerifiedStatus(shop);

        // If admin, allow activation even if shop is deleted
        if (isAdmin(ownerId)) {
            shop.setShopStatus(ShopStatus.ACTIVE);
            shopRepository.save(shop);
            log.info("Shop activated successfully by admin: {}", shopId);
        } else {
            validateShopNotDeleted(shop);

            shop.setShopStatus(ShopStatus.ACTIVE);
            shopRepository.save(shop);
            log.info("Shop activated successfully: {}", shopId);
        }
    }

    @Override
    @Transactional
    public void deactivateShop(UUID shopId, UUID ownerId) {
        log.info("Deactivating shop: {} by owner: {}", shopId, ownerId);
        Shop shop = validateShopAndOwner(shopId, ownerId);
        validateVerifiedStatus(shop);

        validateShopNotDeleted(shop);

        shop.setShopStatus(ShopStatus.INACTIVE);
        shopRepository.save(shop);
        log.info("Shop deactivated successfully: {}", shopId);
    }

}