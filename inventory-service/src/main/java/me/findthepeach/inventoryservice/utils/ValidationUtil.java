package me.findthepeach.inventoryservice.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.findthepeach.common.dto.ShopOwnerResponseDto;
import me.findthepeach.common.response.constant.ReturnCode;
import me.findthepeach.common.response.exception.InventoryException;
import me.findthepeach.common.response.exception.UserException;
import me.findthepeach.inventoryservice.model.dto.ModifyProductDto;
import me.findthepeach.inventoryservice.model.entity.Product;
import me.findthepeach.inventoryservice.repository.ProductRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class ValidationUtil {

    private final ProductRepository productRepository;
    private final WebClient shopServiceWebClient;

    // Validate shop ownership through shop service
    public void validateRoleAndShop(ModifyProductDto modifyProductDto, UUID ownerId, Jwt jwt) {
        UUID shopId = modifyProductDto.getShopId();
        log.debug("Validating shop ownership - shopId: {}, ownerId: {}", shopId, ownerId);

        ShopOwnerResponseDto response = shopServiceWebClient.get()
                .uri("/api/v1/shop/owner/{shopId}", shopId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt.getTokenValue())
                .retrieve()
                .bodyToMono(ShopOwnerResponseDto.class)
                .timeout(Duration.ofSeconds(5))
                .block();

        if (response == null || !response.isOwner()) {
            log.warn("Invalid shop ownership - shopId: {}, ownerId: {}", shopId, ownerId);
            throw new InventoryException(ReturnCode.INVALID_ROLE);
        }
        log.debug("Shop ownership validated successfully - shopId: {}", shopId);
    }

    // Validate product ownership
    public void validateRoleAndShop(UUID productId, UUID ownerId) {
        log.debug("Validating product ownership - productId: {}, ownerId: {}", productId, ownerId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new InventoryException(ReturnCode.PRODUCT_NOT_FOUND));

        if (!product.getOwnerId().equals(ownerId)) {
            log.warn("Invalid product ownership - productId: {}, ownerId: {}", productId, ownerId);
            throw new InventoryException(ReturnCode.INVALID_ROLE);
        }
        log.debug("Product ownership validated successfully - productId: {}", productId);
    }

    // check role
    public void validateMerchantRole(Jwt jwt) {
        String roles = jwt.getClaimAsString("roles");
        if (roles == null || !roles.equals("merchant")) {
            throw new UserException(ReturnCode.INVALID_ROLE);
        }
    }
}
