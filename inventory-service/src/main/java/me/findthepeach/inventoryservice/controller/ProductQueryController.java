package me.findthepeach.inventoryservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.findthepeach.common.enums.ProductStatus;
import me.findthepeach.inventoryservice.model.dto.ProductDto;
import me.findthepeach.inventoryservice.service.ProductQueryService;
import me.findthepeach.inventoryservice.utils.ValidationUtil;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/inventory/product")
@RequiredArgsConstructor
@Slf4j
public class ProductQueryController {

    private final ProductQueryService productQueryService;
    private final ValidationUtil validationUtil;

    /**
     * Get product details by product ID.
     */
    @GetMapping("/{productId}")
    public ProductDto getProductById(@PathVariable UUID productId) {
        return productQueryService.getProductById(productId);

    }

    /**
     * Search for products with optional filtering by keyword and shop ID.
     */
    @GetMapping("/search")
    public Page<ProductDto> searchProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String shopId) {
        return productQueryService.searchProducts(page, size, keyword, shopId);
    }

    /**
     * Search for products by owner with optional filtering.
     */
    @GetMapping("/owner/search")
    public Page<ProductDto> searchProductsByOwner(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String shopId,
            @RequestParam(required = false) List<ProductStatus> statuses,
            @AuthenticationPrincipal Jwt jwt) {
        validationUtil.validateMerchantRole(jwt);
        UUID ownerId = UUID.fromString(jwt.getClaimAsString("sub"));
        return productQueryService.searchProductsByOwner(ownerId, page, size, keyword, shopId, statuses);
    }

    /**
     * Get product details by owner and product ID.
     */
    @GetMapping("/owner/details/{productId}")
    public ProductDto ownerGetProductById(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID productId) {
        validationUtil.validateMerchantRole(jwt);
        UUID ownerId = UUID.fromString(jwt.getClaimAsString("sub"));
        return productQueryService.ownerGetProductById(ownerId, productId);

    }
}

