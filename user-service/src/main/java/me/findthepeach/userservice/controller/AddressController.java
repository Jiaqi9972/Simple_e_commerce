package me.findthepeach.userservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.findthepeach.common.dto.AddressDto;
import me.findthepeach.userservice.service.AddressService;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('CUSTOMER')")
@Tag(name = "Address Management", description = "APIs for managing user addresses")
public class AddressController {
    private final AddressService addressService;

    @Operation(summary = "Add a new address")
    @PostMapping("/address")
    public void addAddress(
            @RequestBody @Valid
            @Parameter(description = "Address details", required = true)
            AddressDto addressDto,
            @Parameter(hidden = true)
            @AuthenticationPrincipal Jwt jwt
    ) {
        UUID userId = UUID.fromString(jwt.getSubject());
        addressService.addAddress(addressDto, userId);
    }

    @Operation(summary = "Get user addresses")
    @GetMapping("/address")
    public Page<AddressDto> getAddresses(
            @Parameter(description = "Page number", required = false)
            @RequestParam(defaultValue = "0", required = false) int page,

            @Parameter(description = "Page size", required = false)
            @RequestParam(defaultValue = "10", required = false) int size,

            @Parameter(hidden = true)
            @AuthenticationPrincipal Jwt jwt
    ) {
        UUID userId = UUID.fromString(jwt.getSubject());
        return addressService.getAddresses(userId, page, size);
    }

    @Operation(summary = "Update an address")
    @PatchMapping("/address/{addressId}")
    public void updateAddress(
            @Parameter(description = "Address ID", required = true)
            @PathVariable UUID addressId,
            @RequestBody @Valid
            @Parameter(description = "Address details to update", required = true)
            AddressDto addressDto,
            @Parameter(hidden = true)
            @AuthenticationPrincipal Jwt jwt
    ) {
        UUID userId = UUID.fromString(jwt.getSubject());
        addressDto.setAddressId(addressId);
        addressService.updateAddress(addressDto, userId);
    }

    @Operation(summary = "Delete an address")
    @DeleteMapping("/address/{addressId}")
    public void deleteAddress(
            @Parameter(description = "Address ID to delete", required = true)
            @PathVariable UUID addressId,
            @Parameter(hidden = true)
            @AuthenticationPrincipal Jwt jwt
    ) {
        UUID userId = UUID.fromString(jwt.getSubject());
        addressService.deleteAddress(addressId, userId);
    }
}