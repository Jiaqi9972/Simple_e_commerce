package me.findthepeach.orderservice.service.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.findthepeach.common.dto.AddressDto;
import me.findthepeach.common.dto.CheckInventoryDto;
import me.findthepeach.common.response.constant.ReturnCode;
import me.findthepeach.common.response.exception.OrderException;
import me.findthepeach.common.response.exception.UserException;
import me.findthepeach.orderservice.model.dto.CreateOrderDto;
import me.findthepeach.orderservice.model.dto.ShippingDto;
import me.findthepeach.orderservice.model.entity.Order;
import me.findthepeach.orderservice.model.enums.OrderStatus;
import me.findthepeach.orderservice.repository.OrderRepository;
import me.findthepeach.orderservice.service.OrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final WebClient webClient;

    @Override
    @Transactional
    public void createOrder(CreateOrderDto createOrderDto, UUID userId) {
        log.info("Create Order for user: {}", userId);

        // check role
        if(!createOrderDto.getUserId().equals(userId)){
            throw new UserException(ReturnCode.INVALID_ROLE);
        }

        // check inventory
        CheckInventoryDto checkRequest = CheckInventoryDto.builder()
                .productId(createOrderDto.getProductId())
                .quantity(createOrderDto.getQuantity())
                .build();

        CheckInventoryDto inventoryResult = webClient.post()
                .uri("/api/v1/inventory/internal/checking-stock")
                .header("Internal-Service-Token", "internal-token-123")
                .bodyValue(checkRequest)
                .retrieve()
                .bodyToMono(CheckInventoryDto.class)
                .block();

        if(!inventoryResult.isHasStock()){
            throw new OrderException(ReturnCode.STOCK_NOT_ENOUGH);
        }

        // check address
        AddressDto address = createOrderDto.getAddress();
        validateAddress(address);

        Order order = Order.builder()
                .userId(userId)
                .shopId(inventoryResult.getShopId())
                .merchantId(inventoryResult.getMerchantId())
                .quantity(createOrderDto.getQuantity())
                .price(createOrderDto.getPrice())
                .totalPrice(createOrderDto.getTotalPrice())
                .address(address)
                .status(OrderStatus.PENDING)
                .build();

        orderRepository.save(order);
    }

    @Override
    @Transactional
    public void completeOrder(UUID orderId, UUID userId) {
        log.info("Attempting to complete order: {} for user: {}", orderId, userId);

        // get order
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> {
                    log.error("Order not found: {}", orderId);
                    return new OrderException(ReturnCode.ORDER_NOT_FOUND);
                });

        // only user can complete order
        if (!order.getUserId().equals(userId)) {
            log.error("User {} is not authorized to complete order: {}", userId, orderId);
            throw new UserException(ReturnCode.INVALID_ROLE);
        }

        // mark order completed
        if (order.getStatus() != OrderStatus.PENDING) {
            log.error("Order {} is not in PENDING status, cannot complete", orderId);
            throw new OrderException(ReturnCode.INVALID_ORDER_STATUS);
        }

        order.setStatus(OrderStatus.COMPLETED);
        orderRepository.save(order);
        log.debug("Order {} successfully completed by user: {}", orderId, userId);
    }

    @Override
    @Transactional
    public void shipOrder(UUID orderId, UUID userId, ShippingDto shippingDto) {
        log.info("Attempting to ship order: {} for user: {}", orderId, userId);

        // get order
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> {
                    log.error("Order not found: {}", orderId);
                    return new OrderException(ReturnCode.ORDER_NOT_FOUND);
                });

        // only merchant can ship
        if (!order.getMerchantId().equals(userId)) {
            log.error("User {} is not authorized to ship order: {}", userId, orderId);
            throw new UserException(ReturnCode.INVALID_ROLE);
        }

        // update status
        if (order.getStatus() != OrderStatus.PENDING) {
            log.error("Order {} is not in PENDING status, cannot ship", orderId);
            throw new OrderException(ReturnCode.INVALID_ORDER_STATUS);
        }

        log.info("Shipping order: {} with tracking number: {} via carrier: {}", orderId, shippingDto.getTrackingNumber(), shippingDto.getCarrier());

        order.setStatus(OrderStatus.SHIPPING);
        order.setCarrier(shippingDto.getCarrier());
        order.setTrackingNumber(shippingDto.getTrackingNumber());
        orderRepository.save(order);

        log.debug("Order {} successfully shipped by merchant: {}", orderId, userId);
    }


    @Override
    public void cancelOrder(UUID orderId, UUID userId) {
        log.info("Attempting to cancel order: {} for user: {}", orderId, userId);

        // get order
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> {
                    log.error("Order not found: {}", orderId);
                    return new OrderException(ReturnCode.ORDER_NOT_FOUND);
                });

        // check status
        if (order.getStatus() != OrderStatus.PENDING) {
            log.error("Order {} is not in PENDING status, cannot cancel", orderId);
            throw new OrderException(ReturnCode.INVALID_ORDER_STATUS);
        }

        // check user
        if (!order.getUserId().equals(userId) && !order.getMerchantId().equals(userId)) {
            log.error("User {} is not authorized to cancel order: {}", userId, orderId);
            throw new UserException(ReturnCode.INVALID_ROLE);
        }

        // update status
        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
        log.info("Order {} successfully cancelled by user: {}", orderId, userId);
    }

    private void validateAddress(AddressDto address) {
        if (address == null) {
            throw new UserException(ReturnCode.ILLGAL_ADDRESS);
        }

        // Check if address ID is null (assuming it should be present)
        if (address.getAddressId() == null) {
            throw new UserException(ReturnCode.ILLGAL_ADDRESS);
        }

        // Check if required fields are not empty
        if (address.getStreet() == null || address.getStreet().trim().isEmpty()) {
            throw new UserException(ReturnCode.ILLGAL_ADDRESS);
        }

        if (address.getCity() == null || address.getCity().trim().isEmpty()) {
            throw new UserException(ReturnCode.ILLGAL_ADDRESS);
        }

        if (address.getState() == null || address.getState().trim().isEmpty()) {
            throw new UserException(ReturnCode.ILLGAL_ADDRESS);
        }

        if (address.getZipCode() == null || address.getZipCode().trim().isEmpty()) {
            throw new UserException(ReturnCode.ILLGAL_ADDRESS);
        }

        if (address.getCountry() == null || address.getCountry().trim().isEmpty()) {
            throw new UserException(ReturnCode.ILLGAL_ADDRESS);
        }

        // Check if receiver name is provided
        if (address.getReceiver() == null || address.getReceiver().trim().isEmpty()) {
            throw new UserException(ReturnCode.ILLGAL_ADDRESS);
        }
    }
}
