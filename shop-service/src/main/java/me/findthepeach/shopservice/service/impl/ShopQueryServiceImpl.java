package me.findthepeach.shopservice.service.impl;

import lombok.RequiredArgsConstructor;
import me.findthepeach.common.dto.ShopOwnerResponseDto;
import me.findthepeach.common.enums.ShopStatus;
import me.findthepeach.common.response.constant.ReturnCode;
import me.findthepeach.common.response.exception.ShopException;
import me.findthepeach.shopservice.model.dto.ShopDto;
import me.findthepeach.shopservice.model.entity.Shop;
import me.findthepeach.shopservice.repository.ShopRepository;
import me.findthepeach.shopservice.service.ShopQueryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ShopQueryServiceImpl implements ShopQueryService {

    private final ShopRepository shopRepository;

    @Override
    @Transactional(readOnly = true)
    public ShopDto getShopInfo(UUID shopId) {
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new ShopException(ReturnCode.SHOP_NOT_FOUND));

        return convertToDto(shop);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ShopDto> searchShops(int page, int size, String keyword) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        // If keyword is empty or null, return all shops
        if (keyword == null || keyword.trim().isEmpty()) {
            Page<Shop> shops = shopRepository.findByShopStatus(ShopStatus.ACTIVE, pageable);
            return shops.map(this::convertToDto);
        }

        // Search shops by keyword in name or description
        Page<Shop> shops = shopRepository.findByShopNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndShopStatus(
                keyword, keyword, ShopStatus.ACTIVE, pageable);

        return shops.map(this::convertToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ShopDto> getAllShopsByOwnerIdAndStatuses(int page, int size, UUID ownerId, List<ShopStatus> statuses) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        List<ShopStatus> safeStatuses = Optional.ofNullable(statuses).orElse(Collections.emptyList());

        if (safeStatuses.isEmpty()) {
            // if safeStatuses is null, return all statuses
            return shopRepository.findByOwnerId(ownerId, pageable)
                    .map(this::convertToDto);
        }

        return shopRepository.findByOwnerIdAndShopStatusIn(ownerId, safeStatuses, pageable)
                .map(this::convertToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public ShopOwnerResponseDto checkOwner(UUID shopId, UUID ownerId) {
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new ShopException(ReturnCode.SHOP_NOT_FOUND));
        return ShopOwnerResponseDto.builder()
                .shopId(shop.getShopId())
                .ownerId(shop.getOwnerId())
                .isOwner(shop.getOwnerId().equals(ownerId))
                .build();
    }

    private ShopDto convertToDto(Shop shop) {
        return ShopDto.builder()
                .shopId(shop.getShopId())
                .shopName(shop.getShopName())
                .shopStatus(shop.getShopStatus().name())
                .description(shop.getDescription())
                .contactEmail(shop.getContactEmail())
                .logoUrl(shop.getLogoUrl())
                .isVerified(shop.isVerified())
                .build();
    }
}
