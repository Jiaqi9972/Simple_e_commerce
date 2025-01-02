package me.findthepeach.orderservice.controller;

import lombok.RequiredArgsConstructor;
import me.findthepeach.orderservice.model.dto.CreateOrderDto;
import me.findthepeach.orderservice.model.dto.ShippingDto;
import me.findthepeach.orderservice.service.OrderService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/create")
    public void createOrder(@RequestBody CreateOrderDto createOrderDto,
                            @AuthenticationPrincipal Jwt jwt){
        UUID userId = UUID.fromString(jwt.getClaimAsString("sub"));
        orderService.createOrder(createOrderDto, userId);
    }

    @PatchMapping("/cancel/{orderId}")
    public void cancelOrder(@RequestParam UUID orderId,
                            @AuthenticationPrincipal Jwt jwt){
        UUID userId = UUID.fromString(jwt.getClaimAsString("sub"));
        orderService.cancelOrder(orderId, userId);
    }

    @PatchMapping("/ship/{orderId}")
    public void shipOrder(@RequestParam UUID orderId,
                          @RequestBody ShippingDto shippingDto,
                          @AuthenticationPrincipal Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getClaimAsString("sub"));
        orderService.shipOrder(orderId, userId, shippingDto);
    }

    @PatchMapping("/complete/{orderId}")
    public void completeOrder(@RequestParam UUID orderId,
                              @AuthenticationPrincipal Jwt jwt){
        UUID userId = UUID.fromString(jwt.getClaimAsString("sub"));
        orderService.completeOrder(orderId, userId);
    }
}
