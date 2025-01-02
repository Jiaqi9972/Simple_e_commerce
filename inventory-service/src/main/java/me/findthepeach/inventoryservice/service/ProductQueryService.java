package me.findthepeach.inventoryservice.service;

import me.findthepeach.common.enums.ProductStatus;
import me.findthepeach.inventoryservice.model.dto.ProductDto;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface ProductQueryService {

    // details (only active)
    ProductDto getProductById(UUID productId);

    // summary
    Page<ProductDto> searchProducts(int page, int size, String keyword, String shopId);

    // need auth
    Page<ProductDto> searchProductsByOwner(UUID ownerId, int page, int size,
                                           String keyword, String shopId, List<ProductStatus> statuses);
    // need auth
    ProductDto ownerGetProductById(UUID ownerId, UUID productId);

}
