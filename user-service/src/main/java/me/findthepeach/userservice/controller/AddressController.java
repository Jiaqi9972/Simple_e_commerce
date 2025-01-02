package me.findthepeach.userservice.controller;

import lombok.RequiredArgsConstructor;
import me.findthepeach.common.dto.AddressDto;
import me.findthepeach.userservice.service.AddressService;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @PostMapping("/address/add")
    public void addAddress(@RequestBody AddressDto addressDto,
                           @AuthenticationPrincipal Jwt jwt){
        UUID userId = UUID.fromString(jwt.getSubject());
        addressService.addAddress(addressDto, userId);
    }

    @GetMapping("/address")
    public Page<AddressDto> getAddresses(@RequestParam int page,
                                         @RequestParam int size,
                                         @AuthenticationPrincipal Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getSubject());
        return addressService.getAddresses(userId, page, size);
    }

    @PatchMapping("/address/{addressId}")
    public void updateAddress(@PathVariable UUID addressId, @RequestBody AddressDto addressDto, @AuthenticationPrincipal Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getSubject());
        addressDto.setAddressId(addressId);
        addressService.updateAddress(addressDto, userId);
    }

    @DeleteMapping("/address/{addressId}")
    public void deleteAddress(@PathVariable UUID addressId, @AuthenticationPrincipal Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getSubject());
        addressService.deleteAddress(addressId, userId);
    }

}
