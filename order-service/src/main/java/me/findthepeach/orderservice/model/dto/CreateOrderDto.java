package me.findthepeach.orderservice.model.dto;

import lombok.Builder;
import lombok.Data;
import me.findthepeach.common.dto.AddressDto;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class CreateOrderDto {

    private UUID userId;
    private UUID productId;
    private Integer quantity;
    private BigDecimal price;
    private BigDecimal totalPrice;
    private AddressDto address;

}
