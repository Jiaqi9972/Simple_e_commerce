package me.findthepeach.orderservice.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ShippingDto {
    private String trackingNumber;
    private String carrier;
}
