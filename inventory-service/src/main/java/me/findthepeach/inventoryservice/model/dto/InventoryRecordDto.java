package me.findthepeach.inventoryservice.model.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class InventoryRecordDto {
    private int quantityChanged;
    private UUID productId;
    private String productName;
    private String description;
}
