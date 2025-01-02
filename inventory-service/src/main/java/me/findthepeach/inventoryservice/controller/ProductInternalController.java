package me.findthepeach.inventoryservice.controller;

import lombok.RequiredArgsConstructor;
import me.findthepeach.common.annotation.InternalApiAuth;
import me.findthepeach.inventoryservice.model.dto.CheckInventoryDto;
import me.findthepeach.inventoryservice.service.ProductQueryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/inventory/internal")
@RequiredArgsConstructor
public class ProductInternalController {

    private final ProductQueryService productQueryService;

    @GetMapping("/checking-stock")
    @InternalApiAuth
    public CheckInventoryDto checkInventory(@RequestBody CheckInventoryDto checkInventoryDto) {
        return productQueryService.checkInventory(checkInventoryDto);
    }

}
