package me.findthepeach.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
public class ShopOwnerResponseDto {
    private UUID shopId;
    private UUID ownerId;
    private boolean isOwner;
}
