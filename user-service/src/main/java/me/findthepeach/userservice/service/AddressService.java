package me.findthepeach.userservice.service;

import me.findthepeach.common.dto.AddressDto;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface AddressService {
    void addAddress(AddressDto addressDto, UUID userId);
    void updateAddress(AddressDto addressDto, UUID userId);
    void deleteAddress(UUID addressId, UUID userId);
    Page<AddressDto> getAddresses(UUID userId, int page, int size);
}
