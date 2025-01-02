package me.findthepeach.shopservice.model.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class ShopDto {

    private UUID shopId;
    private String shopName;
    private UUID ownerId;
    private String description;
    private String logoUrl;
    private String contactEmail;
    private boolean isVerified;
    private String shopStatus;
}
