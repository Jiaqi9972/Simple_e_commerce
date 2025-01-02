package me.findthepeach.shopservice.service;

import me.findthepeach.common.dto.ShopOwnerResponseDto;
import me.findthepeach.common.enums.ShopStatus;
import me.findthepeach.shopservice.model.dto.ShopDto;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface ShopQueryService {

    ShopDto getShopInfo(UUID shopId);

    Page<ShopDto> searchShops(int page, int size, String keyword);

    Page<ShopDto> getAllShopsByOwnerIdAndStatuses(int page, int size, UUID ownerId, List<ShopStatus> statuses);

    ShopOwnerResponseDto checkOwner(UUID shopId, UUID ownerId);
}
