package me.findthepeach.inventoryservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.findthepeach.common.enums.ProductStatus;
import me.findthepeach.common.response.constant.ReturnCode;
import me.findthepeach.common.response.exception.InventoryException;
import me.findthepeach.inventoryservice.model.dto.ModifyProductDto;
import me.findthepeach.inventoryservice.model.entity.Product;
import me.findthepeach.inventoryservice.repository.ProductRepository;
import me.findthepeach.inventoryservice.service.ProductManagementService;
import me.findthepeach.inventoryservice.utils.ValidationUtil;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductManagementServiceImpl implements ProductManagementService {

    private final ProductRepository productRepository;
    private final ValidationUtil validationUtil;

    @Override
    @Transactional
    public void createProduct(ModifyProductDto modifyProductDto, UUID ownerId, Jwt jwt) {
        log.info("Creating new product - owner: {}, shopId: {}", ownerId, modifyProductDto.getShopId());

        validationUtil.validateRoleAndShop(modifyProductDto, ownerId, jwt);

        Product product = Product.builder()
                .productName(modifyProductDto.getProductName())
                .shopId(modifyProductDto.getShopId())
                .ownerId(ownerId)
                .price(modifyProductDto.getPrice().setScale(2, RoundingMode.HALF_UP))
                .description(modifyProductDto.getDescription())
                .productImageUrls(modifyProductDto.getProductImageUrls())
                .status(ProductStatus.ACTIVE)
                .build();
        product = productRepository.save(product);
        log.info("Product created successfully - productId: {}, shopId: {}",
                product.getProductId(), product.getShopId());
    }

    @Override
    @Transactional
    public void updateProduct(ModifyProductDto modifyProductDto, UUID ownerId) {
        UUID productId = modifyProductDto.getProductId();
        log.info("Updating product - productId: {}, owner: {}", productId, ownerId);

        validationUtil.validateRoleAndShop(productId, ownerId);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new InventoryException(ReturnCode.PRODUCT_NOT_FOUND));

        updateProductFields(product, modifyProductDto);

        productRepository.save(product);
        log.info("Product updated successfully - productId: {}", productId);
    }

    @Override
    @Transactional
    public void deleteProduct(UUID productId, UUID ownerId) {
        log.info("Deleting product - productId: {}, owner: {}", productId, ownerId);
        validationUtil.validateRoleAndShop(productId, ownerId);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new InventoryException(ReturnCode.PRODUCT_NOT_FOUND));

        productRepository.delete(product);
        log.info("Product deleted successfully - productId: {}", productId);
    }

    // Update product status with reusable method
    private void updateProductStatus(UUID productId, UUID ownerId, ProductStatus status) {
        log.info("Updating product status - productId: {}, owner: {}, status: {}",
                productId, ownerId, status);
        validationUtil.validateRoleAndShop(productId, ownerId);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new InventoryException(ReturnCode.PRODUCT_NOT_FOUND));

        product.setStatus(status);
        log.info("Product status updated successfully - productId: {}, status: {}",
                productId, status);
    }

    @Override
    @Transactional
    public void deactivateProduct(UUID productId, UUID ownerId) {
        updateProductStatus(productId, ownerId, ProductStatus.INACTIVE);
    }

    @Override
    @Transactional
    public void activateProduct(UUID productId, UUID ownerId) {
        updateProductStatus(productId, ownerId, ProductStatus.ACTIVE);
    }

    // Update product fields with validation
    private void updateProductFields(Product product, ModifyProductDto modifyProductDto) {
        // Update product name if provided and not empty
        if (modifyProductDto.getProductName() != null && !modifyProductDto.getProductName().trim().isEmpty()) {
            product.setProductName(modifyProductDto.getProductName().trim());
        }

        // Update price if provided and validate
        if (modifyProductDto.getPrice() != null) {
            Assert.isTrue(modifyProductDto.getPrice().compareTo(BigDecimal.ZERO) > 0,
                    "Price must be greater than 0");
            product.setPrice(modifyProductDto.getPrice().setScale(2, RoundingMode.HALF_UP));
        }

        // Update description if provided
        if (modifyProductDto.getDescription() != null) {
            product.setDescription(modifyProductDto.getDescription().trim());
        }

        // Update product image URLs if provided
        if (modifyProductDto.getProductImageUrls() != null) {
            List<String> validUrls = modifyProductDto.getProductImageUrls().stream()
                    .filter(url -> url != null && !url.trim().isEmpty())
                    .map(String::trim)
                    .collect(Collectors.toList());

            if (!validUrls.isEmpty()) {
                product.setProductImageUrls(validUrls);
            }
        }
    }
}