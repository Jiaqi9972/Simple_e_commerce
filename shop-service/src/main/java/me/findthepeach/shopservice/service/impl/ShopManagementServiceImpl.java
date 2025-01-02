package me.findthepeach.shopservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.findthepeach.common.enums.ShopStatus;
import me.findthepeach.common.response.constant.ReturnCode;
import me.findthepeach.common.response.exception.ShopException;
import me.findthepeach.common.response.exception.UserException;
import me.findthepeach.shopservice.model.dto.ShopDto;
import me.findthepeach.shopservice.model.entity.Shop;
import me.findthepeach.shopservice.repository.ShopRepository;
import me.findthepeach.shopservice.service.ShopManagementService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShopManagementServiceImpl implements ShopManagementService {

    private final ShopRepository shopRepository;

    @Override
    @Transactional
    public void createShop(ShopDto shopDto, UUID ownerId) {
        log.info("Creating new shop for owner: {}", ownerId);

        validateShopDtoForCreation(shopDto);

        Shop shop = Shop.builder()
                .shopName(shopDto.getShopName())
                .ownerId(ownerId)
                .description(shopDto.getDescription())
                .logoUrl(shopDto.getLogoUrl())
                .contactEmail(shopDto.getContactEmail())
                .shopStatus(ShopStatus.INACTIVE) // Default status
                .isVerified(false) // Default unverified
                .build();

        shop = shopRepository.save(shop);
        log.debug("Shop created successfully: {}", shop.getShopId());
    }

    @Override
    @Transactional
    public void updateShop(UUID shopId, ShopDto shopDto, UUID ownerId) {
        log.info("Updating shop: {} by owner: {}", shopId, ownerId);

        validateShopDtoForUpdate(shopDto);

        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new ShopException(ReturnCode.SHOP_NOT_FOUND));

        if (!shop.getOwnerId().equals(ownerId)) {
            log.warn("Unauthorized shop update attempt - Shop: {}, RequestedBy: {}", shopId, ownerId);
            throw new UserException(ReturnCode.INVALID_ROLE);
        }

        updateShopFields(shop, shopDto);

        shopRepository.save(shop);
        log.debug("Shop updated successfully: {}", shopId);
    }

    private void validateShopDtoForCreation(ShopDto shopDto) {
        if (shopDto.getShopName() == null || shopDto.getContactEmail() == null) {
            throw new ShopException(ReturnCode.INVALID_PARAMETER);
        }
    }

    private void validateShopDtoForUpdate(ShopDto shopDto) {
        if (shopDto.getShopName() == null &&
                shopDto.getDescription() == null &&
                shopDto.getLogoUrl() == null &&
                shopDto.getContactEmail() == null) {
            throw new ShopException(ReturnCode.INVALID_PARAMETER);
        }
    }

    private void updateShopFields(Shop shop, ShopDto shopDto) {
        if (shopDto.getShopName() != null) {
            shop.setShopName(shopDto.getShopName());
        }
        if (shopDto.getDescription() != null) {
            shop.setDescription(shopDto.getDescription());
        }
        if (shopDto.getLogoUrl() != null) {
            shop.setLogoUrl(shopDto.getLogoUrl());
        }
        if (shopDto.getContactEmail() != null) {
            shop.setContactEmail(shopDto.getContactEmail());
        }
    }
}
