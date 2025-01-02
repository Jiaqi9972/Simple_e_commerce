package me.findthepeach.inventoryservice.repository.specification;

import me.findthepeach.common.enums.ProductStatus;
import me.findthepeach.inventoryservice.model.entity.Product;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.UUID;

public class ProductSpecifications {

    // search product by owner
    public static Specification<Product> hasOwnerId(UUID ownerId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("ownerId"), ownerId);
    }

    // search product by status
    public static Specification<Product> hasStatusIn(List<ProductStatus> statuses) {
        return (root, query, criteriaBuilder) ->
                root.get("status").in(statuses);
    }

    // search product by shop
    public static Specification<Product> hasShopId(UUID shopId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("shopId"), shopId);
    }

    // search product by keyword
    public static Specification<Product> nameContains(String keyword) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("productName")),
                        "%" + keyword.toLowerCase() + "%"
                );
    }
}