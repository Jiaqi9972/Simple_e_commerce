package me.findthepeach.inventoryservice.model.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class CheckInventoryDto {

    private UUID productId;
    private int quantity;
    private UUID shopId;
    private UUID merchantId;
    private boolean hasStock;
}
