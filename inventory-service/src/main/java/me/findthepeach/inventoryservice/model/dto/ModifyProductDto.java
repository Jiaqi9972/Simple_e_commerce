package me.findthepeach.inventoryservice.model.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class ModifyProductDto {

    private UUID productId;
    private String productName;
    private UUID shopId;
    private BigDecimal price;
    private String description;
    private List<String> productImageUrls;
}
