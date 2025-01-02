package me.findthepeach.shopservice.controller;

import lombok.RequiredArgsConstructor;
import me.findthepeach.shopservice.model.dto.ShopDto;
import me.findthepeach.shopservice.service.ShopManagementService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/shop")
@RequiredArgsConstructor
public class ShopManagementController {

    private final ShopManagementService shopManagementService;


    private UUID getCurrentUserId(Jwt jwt) {
        return UUID.fromString(jwt.getClaimAsString("sub"));
    }

    @PostMapping("/register")
    public void register(@RequestBody ShopDto shopDto, @AuthenticationPrincipal Jwt jwt) {
        shopManagementService.createShop(shopDto, getCurrentUserId(jwt));
    }

    @PatchMapping("/update/{shopId}")
    public void update(@PathVariable UUID shopId, @RequestBody ShopDto shopDto, @AuthenticationPrincipal Jwt jwt) {
        shopManagementService.updateShop(shopId, shopDto, getCurrentUserId(jwt));
    }
}
