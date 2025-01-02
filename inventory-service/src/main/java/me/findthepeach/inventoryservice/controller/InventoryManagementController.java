package me.findthepeach.inventoryservice.controller;

import lombok.RequiredArgsConstructor;
import me.findthepeach.inventoryservice.model.dto.InventoryRecordDto;
import me.findthepeach.inventoryservice.service.InventoryManagementService;
import me.findthepeach.inventoryservice.utils.ValidationUtil;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
public class InventoryManagementController {

    private final InventoryManagementService inventoryManagementService;
    private final ValidationUtil validationUtil;

    @PostMapping("/create")
    public void createInventoryRecord(@RequestBody InventoryRecordDto inventoryRecordDto,
                                      @AuthenticationPrincipal Jwt jwt) {
        validationUtil.validateMerchantRole(jwt);
        UUID ownerId = UUID.fromString(jwt.getClaimAsString("sub"));
        inventoryManagementService.createInventoryRecord(inventoryRecordDto, ownerId);
    }
}
