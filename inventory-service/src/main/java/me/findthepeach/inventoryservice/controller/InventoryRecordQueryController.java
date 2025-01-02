package me.findthepeach.inventoryservice.controller;

import lombok.RequiredArgsConstructor;
import me.findthepeach.inventoryservice.model.dto.InventoryRecordDto;
import me.findthepeach.inventoryservice.service.InventoryRecordQueryService;
import me.findthepeach.inventoryservice.utils.ValidationUtil;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
public class InventoryRecordQueryController {

    private final InventoryRecordQueryService inventoryRecordQueryService;
    private final ValidationUtil validationUtil;

    @GetMapping("/get-record/{recordId}")
    public InventoryRecordDto getInventoryRecord(@AuthenticationPrincipal Jwt jwt, @RequestParam UUID recordId) {
        validationUtil.validateMerchantRole(jwt);
        UUID ownerId = UUID.fromString(jwt.getClaimAsString("sub"));
        return inventoryRecordQueryService.getInventoryRecordById(ownerId, recordId);
    }

    @GetMapping("/search-record")
    public Page<InventoryRecordDto> searchRecord(@RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "15") int size,
                                                 @AuthenticationPrincipal Jwt jwt,
                                                 @RequestParam String keyword) {
        validationUtil.validateMerchantRole(jwt);
        UUID ownerId = UUID.fromString(jwt.getClaimAsString("sub"));
        return inventoryRecordQueryService.getInventoryRecords(ownerId, page, size, keyword);
    }
}
