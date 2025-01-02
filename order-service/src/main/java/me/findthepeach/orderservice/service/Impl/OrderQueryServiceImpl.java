package me.findthepeach.orderservice.service.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.findthepeach.common.response.constant.ReturnCode;
import me.findthepeach.common.response.exception.OrderException;
import me.findthepeach.common.response.exception.UserException;
import me.findthepeach.orderservice.model.dto.OrderDto;
import me.findthepeach.orderservice.model.entity.Order;
import me.findthepeach.orderservice.repository.OrderRepository;
import me.findthepeach.orderservice.service.OrderQueryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderQueryServiceImpl implements OrderQueryService {

    private final OrderRepository orderRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<OrderDto> searchOrder(UUID merchantId, List<UUID> shopIds, String keyword, int page, int size) {
        log.info("Searching orders for merchant. userId: {}, shopIds: {}, keyword: {}, page: {}, size: {}",
                merchantId, shopIds, keyword, page, size);

        Pageable pageable = PageRequest.of(page, size);
        Page<Order> orders;

        // check if shop is empty
        if (shopIds != null && !shopIds.isEmpty()) {
            orders = orderRepository.findByMerchantIdAndShopIdsAndKeyword(
                    merchantId,
                    shopIds,
                    keyword,
                    pageable
            );
        } else {
            orders = orderRepository.findByMerchantIdAndKeyword(
                    merchantId,
                    keyword,
                    pageable
            );
        }

        return orders.map(this::convertToSimpleDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderDto> searchOrder(UUID userId, String keyword, int page, int size) {
        log.info("Searching orders for user. userId: {}, keyword: {}, page: {}, size: {}",
                userId, keyword, page, size);

        Pageable pageable = PageRequest.of(page, size);

        Page<Order> orders = orderRepository.findByUserIdAndKeyword(
                userId,
                keyword,
                pageable
        );

        return orders.map(this::convertToSimpleDto);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDto getOrderById(UUID orderId, UUID userId) {

        log.info("Getting order detail by id: {}", orderId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderException(ReturnCode.ORDER_NOT_FOUND));

        if(!order.getUserId().equals(userId) && !order.getMerchantId().equals(userId)) {
            throw new UserException(ReturnCode.INVALID_ROLE);
        }

        return convertToDetailDto(order);
    }

    private OrderDto convertToSimpleDto(Order order) {
        return OrderDto.builder()
                .orderId(order.getOrderId())
                .userId(order.getUserId())
                .productId(order.getProductId())
                .shopId(order.getShopId())
                .quantity(order.getQuantity())
                .status(order.getStatus())
                .build();
    }

    private OrderDto convertToDetailDto(Order order) {
        return OrderDto.builder()
                .orderId(order.getOrderId())
                .userId(order.getUserId())
                .productId(order.getProductId())
                .shopId(order.getShopId())
                .quantity(order.getQuantity())
                .status(order.getStatus())
                // details
                .price(order.getPrice())
                .totalPrice(order.getTotalPrice())
                .address(order.getAddress())
                .trackingNumber(order.getTrackingNumber())
                .carrier(order.getCarrier())
                .build();
    }
}
