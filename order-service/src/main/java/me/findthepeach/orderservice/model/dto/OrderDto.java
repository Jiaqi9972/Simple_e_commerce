package me.findthepeach.orderservice.model.dto;

import lombok.Builder;
import lombok.Data;
import me.findthepeach.common.dto.AddressDto;
import me.findthepeach.orderservice.model.enums.OrderStatus;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class OrderDto {

    private UUID orderId;
    private UUID userId;
    private UUID productId;
    private UUID shopId;
    private Integer quantity;
    private OrderStatus status;

    // Details
    private BigDecimal price;
    private BigDecimal totalPrice;
    private AddressDto address;
    private String trackingNumber;
    private String carrier;

}
