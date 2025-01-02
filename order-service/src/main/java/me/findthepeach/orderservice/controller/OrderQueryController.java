package me.findthepeach.orderservice.controller;

import lombok.RequiredArgsConstructor;
import me.findthepeach.common.enums.Role;
import me.findthepeach.common.response.constant.ReturnCode;
import me.findthepeach.common.response.exception.UserException;
import me.findthepeach.orderservice.model.dto.OrderDto;
import me.findthepeach.orderservice.service.OrderQueryService;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/order")
@RequiredArgsConstructor
public class OrderQueryController {

    private final OrderQueryService orderQueryService;

    @GetMapping("/search")
    public Page<OrderDto> getOrders(@RequestParam(defaultValue = "0") int page,
                                    @RequestParam(defaultValue = "15") int size,
                                    @RequestParam(required = false) String keyword,
                                    @RequestParam(required = false) List<UUID> shopIds,
                                    @AuthenticationPrincipal Jwt jwt){

        UUID userId = UUID.fromString(jwt.getClaimAsString("userId"));
        String roleStr = jwt.getClaimAsStringList("cognito:groups").get(0);
        Role role = Role.valueOf(roleStr.toUpperCase());

        if (Role.CUSTOMER.equals(role)) {
            return orderQueryService.searchOrder(userId, keyword, page, size);
        } else if (Role.MERCHANT.equals(role)) {
            return orderQueryService.searchOrder(userId, shopIds, keyword, page, size);
        } else{
            throw new UserException(ReturnCode.INVALID_ROLE);
        }
    }

    @GetMapping("/{orderId}")
    public OrderDto getOrderById(@PathVariable UUID orderId,
                                 @AuthenticationPrincipal Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getClaimAsString("userId"));
        return orderQueryService.getOrderById(orderId, userId);
    }
}
