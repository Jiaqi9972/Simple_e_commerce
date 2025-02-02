package me.findthepeach.userservice.service.impl;

import lombok.extern.slf4j.Slf4j;
import me.findthepeach.common.response.constant.ReturnCode;
import me.findthepeach.common.response.exception.UserException;
import me.findthepeach.common.dto.AddressDto;
import me.findthepeach.userservice.service.AddressService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AddressServiceImpl implements AddressService {

    private final DynamoDbClient dynamoDBClient;
    private final String tableName;

    public AddressServiceImpl(
            @Value("${aws.dynamodb.table.address}") String tableName,
            @Value("${aws.region}") String awsRegion) {
        this.tableName = tableName;
        this.dynamoDBClient = DynamoDbClient.builder()
                .region(Region.of(awsRegion))
                .credentialsProvider(ProfileCredentialsProvider.create("qu9972.2025"))
                .build();
    }


    @Override
    public void addAddress(AddressDto addressDto, UUID userId) {
        log.info("Add address for userId: {}", userId.toString());
        String addressId = UUID.randomUUID().toString();

        Map<String, AttributeValue> item = new HashMap<>();
        item.put("userId", AttributeValue.builder().s(userId.toString()).build());
        item.put("addressId", AttributeValue.builder().s(addressId).build());
        item.put("street", AttributeValue.builder().s(addressDto.getStreet()).build());
        item.put("city", AttributeValue.builder().s(addressDto.getCity()).build());
        item.put("state", AttributeValue.builder().s(addressDto.getState()).build());
        item.put("zipCode", AttributeValue.builder().s(addressDto.getZipCode()).build());
        item.put("isDefault", AttributeValue.builder().bool(addressDto.isDefault()).build());
        item.put("country", AttributeValue.builder().s(addressDto.getCountry()).build());
        item.put("receiver", AttributeValue.builder().s(addressDto.getReceiver()).build());

        System.out.println(item);

        try {

            PutItemRequest putItemRequest = PutItemRequest.builder()
                    .tableName(tableName)
                    .item(item)
                    .build();

            dynamoDBClient.putItem(putItemRequest);
            log.debug("Address added successfully for userId: {}", userId);
        } catch (DynamoDbException e) {
            log.error("Failed to add address for userId: {}", userId, e);
            throw new UserException(ReturnCode.ADDRESS_ADD_FAILED);
        }
    }

    @Override
    public void updateAddress(AddressDto addressDto, UUID userId) {
        log.info("Update address for userId: {}, addressId: {}", userId, addressDto.getAddressId());

        Map<String, AttributeValue> key = Map.of(
                "userId", AttributeValue.builder().s(userId.toString()).build(),
                "addressId", AttributeValue.builder().s(String.valueOf(addressDto.getAddressId())).build()
        );

        Map<String, AttributeValueUpdate> updates = new HashMap<>();
        updates.put("street", AttributeValueUpdate.builder()
                .value(AttributeValue.builder().s(addressDto.getStreet()).build())
                .action(AttributeAction.PUT).build());
        updates.put("city", AttributeValueUpdate.builder()
                .value(AttributeValue.builder().s(addressDto.getCity()).build())
                .action(AttributeAction.PUT).build());
        updates.put("state", AttributeValueUpdate.builder()
                .value(AttributeValue.builder().s(addressDto.getState()).build())
                .action(AttributeAction.PUT).build());
        updates.put("country", AttributeValueUpdate.builder()
                .value(AttributeValue.builder().s(addressDto.getCountry()).build())
                .action(AttributeAction.PUT).build());
        updates.put("zipCode", AttributeValueUpdate.builder()
                .value(AttributeValue.builder().s(addressDto.getZipCode()).build())
                .action(AttributeAction.PUT).build());
        updates.put("receiver", AttributeValueUpdate.builder()
                .value(AttributeValue.builder().s(addressDto.getReceiver()).build())
                .action(AttributeAction.PUT).build());
        updates.put("isDefault", AttributeValueUpdate.builder()
                .value(AttributeValue.builder().bool(addressDto.isDefault()).build())
                .action(AttributeAction.PUT).build());

        try {
            UpdateItemRequest updateRequest = UpdateItemRequest.builder()
                    .tableName(tableName)
                    .key(key)
                    .attributeUpdates(updates)
                    .build();

            dynamoDBClient.updateItem(updateRequest);
            log.info("Address updated successfully for userId: {}", userId);
        } catch (DynamoDbException e) {
            log.error("Failed to update address for userId: {}", userId, e);
            throw new UserException(ReturnCode.ADDRESS_UPDATE_FAILED);
        }
    }

    @Override
    public void deleteAddress(UUID addressId, UUID userId) {
        log.info("Delete address for userId: {}, addressId: {}", userId, addressId);

        Map<String, AttributeValue> key = Map.of(
                "userId", AttributeValue.builder().s(userId.toString()).build(),
                "addressId", AttributeValue.builder().s(addressId.toString()).build()
        );

        try {
            DeleteItemRequest deleteRequest = DeleteItemRequest.builder()
                    .tableName(tableName)
                    .key(key)
                    .build();

            dynamoDBClient.deleteItem(deleteRequest);
            log.info("Address deleted successfully for userId: {}", userId);
        } catch (DynamoDbException e) {
            log.error("Failed to delete address for userId: {}", userId, e);
            throw new UserException(ReturnCode.ADDRESS_DELETE_FAILED);
        }
    }

    @Override
    public Page<AddressDto> getAddresses(UUID userId, int page, int size) {
        log.info("Get addresses for userId: {}", userId);

        QueryRequest queryRequest = QueryRequest.builder()
                .tableName(tableName)
                .keyConditionExpression("userId = :userId")
                .expressionAttributeValues(Map.of(
                        ":userId", AttributeValue.builder().s(userId.toString()).build()))
                .build();

        try {
            QueryResponse response = dynamoDBClient.query(queryRequest);
            List<AddressDto> addresses = response.items().stream()
                    .map(item -> AddressDto.builder()
                            .addressId(UUID.fromString(item.get("addressId").s()))
                            .street(item.get("street").s())
                            .city(item.get("city").s())
                            .state(item.get("state").s())
                            .country(item.get("country").s())
                            .zipCode(item.get("zipCode").s())
                            .isDefault(item.get("isDefault").bool())
                            .receiver(item.get("receiver").s())
                            .build())
                    .collect(Collectors.toList());

            return new PageImpl<>(addresses, PageRequest.of(page, size), addresses.size());
        } catch (DynamoDbException e) {
            log.error("Failed to get addresses for userId: {}", userId, e);
            throw new UserException(ReturnCode.ADDRESS_RETRIEVE_FAILED);
        }
    }
}