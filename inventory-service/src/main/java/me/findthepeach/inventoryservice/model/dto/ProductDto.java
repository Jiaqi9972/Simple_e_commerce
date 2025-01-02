package me.findthepeach.inventoryservice.model.dto;

import lombok.Builder;
import lombok.Data;
import me.findthepeach.common.enums.ProductStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class ProductDto {
    private UUID productId;
    private String productName;
    private BigDecimal price;
    private String description;
    private List<String> productImageUrls;
    private LocalDateTime createdAt;
    private ProductStatus status;
    private int availableQuantity;
    private int totalQuantity;
    private int reservedQuantity;

    // Shop related fields
    private ShopDto shopDto;
}
