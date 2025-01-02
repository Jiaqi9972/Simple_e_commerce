package me.findthepeach.inventoryservice.service;

import me.findthepeach.inventoryservice.model.dto.InventoryRecordDto;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface InventoryRecordQueryService {

    Page<InventoryRecordDto> getInventoryRecords(UUID ownerId, int page, int size, String keyword);

    InventoryRecordDto getInventoryRecordById(UUID ownerId, UUID recordId);
}
