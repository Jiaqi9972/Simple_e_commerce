package me.findthepeach.orderservice.service;

import me.findthepeach.orderservice.model.dto.CreateOrderDto;
import me.findthepeach.orderservice.model.dto.ShippingDto;

import java.util.UUID;

public interface OrderService {

    void createOrder(CreateOrderDto createOrderDto, UUID userId);

    void cancelOrder(UUID orderId, UUID userId);

    void completeOrder(UUID orderId, UUID userId);

    void shipOrder(UUID orderId, UUID userId, ShippingDto shippingDto);


}
