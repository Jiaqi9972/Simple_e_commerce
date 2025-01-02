package me.findthepeach.orderservice.service;

import me.findthepeach.orderservice.model.dto.OrderDto;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface OrderQueryService {

    Page<OrderDto> searchOrder(UUID userId, List<UUID> shopIds, String keyword, int page, int size);

    Page<OrderDto> searchOrder(UUID userId, String keyword, int page, int size);

    OrderDto getOrderById(UUID orderId, UUID userId);
}
