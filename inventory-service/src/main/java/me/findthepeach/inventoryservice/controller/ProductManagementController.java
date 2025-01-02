package me.findthepeach.inventoryservice.controller;

import lombok.RequiredArgsConstructor;
import me.findthepeach.inventoryservice.model.dto.ModifyProductDto;
import me.findthepeach.inventoryservice.service.ProductManagementService;
import me.findthepeach.inventoryservice.utils.ValidationUtil;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
public class ProductManagementController {

    private final ProductManagementService productManagementService;
    private final ValidationUtil validationUtil;

    @PostMapping("/product/create")
    public void createProduct(@RequestBody ModifyProductDto modifyProductDto,
                              @AuthenticationPrincipal Jwt jwt) {
        validationUtil.validateMerchantRole(jwt);
        UUID ownerId = UUID.fromString(jwt.getClaimAsString("sub"));
        productManagementService.createProduct(modifyProductDto, ownerId, jwt);
    }

    @PatchMapping("/product/update/{productId}")
    public void updateProduct(@PathVariable UUID productId,
                              @RequestBody ModifyProductDto modifyProductDto,
                              @AuthenticationPrincipal Jwt jwt) {
        validationUtil.validateMerchantRole(jwt);
        UUID ownerId = UUID.fromString(jwt.getClaimAsString("sub"));
        modifyProductDto.setProductId(productId); // Ensure productId is set
        productManagementService.updateProduct(modifyProductDto, ownerId);
    }

    @DeleteMapping("/product/delete/{productId}")
    public void deleteProduct(@PathVariable UUID productId,
                              @AuthenticationPrincipal Jwt jwt) {
        validationUtil.validateMerchantRole(jwt);
        UUID ownerId = UUID.fromString(jwt.getClaimAsString("sub"));
        productManagementService.deleteProduct(productId, ownerId);
    }

    @PatchMapping("/product/deactivate/{productId}")
    public void deactivateProduct(@PathVariable UUID productId,
                                  @AuthenticationPrincipal Jwt jwt) {
        validationUtil.validateMerchantRole(jwt);
        UUID ownerId = UUID.fromString(jwt.getClaimAsString("sub"));
        productManagementService.deactivateProduct(productId, ownerId);
    }

    @PatchMapping("/product/activate/{productId}")
    public void activateProduct(@PathVariable UUID productId,
                                @AuthenticationPrincipal Jwt jwt) {
        validationUtil.validateMerchantRole(jwt);
        UUID ownerId = UUID.fromString(jwt.getClaimAsString("sub"));
        productManagementService.activateProduct(productId, ownerId);
    }
}
