package me.findthepeach.shopservice.controller;

import lombok.RequiredArgsConstructor;
import me.findthepeach.common.dto.ShopOwnerResponseDto;
import me.findthepeach.common.enums.ShopStatus;
import me.findthepeach.shopservice.model.dto.ShopDto;
import me.findthepeach.shopservice.service.ShopQueryService;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/shop")
@RequiredArgsConstructor
public class ShopQueryController {

    private final ShopQueryService shopQueryService;

    @GetMapping("/{shopId}")
    public ShopDto getShop(@PathVariable UUID shopId) {
        return shopQueryService.getShopInfo(shopId);
    }

    @GetMapping("/search")
    public Page<ShopDto> getShops(@RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "15") int size,
                                  @RequestParam(required = false) String keyword){
        return shopQueryService.searchShops(page, size, keyword);
    }

    @GetMapping("/owner/shops")
    public Page<ShopDto> getShopsByOwner(@RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "15") int size,
                                         @RequestParam(required = false) List<ShopStatus> statuses,
                                         @AuthenticationPrincipal Jwt jwt) {
        UUID ownerId = UUID.fromString(jwt.getClaimAsString("sub"));
        return shopQueryService.getAllShopsByOwnerIdAndStatuses(page, size, ownerId, statuses);
    }

    @GetMapping("/owner/{shopId}")
    public ShopOwnerResponseDto checkOwner(@PathVariable UUID shopId, @AuthenticationPrincipal Jwt jwt) {
        UUID ownerId = UUID.fromString(jwt.getClaimAsString("sub"));
        return shopQueryService.checkOwner(shopId, ownerId);
    }
}
