package me.findthepeach.inventoryservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.findthepeach.common.response.constant.ReturnCode;
import me.findthepeach.common.response.exception.InventoryException;
import me.findthepeach.common.response.exception.UserException;
import me.findthepeach.inventoryservice.model.dto.InventoryRecordDto;
import me.findthepeach.inventoryservice.model.entity.InventoryRecord;
import me.findthepeach.inventoryservice.model.entity.Product;
import me.findthepeach.inventoryservice.repository.InventoryRecordRepository;
import me.findthepeach.inventoryservice.repository.ProductRepository;
import me.findthepeach.inventoryservice.service.InventoryRecordQueryService;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class InventoryRecordQueryServiceImpl implements InventoryRecordQueryService {

    private final InventoryRecordRepository inventoryRecordRepository;
    private final ProductRepository productRepository;

    @Override
    public InventoryRecordDto getInventoryRecordById(UUID ownerId, UUID recordId) {
        // search inventory
        InventoryRecord record = inventoryRecordRepository.findById(recordId)
                .orElseThrow(() -> new InventoryException(ReturnCode.INVENTORY_NOT_FOUND));

        // search product
        Product product = productRepository.findById(record.getProductId())
                .orElseThrow(() -> new InventoryException(ReturnCode.PRODUCT_NOT_FOUND));

        // check owner
        if (!product.getOwnerId().equals(ownerId)) {
            throw new UserException(ReturnCode.INVALID_ROLE);
        }

        return convertToDto(record, product);
    }

    @Override
    public Page<InventoryRecordDto> getInventoryRecords(UUID ownerId, int page, int size, String keyword) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        // search all ID by the owner
        List<UUID> productIds = productRepository.findByOwnerId(ownerId)
                .stream()
                .map(Product::getProductId)
                .collect(Collectors.toList());

        if (productIds.isEmpty()) {
            return Page.empty();
        }

        // search inventory
        Page<InventoryRecord> recordPage;
        if (keyword != null && !keyword.trim().isEmpty()) {
            recordPage = inventoryRecordRepository.findByProductIdInAndProductNameContaining(
                    productIds, keyword, pageable);
        } else {
            recordPage = inventoryRecordRepository.findByProductIdIn(productIds, pageable);
        }

        // get product info
        Map<UUID, Product> productMap = productRepository.findByProductIdIn(
                        recordPage.getContent().stream()
                                .map(InventoryRecord::getProductId)
                                .collect(Collectors.toList()))
                .stream()
                .collect(Collectors.toMap(Product::getProductId, Function.identity()));

        // convert to dto
        List<InventoryRecordDto> dtoList = recordPage.getContent().stream()
                .map(record -> convertToDto(record, productMap.get(record.getProductId())))
                .collect(Collectors.toList());

        return new PageImpl<>(dtoList, pageable, recordPage.getTotalElements());
    }

    private InventoryRecordDto convertToDto(InventoryRecord record, Product product) {
        return InventoryRecordDto.builder()
                .quantityChanged(record.getQuantityChanged())
                .productId(record.getProductId())
                .description(record.getDescription())
                .productName(product.getProductName())
                .build();
    }
}
