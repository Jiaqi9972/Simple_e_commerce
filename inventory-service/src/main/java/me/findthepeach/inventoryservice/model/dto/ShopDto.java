package me.findthepeach.inventoryservice.model.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class ShopDto {
    private UUID shopId;
    private String shopName;
    private String description;
    private String logoUrl;
    private String contactEmail;
    private String shopStatus;
}
