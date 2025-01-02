package me.findthepeach.inventoryservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.findthepeach.common.response.constant.ReturnCode;
import me.findthepeach.common.response.exception.InventoryException;
import me.findthepeach.inventoryservice.model.dto.InventoryRecordDto;
import me.findthepeach.inventoryservice.model.entity.Inventory;
import me.findthepeach.inventoryservice.model.entity.InventoryRecord;
import me.findthepeach.inventoryservice.repository.InventoryRecordRepository;
import me.findthepeach.inventoryservice.repository.InventoryRepository;
import me.findthepeach.inventoryservice.service.InventoryManagementService;
import me.findthepeach.inventoryservice.utils.ValidationUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class InventoryManagementServiceImpl implements InventoryManagementService {

    private final InventoryRepository inventoryRepository;
    private final InventoryRecordRepository inventoryRecordRepository;
    private final ValidationUtil validationUtil;

    @Override
    @Transactional
    public void createInventoryRecord(InventoryRecordDto inventoryRecordDto, UUID ownerId) {
        UUID productId = inventoryRecordDto.getProductId();
        int quantityChanged = inventoryRecordDto.getQuantityChanged();
        String description = inventoryRecordDto.getDescription();
        log.info("Updating inventory - productId: {}, operator: {}, quantity change: {}",
                productId, ownerId, quantityChanged);

        validationUtil.validateRoleAndShop(productId, ownerId);

        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new InventoryException(ReturnCode.INVENTORY_NOT_FOUND));

        int oldQuantity = inventory.getQuantity();
        int newQuantity = oldQuantity + quantityChanged;

        inventory.setQuantity(newQuantity);
        inventoryRepository.save(inventory);
        log.info("Inventory updated successfully - productId: {}, old quantity: {}, new quantity: {}",
                productId, oldQuantity, newQuantity);

        InventoryRecord inventoryRecord = InventoryRecord.builder()
                .productId(productId)
                .quantityChanged(quantityChanged)
                .description(description)
                .operatorId(ownerId)
                .build();
        inventoryRecordRepository.save(inventoryRecord);
        log.debug("Inventory record created - recordId: {}, productId: {}, quantity change: {}",
                inventoryRecord.getInventoryRecordId(), productId, quantityChanged);
    }
}
