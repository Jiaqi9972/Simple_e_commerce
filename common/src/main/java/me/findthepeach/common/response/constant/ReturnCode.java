package me.findthepeach.common.response.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ReturnCode {
    // Common Success (1000-1099)
    SUCCESS(1000, "Operation successful", HttpStatus.OK),

    // User/Auth Domain (2000-2499)
    USER_NOT_FOUND(2000, "User not found", HttpStatus.NOT_FOUND),
    USER_SUB_ALREADY_EXISTS(2001, "User sub already exists", HttpStatus.CONFLICT),
    EMAIL_ALREADY_EXISTS(2002, "User email already exists", HttpStatus.CONFLICT),
    INVALID_ROLE(2003, "Invalid User", HttpStatus.FORBIDDEN ),
    INVALID_ROLE_TYPE(2004, "Invalid role type", HttpStatus.BAD_REQUEST),
    ADDRESS_ADD_FAILED(2005, "Failed to add address", HttpStatus.INTERNAL_SERVER_ERROR),
    ADDRESS_UPDATE_FAILED(2006, "Failed to update address", HttpStatus.INTERNAL_SERVER_ERROR),
    ADDRESS_DELETE_FAILED(2007, "Failed to delete address", HttpStatus.INTERNAL_SERVER_ERROR),
    ADDRESS_RETRIEVE_FAILED(2008, "Failed to retrieve address", HttpStatus.INTERNAL_SERVER_ERROR),
    ILLEGAL_ADDRESS(2009, "Illegal address", HttpStatus.BAD_REQUEST),
    COGNITO_GROUP_ERROR(2010, "Cognito group error", HttpStatus.INTERNAL_SERVER_ERROR),

    // Shop Domain (2500-2999)
    SHOP_NOT_FOUND(2501, "Shop not found", HttpStatus.NOT_FOUND),
    SHOP_NOT_VERIFIED(2502, "Shop not verified" , HttpStatus.FORBIDDEN),
    SHOP_ALREADY_DELETED(2503, "Shop already deleted" , HttpStatus.FORBIDDEN),

    // Inventory Domain (3000-3499)
    PRODUCT_NOT_FOUND(3000, "Product not found", HttpStatus.NOT_FOUND),
    INVENTORY_NOT_FOUND(3001, "Inventory not found", HttpStatus.NOT_FOUND),
    STOCK_NOT_ENOUGH(3002, "Stock not enough", HttpStatus.BAD_REQUEST),

    // Order Domain (4000-4499)
    ORDER_NOT_FOUND(4000, "Order not found", HttpStatus.NOT_FOUND),
    INVALID_ORDER_STATUS(4001, "Invalid order status", HttpStatus.BAD_REQUEST),

    // System Errors (5000-5499)
    INTERNAL_ERROR(5000, "Internal server error", HttpStatus.INTERNAL_SERVER_ERROR),

    // Database Errors (5100-5199)
    DATABASE_ERROR(5100, "Database operation failed", HttpStatus.INTERNAL_SERVER_ERROR),
    DATABASE_TIMEOUT(5101, "Database operation timeout", HttpStatus.INTERNAL_SERVER_ERROR),
    DATA_INTEGRITY_ERROR(5102, "Data integrity violation", HttpStatus.INTERNAL_SERVER_ERROR),

    // Authentication Errors (5200-5299)
    UNAUTHORIZED(5200, "Unauthorized", HttpStatus.UNAUTHORIZED),
    TOKEN_EXPIRED(5201, "Token expired", HttpStatus.UNAUTHORIZED),
    INVALID_TOKEN(5202, "Invalid token", HttpStatus.UNAUTHORIZED),
    INSUFFICIENT_PERMISSIONS(5203, "Insufficient permissions", HttpStatus.UNAUTHORIZED),

    // External Service Errors (5300-5399)
    EXTERNAL_SERVICE_ERROR(5300, "External service error", HttpStatus.INTERNAL_SERVER_ERROR),
    SERVICE_TIMEOUT(5301, "Service call timeout", HttpStatus.INTERNAL_SERVER_ERROR),

    // Validation Errors (5400-5499)
    INVALID_PARAMETER(5400, "Invalid parameter", HttpStatus.BAD_REQUEST);

    private final int code;
    private final String message;
    private final HttpStatus httpStatus;
}