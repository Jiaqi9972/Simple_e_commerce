package me.findthepeach.inventoryservice.service;

import me.findthepeach.inventoryservice.model.dto.InventoryRecordDto;

import java.util.UUID;

public interface InventoryManagementService {

    void createInventoryRecord(InventoryRecordDto inventoryRecordDto, UUID ownerId);

}
