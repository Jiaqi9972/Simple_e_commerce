package me.findthepeach.shopservice.controller;

import lombok.RequiredArgsConstructor;
import me.findthepeach.shopservice.service.ShopStatusService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/shop")
@RequiredArgsConstructor
public class ShopStatusController {

    private final ShopStatusService shopStatusService;

    private UUID getCurrentUserId(Jwt jwt) {
        return UUID.fromString(jwt.getClaimAsString("sub"));
    }

    @PatchMapping("/verify/{shopId}")
    public void verify(@PathVariable("shopId") UUID shopId, @AuthenticationPrincipal Jwt jwt) {
        shopStatusService.verifyShop(shopId, getCurrentUserId(jwt));
    }

    @PatchMapping("/mark-as-deleted/{shopId}")
    public void markShopAsDeleted(@PathVariable("shopId") UUID shopId, @AuthenticationPrincipal Jwt jwt) {
        shopStatusService.markShopAsDeleted(shopId, getCurrentUserId(jwt));
    }

    @PatchMapping("/activate/{shopId}")
    public void activate(@PathVariable("shopId") UUID shopId, @AuthenticationPrincipal Jwt jwt) {
        shopStatusService.activateShop(shopId, getCurrentUserId(jwt));
    }

    @PatchMapping("/deactivate/{shopId}")
    public void deactivate(@PathVariable("shopId") UUID shopId, @AuthenticationPrincipal Jwt jwt) {
        shopStatusService.deactivateShop(shopId, getCurrentUserId(jwt));
    }

}
