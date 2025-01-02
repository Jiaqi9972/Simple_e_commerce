package me.findthepeach.inventoryservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.findthepeach.common.enums.ProductStatus;
import me.findthepeach.common.response.constant.ReturnCode;
import me.findthepeach.common.response.exception.InventoryException;
import me.findthepeach.common.response.exception.UserException;
import me.findthepeach.inventoryservice.model.dto.CheckInventoryDto;
import me.findthepeach.inventoryservice.model.dto.ProductDto;
import me.findthepeach.inventoryservice.model.dto.ShopDto;
import me.findthepeach.inventoryservice.model.entity.Inventory;
import me.findthepeach.inventoryservice.model.entity.Product;
import me.findthepeach.inventoryservice.repository.InventoryRepository;
import me.findthepeach.inventoryservice.repository.ProductRepository;
import me.findthepeach.inventoryservice.repository.specification.ProductSpecifications;
import me.findthepeach.inventoryservice.service.ProductQueryService;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductQueryServiceImpl implements ProductQueryService {

    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;
    private final WebClient shopServiceClient;

    @Override
    public ProductDto getProductById(UUID productId) {
        Product product = productRepository.findByProductIdAndStatus(productId, ProductStatus.ACTIVE)
                .orElseThrow(() -> new InventoryException(ReturnCode.PRODUCT_NOT_FOUND));

        ShopDto shopInfo = getShopInfo(product.getShopId());
        Inventory inventory = inventoryRepository.findById(productId).orElse(null);
        return convertToBaseDto(product, shopInfo, inventory);
    }

    @Override
    public Page<ProductDto> searchProducts(int page, int size, String keyword, String shopId) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<Product> products;
        if (shopId != null && !shopId.trim().isEmpty()) {
            UUID shopUUID = UUID.fromString(shopId);
            if (keyword != null && !keyword.trim().isEmpty()) {
                products = productRepository.findByShopIdAndStatusAndProductNameContainingIgnoreCase(
                        shopUUID, ProductStatus.ACTIVE, keyword, pageable);
            } else {
                products = productRepository.findByShopIdAndStatus(
                        shopUUID, ProductStatus.ACTIVE, pageable);
            }
        } else if (keyword != null && !keyword.trim().isEmpty()) {
            products = productRepository.findByStatusAndProductNameContainingIgnoreCase(
                    ProductStatus.ACTIVE, keyword, pageable);
        } else {
            products = productRepository.findByStatus(ProductStatus.ACTIVE, pageable);
        }

        return convertToBaseDtoPage(products, pageable);
    }

    @Override
    public Page<ProductDto> searchProductsByOwner(
            UUID ownerId, int page, int size, String keyword,
            String shopId, List<ProductStatus> statuses) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Specification<Product> spec = Specification.where(ProductSpecifications.hasOwnerId(ownerId));

        if (statuses != null && !statuses.isEmpty()) {
            spec = spec.and(ProductSpecifications.hasStatusIn(statuses));
        }

        if (shopId != null && !shopId.trim().isEmpty()) {
            spec = spec.and(ProductSpecifications.hasShopId(UUID.fromString(shopId)));
        }

        if (keyword != null && !keyword.trim().isEmpty()) {
            spec = spec.and(ProductSpecifications.nameContains(keyword));
        }

        Page<Product> products = productRepository.findAll(spec, pageable);
        return convertToDetailDtoPage(products, pageable);
    }

    @Override
    public ProductDto ownerGetProductById(UUID ownerId, UUID productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new InventoryException(ReturnCode.PRODUCT_NOT_FOUND));

        if (!product.getOwnerId().equals(ownerId)) {
            throw new UserException(ReturnCode.INVALID_ROLE);
        }

        ShopDto shopInfo = getShopInfo(product.getShopId());
        Inventory inventory = inventoryRepository.findById(productId).orElse(null);
        return convertToDetailDto(product, shopInfo, inventory);
    }

    @Override
    public CheckInventoryDto checkInventory(CheckInventoryDto checkInventoryDto) {

        UUID productId = checkInventoryDto.getProductId();
        int quantity = checkInventoryDto.getQuantity();

        Inventory inventory = inventoryRepository.findById(productId).orElseThrow(
                () -> new InventoryException(ReturnCode.PRODUCT_NOT_FOUND)
        );

        Product product = productRepository.findById(productId).orElseThrow(
                () -> new InventoryException(ReturnCode.PRODUCT_NOT_FOUND)
        );

        if(quantity > inventory.getQuantity()) {
            checkInventoryDto.setHasStock(false);
        }else{
            checkInventoryDto.setHasStock(true);
            checkInventoryDto.setShopId(product.getShopId());
            checkInventoryDto.setMerchantId(product.getOwnerId());
        }

        return checkInventoryDto;
    }

    private ShopDto getShopInfo(UUID shopId) {
        return shopServiceClient.get()
                .uri("/api/v1/shop/" + shopId)
                .retrieve()
                .bodyToMono(ShopDto.class)
                .block();
    }

    private Map<UUID, Inventory> batchGetInventoryInfo(Set<UUID> productIds) {
        List<Inventory> inventories = inventoryRepository.findAllById(productIds);
        return inventories.stream()
                .collect(Collectors.toMap(Inventory::getProductId, Function.identity()));
    }

    private ProductDto convertToBaseDto(Product product, ShopDto shopInfo, Inventory inventory) {
        return ProductDto.builder()
                .productId(product.getProductId())
                .productName(product.getProductName())
                .price(product.getPrice())
                .description(product.getDescription())
                .productImageUrls(product.getProductImageUrls())
                .createdAt(product.getCreatedAt())
                .status(product.getStatus())
                .shopDto(shopInfo)
                .availableQuantity(inventory != null ? inventory.getAvailableQuantity() : 0)
                .build();
    }

    private ProductDto convertToDetailDto(Product product, ShopDto shopInfo, Inventory inventory) {
        return ProductDto.builder()
                .productId(product.getProductId())
                .productName(product.getProductName())
                .price(product.getPrice())
                .description(product.getDescription())
                .productImageUrls(product.getProductImageUrls())
                .createdAt(product.getCreatedAt())
                .status(product.getStatus())
                .shopDto(shopInfo)
                .availableQuantity(inventory != null ? inventory.getAvailableQuantity() : 0)
                .totalQuantity(inventory != null ? inventory.getQuantity() : 0)
                .reservedQuantity(inventory != null ? inventory.getReservedQuantity() : 0)
                .build();
    }

    private Page<ProductDto> convertToBaseDtoPage(Page<Product> productPage, Pageable pageable) {
        // Batch get shop info
        Set<UUID> shopIds = productPage.getContent().stream()
                .map(Product::getShopId)
                .collect(Collectors.toSet());

        Map<UUID, ShopDto> shopInfoMap = new HashMap<>();
        for (UUID shopId : shopIds) {
            shopInfoMap.put(shopId, getShopInfo(shopId));
        }

        // Batch get inventory info
        Set<UUID> productIds = productPage.getContent().stream()
                .map(Product::getProductId)
                .collect(Collectors.toSet());
        Map<UUID, Inventory> inventoryMap = batchGetInventoryInfo(productIds);

        List<ProductDto> dtoList = productPage.getContent().stream()
                .map(product -> convertToBaseDto(
                        product,
                        shopInfoMap.get(product.getShopId()),
                        inventoryMap.get(product.getProductId())))
                .collect(Collectors.toList());

        return new PageImpl<>(dtoList, pageable, productPage.getTotalElements());
    }

    private Page<ProductDto> convertToDetailDtoPage(Page<Product> productPage, Pageable pageable) {
        Set<UUID> shopIds = productPage.getContent().stream()
                .map(Product::getShopId)
                .collect(Collectors.toSet());

        Map<UUID, ShopDto> shopInfoMap = new HashMap<>();
        for (UUID shopId : shopIds) {
            shopInfoMap.put(shopId, getShopInfo(shopId));
        }

        // Batch get inventory info
        Set<UUID> productIds = productPage.getContent().stream()
                .map(Product::getProductId)
                .collect(Collectors.toSet());
        Map<UUID, Inventory> inventoryMap = batchGetInventoryInfo(productIds);

        List<ProductDto> dtoList = productPage.getContent().stream()
                .map(product -> convertToDetailDto(
                        product,
                        shopInfoMap.get(product.getShopId()),
                        inventoryMap.get(product.getProductId())))
                .collect(Collectors.toList());

        return new PageImpl<>(dtoList, pageable, productPage.getTotalElements());
    }
}